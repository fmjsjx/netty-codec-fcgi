package com.github.fmjsjx.netty.codec.fcgi;

import java.util.Objects;

public abstract class AbstractFCGIRecord implements FCGIRecord {

	protected final FCGIHeader header;

	public AbstractFCGIRecord(FCGIHeader header) {
		this.header = Objects.requireNonNull(header, "header");
	}

	@Override
	public FCGIHeader header() {
		return header;
	}

}
