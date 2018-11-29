package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;

public final class FCGIBeginRequest extends AbstractFCGIRecord {

	private int role;
	private byte flags;

	public FCGIBeginRequest(FCGIHeader header) {
		super(header);
	}

	public FCGIBeginRequest() {
		this(new DefaultFCGIHeader(FCGI_BEGIN_REQUEST));
	}

	public int role() {
		return role;
	}

	public void role(int role) {
		this.role = role;
	}

	public byte flags() {
		return flags;
	}

	public void flags(byte flags) {
		this.flags = flags;
	}

	public boolean isKeepConn() {
		return flags == FCGI_KEEP_CONN;
	}

	public void setKeepConn(boolean keepConn) {
		flags = keepConn ? FCGI_KEEP_CONN : 0;
	}

	@Override
	public String toString() {
		return "FCGIBeginRequest[header=" + header + ", role=" + role + ", flags=" + flags + "]";
	}

}
