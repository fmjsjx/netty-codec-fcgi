package com.github.fmjsjx.netty.codec.fcgi;

import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class FCGIConstantsTest {

	@Test
	public void test() {
		assertEquals(8, FCGI_HEADER_LEN);
	}

}
