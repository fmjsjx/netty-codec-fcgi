package com.github.fmjsjx.netty.codec.fcgi;

import static io.netty.util.AsciiString.cached;

import io.netty.util.AsciiString;

public final class FCGIParameterNames {

	public static final AsciiString SCRIPT_FILENAME = cached("SCRIPT_FILENAME");
	public static final AsciiString QUERY_STRING = cached("QUERY_STRING");
	public static final AsciiString REQUEST_METHOD = cached("REQUEST_METHOD");
	public static final AsciiString CONTENT_TYPE = cached("CONTENT_TYPE");
	public static final AsciiString CONTENT_LENGTH = cached("CONTENT_LENGTH");

	public static final AsciiString SCRIPT_NAME = cached("SCRIPT_NAME");
	public static final AsciiString REQUEST_URI = cached("REQUEST_URI");
	public static final AsciiString DOCUMENT_URI = cached("DOCUMENT_URI");
	public static final AsciiString DOCUMENT_ROOT = cached("DOCUMENT_ROOT");
	public static final AsciiString SERVER_PROTOCOL = cached("SERVER_PROTOCOL");
	public static final AsciiString HTTPS = cached("HTTPS");

	public static final AsciiString GATEWAY_INTERFACE = cached("GATEWAY_INTERFACE");
	public static final AsciiString SERVER_SOFTWARE = cached("SERVER_SOFTWARE");

	public static final AsciiString REMOTE_ADDR = cached("REMOTE_ADDR");
	public static final AsciiString REMOTE_PORT = cached("REMOTE_PORT");
	public static final AsciiString SERVER_ADDR = cached("SERVER_ADDR");
	public static final AsciiString SERVER_PORT = cached("SERVER_PORT");
	public static final AsciiString SERVER_NAME = cached("SERVER_NAME");

	public static final AsciiString REDIRECT_STATUS = cached("REDIRECT_STATUS");

	public static final AsciiString PATH_INFO = cached("PATH_INFO");

	private FCGIParameterNames() {
	}

}
