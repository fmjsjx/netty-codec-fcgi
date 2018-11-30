package com.github.fmjsjx.netty.codec.fcgi;

public interface FullFCGIResponse extends FCGIMessage {
	
	int requestId();

	FCGIEndRequest endRequest();

	FCGIStdout stdout();

	FCGIStderr stderr();

	default boolean hasStderr() {
		return stderr() != null;
	}
}
