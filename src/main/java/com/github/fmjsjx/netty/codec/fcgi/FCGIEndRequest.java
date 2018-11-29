package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;

public final class FCGIEndRequest extends AbstractFCGIRecord {

	private int appStatus;
	private byte protocolStatus;

	public FCGIEndRequest(FCGIHeader header) {
		super(header);
	}

	public FCGIEndRequest() {
		this(new DefaultFCGIHeader(FCGI_END_REQUEST));
	}

	public int appStatus() {
		return appStatus;
	}

	public FCGIEndRequest appStatus(int appStatus) {
		this.appStatus = appStatus;
		return this;
	}

	public byte protocolStatus() {
		return protocolStatus;
	}

	public FCGIEndRequest protocolStatus(byte protocolStatus) {
		this.protocolStatus = protocolStatus;
		return this;
	}

	@Override
	public String toString() {
		return "FCGIEndRequest[header=" + header + ", appStatus=" + appStatus + ", protocolStatus=" + protocolStatus
				+ "]";
	}

}
