package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.netty.util.AsciiString;

public final class FCGIGetValues extends AbstractFCGINameValuePairs {

	public FCGIGetValues(FCGIHeader header) {
		super(header);
	}

	public FCGIGetValues() {
		this(new DefaultFCGIHeader(FCGI_GET_VALUES));
	}

	public FCGIGetValues(CharSequence... names) {
		this();
		Arrays.stream(names).forEach(this::addName);
	}

	public FCGIGetValues addName(CharSequence name) {
		addPair(name, AsciiString.EMPTY_STRING);
		return this;
	}

	public boolean removeName(CharSequence name) {
		return removePair(name) != null;
	}

	public boolean containsName(CharSequence name) {
		return contains(name);
	}

	public List<CharSequence> names() {
		return pairs().stream().map(FCGINameValuePair::name).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "FCGIGetValues[header=" + header() + ", names=" + names() + "]";
	}

}
