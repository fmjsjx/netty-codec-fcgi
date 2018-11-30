package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;
import java.util.List;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.EncoderException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

@Sharable
public final class FCGIResponseEncoder extends FCGIRecordEncoder<FCGIMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, FCGIMessage msg, List<Object> out) throws Exception {
		if (msg instanceof FullFCGIResponse) {
			encodeResponse(ctx, (FullFCGIResponse) msg, out);
		} else {
			encordRecord(ctx, (FCGIRecord) msg, out);
		}
	}

	private void encodeResponse(ChannelHandlerContext ctx, FullFCGIResponse response, List<Object> out) {
		if (response.hasStderr()) {
			encodeContentHolder(ctx, response.stderr(), out, false);
		}
		encodeContentHolder(ctx, response.stdout(), out, false);
		encodeEndRequest(ctx, response.endRequest(), out);
	}

	private void encodeEndRequest(ChannelHandlerContext ctx, FCGIEndRequest endRequest, List<Object> out) {
		ByteBuf buf = ctx.alloc().buffer(FCGI_HEADER_LEN + FCGI_END_REQUEST_CONTENT_LENGTH);
		encodeEndRequest(endRequest, buf);
		out.add(buf);
	}

	private void encordRecord(ChannelHandlerContext ctx, FCGIRecord record, List<Object> out) {
		FCGIHeader header = record.header();
		int type = header.type();
		// only encode request messages
		if (type == FCGI_END_REQUEST) {
			encodeEndRequest(ctx, (FCGIEndRequest) record, out);
		} else if (type == FCGI_STDOUT) {
			encodeContentHolder(ctx, (FCGIStdout) record, out, false);
		} else if (type == FCGI_STDERR) {
			encodeContentHolder(ctx, (FCGIStderr) record, out, false);
		} else if (type == FCGI_GET_VALUES_RESULT) {
			ByteBuf buf = ctx.alloc().buffer();
			encodeNameValuePairs((FCGIGetValuesResult) record, buf);
			out.add(buf);
		} else if (type == FCGI_UNKNOWN_TYPE) {
			ByteBuf buf = ctx.alloc().buffer(FCGI_HEADER_LEN + FCGI_UNKNOWN_TYPE_CONTENT_LENGTH);
			encodeUnknownType((FCGIUnknownType) record, buf);
			out.add(buf);
		} else {
			throw new EncoderException("no request message type " + type);
		}
	}

}
