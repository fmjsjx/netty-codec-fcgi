package com.github.fmjsjx.netty.codec.fcgi;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCounted;

public abstract class AbstractFCGIContentHolder extends AbstractFCGIRecord implements FCGIContentHolder {

	protected final ByteBuf content;

	public AbstractFCGIContentHolder(FCGIHeader header) {
		this(header, Unpooled.EMPTY_BUFFER);
	}

	public AbstractFCGIContentHolder(FCGIHeader header, ByteBuf content) {
		super(header);
		this.content = content;
	}

	@Override
	public ByteBuf content() {
		return content;
	}

	@Override
	public int refCnt() {
		return content.refCnt();
	}

	@Override
	public ReferenceCounted retain() {
		content.retain();
		return this;
	}

	@Override
	public ReferenceCounted retain(int increment) {
		content.retain(increment);
		return this;
	}

	@Override
	public ReferenceCounted touch() {
		content.touch();
		return this;
	}

	@Override
	public ReferenceCounted touch(Object hint) {
		content.touch(hint);
		return this;
	}

	@Override
	public boolean release() {
		return content.release();
	}

	@Override
	public boolean release(int decrement) {
		return content.release(decrement);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[header=" + header + ", content=" + content + "]";
	}

}
