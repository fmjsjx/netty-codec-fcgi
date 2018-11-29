package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;

public final class FCGIAbortRequest extends AbstractFCGIRecord {

	public FCGIAbortRequest(FCGIHeader header) {
		super(header);
	}

	public FCGIAbortRequest() {
		this(new DefaultFCGIHeader(FCGI_ABORT_REQUEST));
	}

	@Override
	public String toString() {
		return "FCGIAbortRequest[header=" + header() + "]";
	}

}
