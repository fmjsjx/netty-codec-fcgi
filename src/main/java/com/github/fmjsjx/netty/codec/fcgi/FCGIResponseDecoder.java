package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;
import java.util.LinkedList;
import java.util.Objects;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;

public final class FCGIResponseDecoder extends FCGIRecordDecoder {

	private final FCGIServiceMode mode;

	private ResponseBuf responseBuf;
	private ResponseBuf[] responseBufs;

	public FCGIResponseDecoder() {
		this(FCGIServiceMode.SIMPLE);
	}

	public FCGIResponseDecoder(FCGIServiceMode mode) {
		this.mode = Objects.requireNonNull(mode, "mode");
		if (mode == FCGIServiceMode.COMPLEX) {
			responseBufs = new ResponseBuf[FCGI_REQUEST_ID_LIMIT];
		}
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
		int type = header.type();
		int requestId = header.requestId();
		// switch type
		if (type == FCGI_STDOUT) {
			ResponseBuf buf = ensureResponseBuf(header);
			if (contentLength == 0) {
				// end stdout, ignore
			} else {
				ByteBuf content = in.readRetainedSlice(contentLength);
				buf.stdoutBufs.add(content);
			}
			skipBytes(in, paddingLength);
			return null;
		} else if (type == FCGI_END_REQUEST) {
			ResponseBuf buf = getResponseBuf(header);
			if (buf == null) {
				throw new DecoderException("FCGI_END_REQUEST without STDOUT");
			}
			FCGIEndRequest endRequest = decodeEndRequest(header, in, contentLength, paddingLength);
			FullFCGIResponse response = buf.toResponse(requestId, endRequest);
			if (mode == FCGIServiceMode.COMPLEX) {
				responseBufs[requestId] = null;
			}
			return response;
		} else if (type == FCGI_STDERR) {
			ResponseBuf buf = ensureResponseBuf(header);
			if (buf.stderrBufs == null) {
				buf.stderrBufs = new LinkedList<>();
			}
			if (contentLength == 0) {
				// end stderr, ignore
			} else {
				ByteBuf content = in.readRetainedSlice(contentLength);
				buf.stderrBufs.add(content);
			}
			skipBytes(in, paddingLength);
			return null;
		} else if (type == FCGI_GET_VALUES_RESULT) {
			return decodeGetValuesResult(header, in, contentLength, paddingLength);
		} else if (type == FCGI_UNKNOWN_TYPE) {
			FCGIUnknownType unknownType = decodeUnknownType(header, in, contentLength, paddingLength);
			ctx.fireExceptionCaught(new FCGIUnknownTypeException(unknownType.getRequestId(), unknownType.type()));
			return null;
		} else {
			throw new DecoderException("not response type " + type);
		}
	}

	private ResponseBuf getResponseBuf(FCGIHeader header) {
		if (mode == FCGIServiceMode.SIMPLE) {
			return responseBuf;
		} else {
			return responseBufs[header.requestId()];
		}
	}

	private ResponseBuf ensureResponseBuf(FCGIHeader header) {
		if (mode == FCGIServiceMode.SIMPLE) {
			if (responseBuf == null) {
				responseBuf = new ResponseBuf();
			}
			return responseBuf;
		} else {
			int index = header.requestId();
			if (responseBufs[index] == null) {
				responseBufs[index] = new ResponseBuf();
			}
			return responseBufs[index];
		}
	}

	private final class ResponseBuf {

		private LinkedList<ByteBuf> stdoutBufs = new LinkedList<>();
		private LinkedList<ByteBuf> stderrBufs;

		private FullFCGIResponse toResponse(int requestId, FCGIEndRequest endRequest) {
			DefaultFullFCGIResponse response = new DefaultFullFCGIResponse(requestId);
			response.endRequest(endRequest);
			if (stderrBufs != null) {
				ByteBuf content = compose(stderrBufs);
				stderrBufs = null;
				FCGIStderr stderr = new FCGIStderr(content);
				FCGIMessageUtil.setRequestId(stderr, requestId);
				response.stderr(stderr);
			}
			ByteBuf content = compose(stdoutBufs);
			stdoutBufs = null;
			FCGIStdout stdout = new FCGIStdout(content);
			FCGIMessageUtil.setRequestId(stdout, requestId);
			response.stdout(stdout);
			return response;
		}

	}

}
