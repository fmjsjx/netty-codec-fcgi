package com.github.fmjsjx.netty.codec.fcgi;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractFCGINameValuePairs extends AbstractFCGIRecord implements FCGINameValuePairs {

	protected final List<FCGINameValuePair> pairs = new LinkedList<>();
	protected final Map<String, FCGINameValuePair> map = new LinkedHashMap<>();

	public AbstractFCGINameValuePairs(FCGIHeader header) {
		super(header);
	}

	@Override
	public List<FCGINameValuePair> pairs() {
		return pairs;
	}

	@Override
	public FCGINameValuePairs addPair(CharSequence name, CharSequence value) {
		FCGINameValuePair pair = new DefaultFCGINameValuePair(name, value);
		pairs.add(pair);
		map.put(name.toString(), pair);
		return this;
	}

	@Override
	public FCGINameValuePair removePair(CharSequence name) {
		FCGINameValuePair pair = map.remove(name.toString());
		if (pair != null) {
			pairs.remove(pair);
		}
		return pair;
	}

	@Override
	public FCGINameValuePair pair(CharSequence name) {
		return map.get(name.toString());
	}

	@Override
	public boolean contains(CharSequence name) {
		return map.containsKey(name.toString());
	}

	@Override
	public int size() {
		return pairs.size();
	}

}
