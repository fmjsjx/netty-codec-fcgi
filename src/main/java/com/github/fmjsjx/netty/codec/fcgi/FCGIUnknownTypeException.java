package com.github.fmjsjx.netty.codec.fcgi;

public final class FCGIUnknownTypeException extends FCGIException {

	private static final long serialVersionUID = 1L;

	private final int requestId;
	private final byte type;

	public FCGIUnknownTypeException(int requestId, byte type) {
		super("unknown type " + type + " for the request with id " + requestId);
		this.type = type;
		this.requestId = requestId;
	}

	public int requestId() {
		return requestId;
	}

	public byte type() {
		return type;
	}

}
