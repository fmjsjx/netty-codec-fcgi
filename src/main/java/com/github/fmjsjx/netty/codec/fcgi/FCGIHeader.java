package com.github.fmjsjx.netty.codec.fcgi;

public interface FCGIHeader {

	byte version();

	byte type();

	int requestId();

	FCGIHeader requestId(int requestId);

	int contentLength();

	FCGIHeader contentLength(int contentLength);

	int paddingLength();

	FCGIHeader paddingLength(int paddingLength);

}
