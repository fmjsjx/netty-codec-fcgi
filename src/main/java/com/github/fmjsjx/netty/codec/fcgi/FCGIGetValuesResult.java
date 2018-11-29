package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;

import java.util.List;

public final class FCGIGetValuesResult extends AbstractFCGINameValuePairs {

	public FCGIGetValuesResult(FCGIHeader header) {
		super(header);
	}

	public FCGIGetValuesResult() {
		this(new DefaultFCGIHeader(FCGI_GET_VALUES_RESULT));
	}

	public FCGIGetValuesResult(CharSequence name, CharSequence value) {
		this();
		addPair(name, value);
	}

	public List<FCGINameValuePair> results() {
		return pairs();
	}

	public CharSequence result(CharSequence name) {
		return getValue(name);
	}

	public FCGIGetValuesResult setResult(CharSequence name, CharSequence value) {
		addPair(name, value);
		return this;
	}

	public FCGIGetValuesResult setResult(CharSequence name, int value) {
		return setResult(name, Integer.toString(value));
	}

	@Override
	public String toString() {
		return "FCGIGetValuesResult[header=" + header() + ", results=" + results() + "]";
	}

}
