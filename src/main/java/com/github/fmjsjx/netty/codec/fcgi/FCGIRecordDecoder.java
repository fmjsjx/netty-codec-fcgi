package com.github.fmjsjx.netty.codec.fcgi;

import java.util.List;
import java.util.Queue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

public abstract class FCGIRecordDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		Object decoded = decode(ctx, in);
		if (decoded != null) {
			out.add(decoded);
		}
	}

	protected abstract Object decode(ChannelHandlerContext ctx, ByteBuf in);

	protected FCGIHeader decodeHeader(ByteBuf in, int contentLength, int paddingLength) {
		byte version = in.readByte();
		byte type = in.readByte();
		int requestId = in.readUnsignedShort();
		in.skipBytes(4);
		DefaultFCGIHeader header = new DefaultFCGIHeader(version, type);
		header.requestId(requestId);
		header.contentLength(contentLength);
		header.paddingLength(paddingLength);
		return header;
	}

	protected int getContentLength(ByteBuf in, int readerIndex) {
		return in.getUnsignedShort(readerIndex + 4);
	}

	protected int getPaddingLength(ByteBuf in, int readerIndex) {
		return in.getUnsignedByte(readerIndex + 6);
	}

	protected void skipBytes(ByteBuf in, int length) {
		if (length > 0) {
			in.skipBytes(length);
		}
	}

	protected ByteBuf compose(Queue<ByteBuf> bufs) {
		int size = bufs.size();
		if (size == 1) {
			return bufs.element();
		} else if (size == 0) {
			return Unpooled.EMPTY_BUFFER;
		} else {
			return Unpooled.wrappedBuffer(bufs.stream().filter(ByteBuf::isReadable).toArray(ByteBuf[]::new));
		}
	}

	protected FCGIBeginRequest decodeBeginRequest(FCGIHeader header, ByteBuf in, int contentLength, int paddingLength) {
		FCGIBeginRequest beginRequest = new FCGIBeginRequest(header);
		beginRequest.role(in.readUnsignedShort());
		beginRequest.flags(in.readByte());
		in.skipBytes(contentLength - 3);
		skipBytes(in, paddingLength);
		return beginRequest;
	}

	protected FCGIAbortRequest decodeAbortRequest(FCGIHeader header, ByteBuf in, int contentLength, int paddingLength) {
		skipBytes(in, paddingLength);
		return new FCGIAbortRequest(header);
	}

	protected FCGIEndRequest decodeEndRequest(FCGIHeader header, ByteBuf in, int contentLength, int paddingLength) {
		FCGIEndRequest endRequest = new FCGIEndRequest(header);
		endRequest.appStatus(in.readInt());
		endRequest.protocolStatus(in.readByte());
		in.skipBytes(contentLength - 5);
		skipBytes(in, paddingLength);
		return endRequest;
	}

	protected FCGIGetValues decodeGetValues(FCGIHeader header, ByteBuf in, int contentLength, int paddingLength) {
		FCGIGetValues getValues = new FCGIGetValues(header);
		decodeNameValuePairs(getValues, in.readSlice(contentLength));
		skipBytes(in, paddingLength);
		return getValues;
	}

	protected FCGIGetValuesResult decodeGetValuesResult(FCGIHeader header, ByteBuf in, int contentLength,
			int paddingLength) {
		FCGIGetValuesResult getValuesResult = new FCGIGetValuesResult(header);
		decodeNameValuePairs(getValuesResult, in.readSlice(contentLength));
		skipBytes(in, paddingLength);
		return getValuesResult;
	}

	protected void decodeNameValuePairs(FCGINameValuePairs nameValuePairs, ByteBuf in) {
		for (; in.isReadable();) {
			int nameLength = readVariableLength(in);
			int valueLength = readVariableLength(in);
			CharSequence name = in.readCharSequence(nameLength, CharsetUtil.UTF_8);
			CharSequence value = in.readCharSequence(valueLength, CharsetUtil.UTF_8);
			nameValuePairs.addPair(name, value);
		}
	}

	protected int readVariableLength(ByteBuf in) {
		int length = in.getByte(in.readerIndex());
		if (length < 0) {
			return in.readInt() & 0x7fffffff;
		} else {
			in.skipBytes(1);
			return length;
		}
	}

	protected FCGIUnknownType decodeUnknownType(FCGIHeader header, ByteBuf in, int contentLength, int paddingLength) {
		FCGIUnknownType unknownType = new FCGIUnknownType(header);
		unknownType.type(in.readByte());
		in.skipBytes(contentLength - 1);
		skipBytes(in, paddingLength);
		return unknownType;
	}

}
