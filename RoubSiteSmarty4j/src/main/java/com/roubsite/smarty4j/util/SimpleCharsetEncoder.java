package com.roubsite.smarty4j.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

class SimpleCharsetEncoder extends SimpleEncoder {

	private CharsetEncoder ce;

	public SimpleCharsetEncoder(Charset charset) {
		ce = charset.newEncoder();
	}

	@Override
	public void encode(CharBuffer in, ByteBuffer out) {
		ce.encode(in, out, false);
	}
}
