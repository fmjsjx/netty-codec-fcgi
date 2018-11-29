package com.github.fmjsjx.netty.codec.fcgi;

import io.netty.util.AsciiString;

public class FCGIConstants {

	/**
	 * Number of public static final bytes in a FCGI_Header. Future versions of the protocol will not
	 * reduce this number.
	 */
	public static final byte FCGI_HEADER_LEN = 8;

	/**
	 * Max length of contentLength in FCGI_Header.
	 */
	public static final int FCGI_MAX_CONTENT_LENGTH = 65535;

	/**
	 * Request id limit for complex services.
	 */
	public static final int FCGI_REQUEST_ID_LIMIT = 65536;

	/**
	 * Content length of FCGI_BeginRequest.
	 */
	public static final int FCGI_BEGIN_REQUEST_CONTENT_LENGTH = 8;

	/**
	 * Content length of FCGI_EndRequest.
	 */
	public static final int FCGI_END_REQUEST_CONTENT_LENGTH = 8;

	/**
	 * Content length of FCGI_UnknownType.
	 */
	public static final int FCGI_UNKNOWN_TYPE_CONTENT_LENGTH = 8;

	/**
	 * Value for version component of FCGI_Header
	 */
	public static final byte FCGI_VERSION_1 = 1;

	/*
	 * Values for type component of FCGI_Header
	 */
	public static final byte FCGI_BEGIN_REQUEST = 1;
	public static final byte FCGI_ABORT_REQUEST = 2;
	public static final byte FCGI_END_REQUEST = 3;
	public static final byte FCGI_PARAMS = 4;
	public static final byte FCGI_STDIN = 5;
	public static final byte FCGI_STDOUT = 6;
	public static final byte FCGI_STDERR = 7;
	public static final byte FCGI_DATA = 8;
	public static final byte FCGI_GET_VALUES = 9;
	public static final byte FCGI_GET_VALUES_RESULT = 10;
	public static final byte FCGI_UNKNOWN_TYPE = 11;
	public static final byte FCGI_MAXTYPE = FCGI_UNKNOWN_TYPE;

	/**
	 * Value for requestId component of FCGI_Header
	 */
	public static final byte FCGI_NULL_REQUEST_ID = 0;

	/**
	 * Mask for flags component of FCGI_BeginRequestBody
	 */
	public static final byte FCGI_KEEP_CONN = 1;

	/*
	 * Values for role component of FCGI_BeginRequestBody
	 */
	public static final byte FCGI_RESPONDER = 1;
	public static final byte FCGI_AUTHORIZER = 2;
	public static final byte FCGI_FILTER = 3;

	/*
	 * Values for protocolStatus component of FCGI_EndRequestBody
	 */
	public static final byte FCGI_REQUEST_COMPLETE = 0;
	public static final byte FCGI_CANT_MPX_CONN = 1;
	public static final byte FCGI_OVERLOADED = 2;
	public static final byte FCGI_UNKNOWN_ROLE = 3;

	/*
	 * Variable names for FCGI_GET_VALUES / FCGI_GET_VALUES_RESULT records
	 */
	public static final AsciiString FCGI_MAX_CONNS = AsciiString.cached("FCGI_MAX_CONNS");
	public static final AsciiString FCGI_MAX_REQS = AsciiString.cached("FCGI_MAX_REQS");
	public static final AsciiString FCGI_MPXS_CONNS = AsciiString.cached("FCGI_MPXS_CONNS");
	
}
