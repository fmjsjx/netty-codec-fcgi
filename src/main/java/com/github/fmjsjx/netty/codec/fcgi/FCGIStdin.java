package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;
import io.netty.buffer.ByteBuf;

public final class FCGIStdin extends AbstractFCGIContentHolder {

	public FCGIStdin(FCGIHeader header, ByteBuf content) {
		super(header, content);
	}

	public FCGIStdin(ByteBuf content) {
		this(new DefaultFCGIHeader(FCGI_STDIN), content);
	}

}
