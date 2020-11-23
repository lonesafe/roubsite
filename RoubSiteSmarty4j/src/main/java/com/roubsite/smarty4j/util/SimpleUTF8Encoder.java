package com.roubsite.smarty4j.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

class SimpleUTF8Encoder extends SimpleEncoder {

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
			int c = ca[i];
			if (c < 0x80) {
				ba[j++] = (byte) c;
			} else if (c < 0x800) {
				ba[j++] = (byte) ((c & 0x7C0 | 0x3000) >> 6);
				ba[j++] = (byte) (c & 0x3F | 0x80);
			} else {
				ba[j++] = (byte) ((c & 0xF000 | 0xE0000) >> 12);
				ba[j++] = (byte) ((c & 0xFC0 | 0x2000) >> 6);
				ba[j++] = (byte) (c & 0x3F | 0x80);
			}
			i++;
		}
		out.position(j);
	}
}
