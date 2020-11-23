package com.roubsite.smarty4j.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

class SimpleGBKEncoder extends SimpleEncoder {

	private static char[] c2b = new char[65536];

	static {
		CharsetEncoder ce = Charset.forName("GBK").newEncoder();
		char[] ca = new char[1];
		byte[] ba = new byte[2];
		CharBuffer cb = CharBuffer.wrap(ca);
		ByteBuffer bb = ByteBuffer.wrap(ba);
		for (int i = 0; i < 128; i++) {
			c2b[i] = (char) i;
		}
		for (int i = 128; i < 65536; i++) {
			ca[0] = (char) i;
			ce.encode(cb, bb, false);
			cb.flip();
			bb.flip();
			if (bb.limit() == 1) {
				int v = ba[0] & 0xFF;
				c2b[i] = (char) v;
			} else {
				int v1 = ba[0] & 0xFF;
				int v2 = ba[1] & 0xFF;
				c2b[i] = (char) ((v1 << 8) | v2);
			}
		}
	}

	@Override
	public void encode(CharBuffer in, ByteBuffer out) {
		char[] ca = in.array();
		byte[] ba = out.array();
		int len = in.limit();
		int i = 0;
		int j = 0;
		while (i < len && ca[i] < 0x80) {
			ba[j++] = (byte) ca[i++];
		}
		while (i < len) {
			int c = c2b[ca[i]];
			if (c < 0x100) {
				ba[j++] = (byte) c;
			} else {
				ba[j++] = (byte) (c >> 8);
				ba[j++] = (byte) c;
			}
			i++;
		}
		out.position(j);
	}
}
