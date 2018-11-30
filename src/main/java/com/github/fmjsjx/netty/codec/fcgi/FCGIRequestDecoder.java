package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;
import java.util.LinkedList;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.util.CharsetUtil;

public final class FCGIRequestDecoder extends FCGIRecordDecoder {

	private static final ByteBuf defaultAbortContent;
	static {
		ByteBuf buf = Unpooled.directBuffer();
		String javaVersion = System.getProperty("java.version").split("_", 2)[0];
		buf.writeCharSequence(
				"X-Powered-By: JAVA/" + javaVersion + "\r\nContent-type: text/plain; charset=UTF-8\r\n\r\n",
				CharsetUtil.UTF_8);
		defaultAbortContent = Unpooled.unreleasableBuffer(buf);
	}

	private FCGIServiceMode mode;
	private RequestBuf requestBuf;
	private RequestBuf[] requestBufs;

	private final ByteBuf abortContent;

	public FCGIRequestDecoder(ByteBuf abortContent) {
		this.abortContent = abortContent;
	}

	public FCGIRequestDecoder() {
		this(defaultAbortContent);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		abortContent.release();
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) {
		if (in.readableBytes() < FCGI_HEADER_LEN) {
			return null;
		}
		int readerIndex = in.readerIndex();
		int contentLength = getContentLength(in, readerIndex);
		int paddingLength = getPaddingLength(in, readerIndex);
		int frameLength = contentLength + paddingLength + FCGI_HEADER_LEN;
		if (in.readableBytes() < frameLength) {
			return null;
		}
		FCGIHeader header = decodeHeader(in, contentLength, paddingLength);
		byte type = header.type();
		int requestId = header.requestId();
		if (type == FCGI_BEGIN_REQUEST) {
			FCGIBeginRequest beginRequest = decodeBeginRequest(header, in, contentLength, paddingLength);
			// deside mode at first FCGI_BEGIN_REQUEST reached
			if (mode == null) {
				if (beginRequest.isKeepConn()) {
					mode = FCGIServiceMode.COMPLEX;
					requestBufs = new RequestBuf[FCGI_REQUEST_ID_LIMIT];
				} else {
					mode = FCGIServiceMode.SIMPLE;
				}
			}
			initialRequestBuf(ctx, requestId, beginRequest);
		} else if (type == FCGI_PARAMS) {
			RequestBuf buf = getRequestBuf(requestId);
			if (buf != null) {
				if (contentLength > 0) {
					buf.paramsContentBufs.add(in.readRetainedSlice(contentLength));
					skipBytes(in, paddingLength);
				}
			} else {
				skipBytes(in, contentLength);
				skipBytes(in, paddingLength);
			}
		} else if (type == FCGI_STDIN) {
			RequestBuf buf = getRequestBuf(requestId);
			if (buf != null) {
				if (contentLength == 0) {
					// end stdin, return full request
					return buf.toRequest();
				} else {
					if (buf.stdinBufs == null) {
						buf.stdinBufs = new LinkedList<>();
					}
					buf.stdinBufs.add(in.readRetainedSlice(contentLength));
					skipBytes(in, paddingLength);
				}
			} else {
				skipBytes(in, contentLength);
				skipBytes(in, paddingLength);
			}
		} else if (type == FCGI_DATA) {
			RequestBuf buf = getRequestBuf(requestId);
			if (buf != null) {
				if (contentLength > 0) {
					if (buf.dataBufs == null) {
						buf.dataBufs = new LinkedList<>();
					}
					buf.dataBufs.add(in.readRetainedSlice(contentLength));
					skipBytes(in, paddingLength);
				}
			} else {
				skipBytes(in, contentLength);
				skipBytes(in, paddingLength);
			}
		} else if (type == FCGI_ABORT_REQUEST) {
			FCGIAbortRequest abortRequest = decodeAbortRequest(header, in, contentLength, paddingLength);
			RequestBuf buf = getRequestBuf(requestId);
			if (buf != null) {
				DefaultFullFCGIResponse response = new DefaultFullFCGIResponse(requestId);
				response.stdout(abortContent.retainedDuplicate());
				response.endRequest(0);
				ChannelFuture future = ctx.writeAndFlush(response);
				if (mode == FCGIServiceMode.SIMPLE) {
					future.addListener(ChannelFutureListener.CLOSE);
				} else {
					if (requestBufs != null) {
						requestBufs[requestId] = null;
					}
				}
			} else {
				return abortRequest;
			}
		} else if (type == FCGI_GET_VALUES) {
			return decodeGetValues(header, in, contentLength, paddingLength);
		} else if (type >= FCGI_MAXTYPE) {
			FCGIUnknownType unknownType = new FCGIUnknownType(type);
			ChannelFuture future = ctx.writeAndFlush(unknownType);
			if (mode == FCGIServiceMode.SIMPLE) {
				future.addListener(ChannelFutureListener.CLOSE);
			}
		} else {
			throw new DecoderException("not request type " + type);
		}
		return null;
	}

	private RequestBuf initialRequestBuf(ChannelHandlerContext ctx, int requestId, FCGIBeginRequest beginRequest) {
		if (mode == FCGIServiceMode.SIMPLE) {
			if (requestBuf == null) {
				requestBuf = new RequestBuf(beginRequest);
				return requestBuf;
			} else {
				// ignore
				return null;
			}
		} else {
			RequestBuf buf = requestBufs[requestId];
			if (buf == null) {
				buf = new RequestBuf(beginRequest);
				requestBufs[requestId] = buf;
			}
			return buf;
		}
	}

	private RequestBuf getRequestBuf(int requestId) {
		if (mode == FCGIServiceMode.SIMPLE) {
			if (requestId == requestBuf.beginRequest.getRequestId()) {
				return requestBuf;
			}
		} else {
			if (requestBufs != null) {
				return requestBufs[requestId];
			}
		}
		return null;
	}

	private final class RequestBuf {

		private final FCGIBeginRequest beginRequest;

		private RequestBuf(FCGIBeginRequest beginRequest) {
			this.beginRequest = beginRequest;
		}

		private LinkedList<ByteBuf> paramsContentBufs = new LinkedList<>();
		private LinkedList<ByteBuf> stdinBufs;
		private LinkedList<ByteBuf> dataBufs;

		private FullFCGIRequest toRequest() {
			int requestId = beginRequest.getRequestId();
			DefaultFullFCGIRequest request = new DefaultFullFCGIRequest(beginRequest);
			// params
			FCGIParams params = new FCGIParams();
			FCGIMessageUtil.setRequestId(params, requestId);
			ByteBuf in = compose(paramsContentBufs);
			paramsContentBufs = null;
			decodeNameValuePairs(params, in);
			// data
			if (dataBufs != null) {
				request.data(compose(dataBufs));
				dataBufs = null;
			}
			// stdin
			if (stdinBufs != null) {
				request.stdin(compose(stdinBufs));
				stdinBufs = null;
			}
			return request;
		}

	}

}
