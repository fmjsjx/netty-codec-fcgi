package com.github.fmjsjx.netty.codec.fcgi;

import io.netty.buffer.ByteBuf;

public final class FCGIResponseUtil {

	public static final FCGIResponseHeaders decodeHeaders(FCGIStdout stdout) {
		return decodeHeaders(stdout.content());
	}

	public static final FCGIResponseHeaders decodeHeaders(ByteBuf content) {
		return new FCGIResponseHeaders(content);
	}

	private FCGIResponseUtil() {
	}

}
