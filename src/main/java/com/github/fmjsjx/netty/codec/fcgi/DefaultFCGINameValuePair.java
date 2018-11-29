package com.github.fmjsjx.netty.codec.fcgi;

public class DefaultFCGINameValuePair implements FCGINameValuePair {

	private final CharSequence name;
	private final CharSequence value;

	public DefaultFCGINameValuePair(CharSequence name, CharSequence value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public CharSequence name() {
		return name;
	}

	@Override
	public CharSequence value() {
		return value;
	}

	@Override
	public String toString() {
		return "(" + name + "," + value + ")";
	}

}
