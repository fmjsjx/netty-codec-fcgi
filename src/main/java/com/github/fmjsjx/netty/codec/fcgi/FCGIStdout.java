package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;
import io.netty.buffer.ByteBuf;

public final class FCGIStdout extends AbstractFCGIContentHolder {

	public FCGIStdout(FCGIHeader header, ByteBuf content) {
		super(header, content);
	}

	public FCGIStdout(ByteBuf content) {
		this(new DefaultFCGIHeader(FCGI_STDOUT), content);
	}

}
