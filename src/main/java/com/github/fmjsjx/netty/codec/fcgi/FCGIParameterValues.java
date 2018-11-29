package com.github.fmjsjx.netty.codec.fcgi;

import static io.netty.util.AsciiString.cached;

import io.netty.util.AsciiString;

public final class FCGIParameterValues {

	public static final AsciiString OPTIONS = cached("OPTIONS");
	public static final AsciiString HEAD = cached("HEAD");
	public static final AsciiString GET = cached("GET");
	public static final AsciiString POST = cached("POST");
	public static final AsciiString PUT = cached("PUT");
	public static final AsciiString PATCH = cached("PATCH");
	public static final AsciiString DELETE = cached("DELETE");
	public static final AsciiString TRACE = cached("TRACE");
	public static final AsciiString CONNECT = cached("CONNECT");

	public static final AsciiString APPLICATION_JSON = cached("application/json");
	public static final AsciiString APPLICATION_XML = cached("application/xml");
	public static final AsciiString APPLICATION_X_WWW_FORM_URLENCODED = cached("application/x-www-form-urlencoded");
	public static final AsciiString APPLICATION_OCTET_STREAM = cached("application/octet-stream");
	public static final AsciiString APPLICATION_JSON_UTF8 = cached("application/json; charset=UTF-8");
	public static final AsciiString APPLICATION_XML_UTF8 = cached("application/xml; charset=UTF-8");
	public static final AsciiString TEXT_PLAIN = cached("text/plain");
	public static final AsciiString TEXT_PLAIN_UTF8 = cached("text/plain; charset=UTF-8");

	public static final AsciiString CGI_1_1 = cached("CGI/1.1");
	public static final AsciiString _200 = cached("200");

	private FCGIParameterValues() {
	}

}
