package com.github.fmjsjx.netty.codec.fcgi;

import static java.util.Objects.*;
import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;

import io.netty.buffer.ByteBuf;
import io.netty.util.AbstractReferenceCounted;

public final class DefaultFullFCGIRequest extends AbstractReferenceCounted implements FullFCGIRequest {

	private final FCGIBeginRequest beginRequest;
	private FCGIParams params;
	private FCGIStdin stdin;
	private FCGIData data;

	public DefaultFullFCGIRequest(FCGIBeginRequest beginRequest) {
		this.beginRequest = requireNonNull(beginRequest, "beginRequest");
	}

	public DefaultFullFCGIRequest(int requestId) {
		this(new FCGIBeginRequest());
		FCGIMessageUtil.setRequestId(beginRequest, requestId);
		beginRequest.role(FCGI_RESPONDER);
	}

	@Override
	public FCGIBeginRequest beginRequest() {
		return beginRequest;
	}

	@Override
	public FCGIParams params() {
		if (params == null) {
			params = new FCGIParams();
			FCGIMessageUtil.setRequestId(params, requestId());
		}
		return params;
	}

	public DefaultFullFCGIRequest params(FCGIParams params) {
		this.params = params;
		return this;
	}

	@Override
	public FCGIStdin stdin() {
		return stdin;
	}

	public DefaultFullFCGIRequest stdin(FCGIStdin stdin) {
		this.stdin = stdin;
		return this;
	}

	public DefaultFullFCGIRequest stdin(ByteBuf content) {
		stdin = new FCGIStdin(content);
		FCGIMessageUtil.setRequestId(stdin, requestId());
		return this;
	}

	@Override
	public FCGIData data() {
		return data;
	}

	public DefaultFullFCGIRequest data(FCGIData data) {
		this.data = data;
		return this;
	}

	public DefaultFullFCGIRequest data(ByteBuf content) {
		data = new FCGIData(content);
		FCGIMessageUtil.setRequestId(data, requestId());
		return this;
	}

	@Override
	public DefaultFullFCGIRequest retain() {
		super.retain();
		return this;
	}

	@Override
	public DefaultFullFCGIRequest retain(int increment) {
		super.retain(increment);
		return this;
	}

	@Override
	public DefaultFullFCGIRequest touch() {
		super.touch();
		return this;
	}

	@Override
	public DefaultFullFCGIRequest touch(Object hint) {
		if (stdin != null) {
			stdin.touch(hint);
		}
		if (data != null) {
			data.touch(hint);
		}
		return this;
	}

	@Override
	protected void deallocate() {
		if (stdin != null) {
			stdin.release();
		}
		if (data != null) {
			data.release();
		}
	}

	@Override
	public String toString() {
		return "DefaultFullFCGIRequest[beginRequest=" + beginRequest + ", params=" + params + ", stdin=" + stdin
				+ ", data=" + data + "]";
	}

}
