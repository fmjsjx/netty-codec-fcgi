package com.github.fmjsjx.netty.codec.fcgi;

public interface FCGIRecord extends FCGIMessage {

	FCGIHeader header();

	default boolean isType(byte type) {
		return FCGIMessageUtil.getType(this) == type;
	}

	default int getRequestId() {
		return FCGIMessageUtil.getRequestId(this);
	}

}
