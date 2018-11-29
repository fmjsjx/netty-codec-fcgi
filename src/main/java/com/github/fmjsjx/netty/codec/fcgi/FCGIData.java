package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;
import io.netty.buffer.ByteBuf;

public final class FCGIData extends AbstractFCGIContentHolder {

	public FCGIData(FCGIHeader header, ByteBuf content) {
		super(header, content);
	}

	public FCGIData(ByteBuf content) {
		super(new DefaultFCGIHeader(FCGI_DATA), content);
	}

}
