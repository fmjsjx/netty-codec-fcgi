package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;

@Sharable
public final class FCGIRequestEncoder extends FCGIRecordEncoder<FCGIMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, FCGIMessage msg, List<Object> out) throws Exception {
		if (msg instanceof FullFCGIRequest) {
			encodeRequest(ctx, out, (FullFCGIRequest) msg);
		} else {
			encordRecord(ctx, (FCGIRecord) msg, out);
		}
	}

	private void encodeRequest(ChannelHandlerContext ctx, List<Object> out, FullFCGIRequest request) {
		ByteBuf buf = ctx.alloc().buffer();
		// encode FCGI_BEGIN_REQUEST
		encodeBeginRequest(request.beginRequest(), buf);
		// encode FCGI_PARAMS
		encodeNameValuePairs(request.params(), buf);
		// encode FCGI_STDIN
		out.add(buf);
		// encode FCGI_DATA if exists
		if (request.hasData()) {
			encodeContentHolder(ctx, request.data(), out);
		}
		// encode FCGI_STDIN
		FCGIStdin stdin = request.stdin();
		if (stdin == null) {
			stdin = new FCGIStdin(Unpooled.EMPTY_BUFFER);
		}
		encodeContentHolder(ctx, stdin, out);
	}

	private void encordRecord(ChannelHandlerContext ctx, FCGIRecord record, List<Object> out) {
		FCGIHeader header = record.header();
		int type = header.type();
		// only encode request messages
		if (type == FCGI_BEGIN_REQUEST) {
			ByteBuf buf = ctx.alloc().buffer(FCGI_HEADER_LEN + FCGI_BEGIN_REQUEST_CONTENT_LENGTH);
			encodeBeginRequest((FCGIBeginRequest) record, buf);
			out.add(buf);
		} else if (type == FCGI_PARAMS) {
			ByteBuf buf = ctx.alloc().buffer();
			encodeNameValuePairs((FCGIParams) record, buf);
			out.add(buf);
		} else if (type == FCGI_STDIN) {
			encodeContentHolder(ctx, (FCGIStdin) record, out);
		} else if (type == FCGI_ABORT_REQUEST) {
			ByteBuf buf = ctx.alloc().buffer(FCGI_HEADER_LEN);
			encodeAbortRequest((FCGIAbortRequest) record, buf);
			out.add(buf);
		} else if (type == FCGI_DATA) {
			encodeContentHolder(ctx, (FCGIData) record, out);
		} else if (type == FCGI_GET_VALUES) {
			ByteBuf buf = ctx.alloc().buffer();
			encodeNameValuePairs((FCGIGetValues) record, buf, false);
			out.add(buf);
		} else {
			throw new EncoderException("no request message type " + type);
		}
	}

}
