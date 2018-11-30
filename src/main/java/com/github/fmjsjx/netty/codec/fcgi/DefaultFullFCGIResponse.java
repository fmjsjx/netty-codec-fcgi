package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;

import io.netty.buffer.ByteBuf;
import io.netty.util.AbstractReferenceCounted;

public final class DefaultFullFCGIResponse extends AbstractReferenceCounted implements FullFCGIResponse {

	private final int requestId;
	private FCGIStderr stderr;
	private FCGIStdout stdout;
	private FCGIEndRequest endRequest;

	public DefaultFullFCGIResponse(int requestId) {
		this.requestId = requestId;
	}

	@Override
	public int requestId() {
		return requestId;
	}

	@Override
	public FCGIStderr stderr() {
		return stderr;
	}

	public DefaultFullFCGIResponse stderr(FCGIStderr stderr) {
		this.stderr = stderr;
		return this;
	}

	public DefaultFullFCGIResponse stderr(ByteBuf content) {
		stderr = new FCGIStderr(content);
		FCGIMessageUtil.setRequestId(stderr, requestId);
		return this;
	}

	@Override
	public FCGIStdout stdout() {
		return stdout;
	}

	public DefaultFullFCGIResponse stdout(FCGIStdout stdout) {
		this.stdout = stdout;
		return this;
	}

	public DefaultFullFCGIResponse stdout(ByteBuf content) {
		stdout = new FCGIStdout(content);
		FCGIMessageUtil.setRequestId(stdout, requestId);
		return this;
	}

	@Override
	public FCGIEndRequest endRequest() {
		return endRequest;
	}

	public DefaultFullFCGIResponse endRequest(FCGIEndRequest endRequest) {
		this.endRequest = endRequest;
		return this;
	}
	
	public FCGIEndRequest endRequest(int appStatus, byte protocolStatus) {
		endRequest = new FCGIEndRequest();
		FCGIMessageUtil.setRequestId(endRequest, requestId);
		endRequest.appStatus(appStatus);
		endRequest.protocolStatus(protocolStatus);
		return endRequest;
	}
	
	public FCGIEndRequest endRequest(int appStatus) {
		return endRequest(appStatus, FCGI_REQUEST_COMPLETE);
	}

	@Override
	public DefaultFullFCGIResponse retain() {
		super.retain();
		return this;
	}

	@Override
	public DefaultFullFCGIResponse retain(int increment) {
		super.retain(increment);
		return this;
	}

	@Override
	public DefaultFullFCGIResponse touch() {
		super.touch();
		return this;
	}

	@Override
	public DefaultFullFCGIResponse touch(Object hint) {
		if (stderr != null) {
			stderr.touch(hint);
		}
		if (stdout != null) {
			stdout.touch(hint);
		}
		return this;
	}

	@Override
	protected void deallocate() {
		if (stderr != null) {
			stderr.release();
		}
		if (stdout != null) {
			stdout.release();
		}
	}

	@Override
	public String toString() {
		return "DefaultFullFCGIResponse[requestId=" + requestId + ", stderr=" + stderr + ", stdout=" + stdout + "]";
	}
	
}
