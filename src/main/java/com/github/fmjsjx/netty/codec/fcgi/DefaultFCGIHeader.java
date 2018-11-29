package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;

public class DefaultFCGIHeader implements FCGIHeader {

	private final byte version;
	private final byte type;
	private int requestId;
	private int contentLength;
	private int paddingLength;

	public DefaultFCGIHeader(byte version, byte type) {
		this.version = version;
		this.type = type;
	}

	public DefaultFCGIHeader(byte type) {
		this(FCGI_VERSION_1, type);
	}

	@Override
	public byte version() {
		return version;
	}

	@Override
	public byte type() {
		return type;
	}

	@Override
	public int requestId() {
		return requestId;
	}

	@Override
	public FCGIHeader requestId(int requestId) {
		this.requestId = requestId;
		return this;
	}

	@Override
	public int contentLength() {
		return contentLength;
	}

	@Override
	public FCGIHeader contentLength(int contentLength) {
		this.contentLength = contentLength;
		return this;
	}

	@Override
	public int paddingLength() {
		return paddingLength;
	}

	@Override
	public FCGIHeader paddingLength(int paddingLength) {
		this.paddingLength = paddingLength;
		return this;
	}

	@Override
	public String toString() {
		return "FCGIHeader[version=" + version + ", type=" + type + ", requestId=" + requestId + ", contentLength="
				+ contentLength + ", paddingLength=" + paddingLength + "]";
	}

}
