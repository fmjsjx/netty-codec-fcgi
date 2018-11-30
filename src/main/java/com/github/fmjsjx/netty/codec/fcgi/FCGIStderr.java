package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;
import io.netty.buffer.ByteBuf;

public final class FCGIStderr extends AbstractFCGIContentHolder {

	public FCGIStderr(FCGIHeader header, ByteBuf content) {
		super(header, content);
	}

	public FCGIStderr(ByteBuf content) {
		this(new DefaultFCGIHeader(FCGI_STDERR), content);
	}

}
