package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;
import static com.github.fmjsjx.netty.codec.fcgi.FCGIMessageUtil.*;
import static io.netty.buffer.Unpooled.*;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

public abstract class FCGIRecordEncoder<I> extends MessageToMessageEncoder<I> {

	private static final int MASK_4_BYTES_LENGTH = 0x80000000;

	private static final ByteBuf[] PADDING_BUFS = new ByteBuf[8];

	static {
		for (int i = 1; i < PADDING_BUFS.length; i++) {
			PADDING_BUFS[i] = unreleasableBuffer(directBuffer(i).writeZero(i));
		}
	}

	protected ByteBuf paddingBuf(int paddingLength) {
		return PADDING_BUFS[paddingLength];
	}

	protected void encodeBeginRequest(FCGIBeginRequest beginRequest, ByteBuf buf) {
		setContentAndPaddingLength(beginRequest, FCGI_BEGIN_REQUEST_CONTENT_LENGTH, 0);
		encodeHeader(beginRequest, buf);
		buf.writeShort(beginRequest.role()).writeByte(beginRequest.flags()).writeZero(5);
	}

	protected void encodeHeader(FCGIRecord record, ByteBuf buf) {
		encodeHeader(record.header(), buf);
	}

	protected void encodeHeader(FCGIHeader header, ByteBuf buf) {
		encodeHeader(header, buf, header.contentLength(), header.paddingLength());
	}

	protected void encodeHeader(FCGIHeader header, ByteBuf buf, int contentLength, int paddingLength) {
		buf.writeByte(header.version()).writeByte(header.type()).writeShort(header.requestId())
				.writeShort(contentLength).writeByte(paddingLength).writeZero(1);
	}

	protected void encodeNameValuePairs(FCGINameValuePairs nameValuePairs, ByteBuf buf) {
		encodeNameValuePairs(nameValuePairs, buf, true);
	}

	protected void encodeNameValuePairs(FCGINameValuePairs nameValuePairs, ByteBuf buf, boolean writeEndBlock) {
		int writerIndex = buf.writerIndex();
		FCGIHeader header = nameValuePairs.header();
		buf.writeZero(FCGI_HEADER_LEN);
		int contentLength = 0;
		int limit = FCGI_MAX_CONTENT_LENGTH;
		for (FCGINameValuePair pair : nameValuePairs.pairs()) {
			contentLength += encodeNameValuePair(pair, buf);
			if (contentLength > limit) {
				buf.ensureWritable(FCGI_HEADER_LEN + 1);
				int w = buf.writerIndex();
				buf.writerIndex(writerIndex);
				header.contentLength(limit).paddingLength(1);
				encodeHeader(header, buf);
				writerIndex = writerIndex + FCGI_HEADER_LEN + limit;
				contentLength = w - writerIndex;
				// move remaining contents to next block
				for (int i = w - 1; i >= writerIndex; i--) {
					buf.setByte(i + FCGI_HEADER_LEN + 1, buf.getByte(i));
				}
				buf.setByte(writerIndex++, 0); // set padding
				buf.writerIndex(w + FCGI_HEADER_LEN + 1);
			}
		}
		buf.markWriterIndex();
		buf.writerIndex(writerIndex);
		setContentLength(nameValuePairs, contentLength);
		encodeHeader(header, buf);
		buf.resetWriterIndex();
		buf.writeZero(header.paddingLength());
		if (writeEndBlock) {
			encodeHeader(header, buf, 0, 0);
		}
	}

	protected int encodeNameValuePair(FCGINameValuePair pair, ByteBuf buf) {
		CharSequence name = pair.name();
		CharSequence value = pair.value();
		int beginIndex = buf.writerIndex();
		if (name instanceof AsciiString) {
			int nameLength = name.length();
			writeVariableLength(nameLength, buf);
			if (value instanceof AsciiString) {
				int valueLength = value.length();
				writeVariableLength(valueLength, buf);
				ByteBufUtil.copy((AsciiString) name, 0, buf, nameLength);
				ByteBufUtil.copy((AsciiString) value, 0, buf, valueLength);
			} else {
				buf.markWriterIndex();
				int valueLength = value.length();
				writeVariableLength(valueLength, buf);
				ByteBufUtil.copy((AsciiString) name, 0, buf, nameLength);
				int len = buf.writeCharSequence(value, CharsetUtil.UTF_8);
				if (len != valueLength) {
					if (len > 127 && valueLength <= 127) {
						buf.resetWriterIndex();
						writeVariableLength(len, buf);
						ByteBufUtil.copy((AsciiString) name, 0, buf, nameLength);
						buf.writeCharSequence(value, CharsetUtil.UTF_8);
					} else {
						int wi = buf.writerIndex();
						buf.resetWriterIndex();
						writeVariableLength(len, buf);
						buf.writerIndex(wi);
					}
				}
			}
		} else {
			int nameLength = name.length();
			writeVariableLength(nameLength, buf);
			if (value instanceof AsciiString) {
				int valueLength = value.length();
				writeVariableLength(valueLength, buf);
				int len = buf.writeCharSequence(name, CharsetUtil.UTF_8);
				if (len != nameLength) {
					if (len > 127 && nameLength <= 127) {
						buf.writerIndex(beginIndex);
						writeVariableLength(len, buf);
						buf.writeCharSequence(name, CharsetUtil.UTF_8);
					} else {
						int wr = buf.writerIndex();
						buf.writerIndex(beginIndex);
						writeVariableLength(len, buf);
						buf.writerIndex(wr);
					}
				}
				ByteBufUtil.copy((AsciiString) value, 0, buf, valueLength);
			} else {
				int valueLength = value.length();
				int valueLengthIndex = buf.writerIndex();
				writeVariableLength(valueLength, buf);
				int len = buf.writeCharSequence(name, CharsetUtil.UTF_8);
				if (len != nameLength) {
					if (len > 127 && nameLength <= 127) {
						buf.writerIndex(beginIndex);
						writeVariableLength(len, buf);
						valueLengthIndex = buf.writerIndex();
						writeVariableLength(valueLengthIndex, buf);
						buf.writeCharSequence(name, CharsetUtil.UTF_8);

					} else {
						int wi = buf.writerIndex();
						buf.writerIndex(beginIndex);
						writeVariableLength(len, buf);
						buf.writerIndex(wi);
					}
				}
				int valLen = buf.writeCharSequence(value, CharsetUtil.UTF_8);
				if (valLen != valueLength) {
					if (valLen > 127 && valueLength <= 127) {
						buf.writerIndex(valueLengthIndex);
						writeVariableLength(valLen, buf);
						buf.writeCharSequence(value, CharsetUtil.UTF_8);
					} else {
						int wi = buf.writerIndex();
						buf.writerIndex(valueLengthIndex);
						writeVariableLength(valLen, buf);
						buf.writerIndex(wi);
					}
				}
			}
		}
		return buf.writerIndex() - beginIndex;
	}

	protected void writeVariableLength(int length, ByteBuf buf) {
		if (length <= 127) {
			buf.writeByte(length);
		} else {
			buf.writeInt(length | MASK_4_BYTES_LENGTH);
		}
	}

	protected void encodeContentHolder(ChannelHandlerContext ctx, FCGIContentHolder contentHolder, List<Object> out) {
		encodeContentHolder(ctx, contentHolder, out, true);
	}

	protected void encodeContentHolder(ChannelHandlerContext ctx, FCGIContentHolder contentHolder, List<Object> out,
			boolean writeEndBlock) {
		ByteBuf content = contentHolder.content();
		FCGIHeader header = contentHolder.header();
		int contentLength = content.readableBytes();
		for (; contentLength > FCGI_MAX_CONTENT_LENGTH;) {
			header.contentLength(FCGI_MAX_CONTENT_LENGTH).paddingLength(1);
			ByteBuf buf = ctx.alloc().buffer(FCGI_HEADER_LEN);
			encodeHeader(header, buf);
			out.add(buf);
			ByteBuf sub = content.readRetainedSlice(FCGI_MAX_CONTENT_LENGTH);
			out.add(sub);
			contentLength -= FCGI_MAX_CONTENT_LENGTH;
		}
		if (contentLength > 0) {
			setContentLength(contentHolder, contentLength);
			ByteBuf buf = ctx.alloc().buffer(FCGI_HEADER_LEN);
			encodeHeader(header, buf);
			out.add(buf);
			out.add(content.retain());
			if (header.paddingLength() > 0) {
				out.add(paddingBuf(header.paddingLength()).duplicate());
			}
		}
		if (writeEndBlock) {
			ByteBuf buf = ctx.alloc().buffer(FCGI_HEADER_LEN);
			encodeHeader(header, buf, 0, 0);
			out.add(buf);
		}
	}

	protected void encodeAbortRequest(FCGIAbortRequest abortRequest, ByteBuf buf) {
		encodeHeader(abortRequest.header(), buf, 0, 0);
	}

	protected void encodeEndRequest(FCGIEndRequest endRequest, ByteBuf buf) {
		setContentAndPaddingLength(endRequest, FCGI_END_REQUEST_CONTENT_LENGTH, 0);
		encodeHeader(endRequest, buf);
		buf.writeInt(endRequest.appStatus()).writeByte(endRequest.protocolStatus()).writeZero(3);
	}

	protected void encodeUnknownType(FCGIUnknownType unknownType, ByteBuf buf) {
		setContentAndPaddingLength(unknownType, FCGI_UNKNOWN_TYPE_CONTENT_LENGTH, 0);
		encodeHeader(unknownType, buf);
		buf.writeByte(unknownType.type()).writeZero(7);
	}

}
