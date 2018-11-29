package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;

public final class FCGIUnknownType extends AbstractFCGIRecord {

	private byte type;

	public FCGIUnknownType(FCGIHeader header) {
		super(header);
	}

	public FCGIUnknownType(byte type) {
		this(new DefaultFCGIHeader(FCGI_UNKNOWN_TYPE));
		type(type);
	}

	public byte type() {
		return type;
	}

	public FCGIUnknownType type(byte type) {
		this.type = type;
		return this;
	}

	@Override
	public String toString() {
		return "FCGIUnknownType[header=" + header() + ", type=" + type + "]";
	}

}
