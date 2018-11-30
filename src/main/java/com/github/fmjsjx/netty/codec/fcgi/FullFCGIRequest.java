package com.github.fmjsjx.netty.codec.fcgi;

import io.netty.util.ReferenceCounted;

public interface FullFCGIRequest extends FCGIMessage, ReferenceCounted {

	default int requestId() {
		return beginRequest().getRequestId();
	}

	FCGIBeginRequest beginRequest();

	FCGIParams params();

	FCGIStdin stdin();

	FCGIData data();

	default boolean hasData() {
		return data() != null;
	}

}
