package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;

import java.util.List;

public final class FCGIParams extends AbstractFCGINameValuePairs {

	public FCGIParams(FCGIHeader header) {
		super(header);
	}

	public FCGIParams() {
		this(new DefaultFCGIHeader(FCGI_PARAMS));
	}

	public FCGIParams(CharSequence name, CharSequence value) {
		this();
		addPair(name, value);
	}

	public List<FCGINameValuePair> params() {
		return pairs;
	}

	public FCGIParams addParam(CharSequence name, CharSequence value) {
		addPair(name, value);
		return this;
	}

	public CharSequence removeParam(CharSequence name) {
		FCGINameValuePair pair = removePair(name);
		if (pair != null) {
			return pair.value();
		}
		return null;
	}

	@Override
	public String toString() {
		return "FCGIParams[header=" + header() + ", params=" + params() + "]";
	}

}
