package com.github.fmjsjx.netty.codec.fcgi;

import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

public final class FCGIMessageUtil {

	public static final byte getVersion(FCGIRecord record) {
		return record.header().version();
	}

	public static final byte getType(FCGIRecord record) {
		return record.header().type();
	}

	public static final int getRequestId(FCGIRecord record) {
		return record.header().requestId();
	}

	public static final void setRequestId(FCGIRecord record, int requestId) {
		record.header().requestId(requestId);
	}

	public static final int getContentLength(FCGIRecord record) {
		return record.header().contentLength();
	}

	public static final void setContentLength(FCGIRecord record, int contentLength) {
		int paddingLength = contentLength % 8;
		paddingLength = paddingLength == 0 ? 0 : 8 - paddingLength;
		setContentAndPaddingLength(record, contentLength, paddingLength);
	}

	public static final int getPaddingLength(FCGIRecord record) {
		return record.header().paddingLength();
	}

	public static final void setContentAndPaddingLength(FCGIRecord record, int contentLength, int paddingLength) {
		record.header().contentLength(contentLength).paddingLength(paddingLength);
	}

	public static final int length(CharSequence s) {
		if (s instanceof AsciiString) {
			return s.length();
		} else {
			return s.toString().getBytes(CharsetUtil.UTF_8).length;
		}
	}

	private FCGIMessageUtil() {
	}

}
