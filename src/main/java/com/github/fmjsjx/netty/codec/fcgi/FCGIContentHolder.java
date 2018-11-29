package com.github.fmjsjx.netty.codec.fcgi;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;

public interface FCGIContentHolder extends FCGIRecord, ReferenceCounted {

	ByteBuf content();

}
