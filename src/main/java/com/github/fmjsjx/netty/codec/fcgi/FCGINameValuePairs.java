package com.github.fmjsjx.netty.codec.fcgi;

import java.util.List;

public interface FCGINameValuePairs extends FCGIRecord {

	List<FCGINameValuePair> pairs();

	FCGINameValuePairs addPair(CharSequence name, CharSequence value);

	FCGINameValuePair removePair(CharSequence name);

	FCGINameValuePair pair(CharSequence name);

	boolean contains(CharSequence name);

	int size();

	default boolean isEmpty() {
		return size() == 0;
	}

	default CharSequence getValue(CharSequence name) {
		FCGINameValuePair pair = pair(name);
		if (pair != null) {
			return pair.value();
		}
		return null;
	}

	default CharSequence getStringValue(CharSequence name) {
		FCGINameValuePair pair = pair(name);
		if (pair != null) {
			return pair.value().toString();
		}
		return null;
	}

	default Integer getIntValue(CharSequence name) {
		CharSequence value = getValue(name);
		if (value != null) {
			return new Integer(value.toString());
		}
		return null;
	}

	default int getIntValue(CharSequence name, int defaultValue) {
		CharSequence value = getValue(name);
		if (value != null) {
			return Integer.parseInt(value.toString());
		}
		return defaultValue;
	}

}
