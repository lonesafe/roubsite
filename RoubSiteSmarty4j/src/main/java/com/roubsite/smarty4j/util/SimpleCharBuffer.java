package com.roubsite.smarty4j.util;

import java.io.IOException;
import java.io.Writer;
import java.lang.Integer;
import java.lang.Math;
import java.lang.String;
import java.lang.System;

public class SimpleCharBuffer {
	public static final String NAME = SimpleCharBuffer.class.getName().replace('.', '/');

	private Writer writer;
	private char[] buf;
	private int off;

	public SimpleCharBuffer(int initSize) {
		buf = new char[initSize];
	}

	private static final int[] intTable = { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999,
			Integer.MAX_VALUE };

	private static final char[] digitOnes = new char[100];
	private static final char[] digitTens = new char[100];
	private static final int[] escCodes = new int[128];

	private static final char[] hexChars = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
			'd', 'e', 'f' };

	static {
		for (int i = 0; i < 100; i++) {
			digitOnes[i] = (char) ('0' + i % 10);
			digitTens[i] = (char) ('0' + i / 10);
		}
		for (int i = 0; i < 128; i++) {
			switch (i) {
			case '\\':
				escCodes[i] = '\\';
				break;
			case '"':
				escCodes[i] = '"';
				break;
			case '\b':
				escCodes[i] = 'b';
				break;
			case '\f':
				escCodes[i] = 'f';
				break;
			case '\n':
				escCodes[i] = 'n';
				break;
			case '\r':
				escCodes[i] = 'r';
				break;
			case '\t':
				escCodes[i] = 't';
				break;
			default:
				if (i < 32 || i == 127) {
					escCodes[i] = 'u';
				}
			}
		}
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}

	public void append(int i) {
		if (i == Integer.MIN_VALUE) {
			append("-2147483648");
			return;
		}
		
		if (i < 0) {
			i = -i;
			buf[off++] = '-';
		}
		
		int capacity;
		for (int j = 0;; j++) {
			if (i <= intTable[j]) {
				capacity = off + j + 1;
				break;
			}
		}
		ensureCapacityInternal(capacity);
		off = capacity;

		while (i >= 100) {
			int r = i % 100;
			i = i / 100;
			buf[--capacity] = digitOnes[r];
			buf[--capacity] = digitTens[r];
		}

		buf[--capacity] = digitOnes[i];
		if (i >= 10) {
			buf[--capacity] = digitTens[i];
		}
	}

	public void append(long l) {
		if (l == Long.MIN_VALUE) {
			append("-9223372036854775808");
			return;
		}
		if (l < 0) {
			l = -l;
			buf[off++] = '-';
		}
		
		int capacity;
		int i;
		if (l < 1000000000) {
			i = (int) l;
			capacity = off;
		} else {
			i = (int) (l / 10000000000L);
			capacity = off + 10;
		}
		if (i > 0) {
			for (int j = 0;; j++) {
				if (i <= intTable[j]) {
					capacity += j + 1;
					break;
				}
			}
		}
		ensureCapacityInternal(capacity);
		off = capacity;
		while (l > 100) {
			int r = (int) (l % 100);
			l = l / 100;
			buf[--capacity] = digitOnes[r];
			buf[--capacity] = digitTens[r];
		}

		i = (int) l;
		while (i >= 100) {
			int r = i % 100;
			i = i / 100;
			buf[--capacity] = digitOnes[r];
			buf[--capacity] = digitTens[r];
		}

		buf[--capacity] = digitOnes[i];
		if (i >= 10) {
			buf[--capacity] = digitTens[i];
		}
	}

	public void append(boolean b) {
		if (b) {
			ensureCapacityInternal(off + 4);
			buf[off++] = 't';
			buf[off++] = 'r';
			buf[off++] = 'u';
			buf[off++] = 'e';
		} else {
			ensureCapacityInternal(off + 5);
			buf[off++] = 'f';
			buf[off++] = 'a';
			buf[off++] = 'l';
			buf[off++] = 's';
			buf[off++] = 'e';
		}
	}

	public void append(char c) {
		ensureCapacityInternal(off + 1);
		buf[off++] = c;
	}

	public void append(float f) {
		append(Float.toString(f));
	}

	public void append(double d) {
		append(Double.toString(d));
	}

	public void append(String str) {
		int len = str.length();
		ensureCapacityInternal(off + len);
		str.getChars(0, len, buf, off);
		off += len;
	}

	public void append(char[] str, int offset, int len) {
		ensureCapacityInternal(off + len);
		System.arraycopy(str, offset, buf, off, len);
		off += len;
	}

	public void appendNull() {
		ensureCapacityInternal(off + 4);
		buf[off++] = 'n';
		buf[off++] = 'u';
		buf[off++] = 'l';
		buf[off++] = 'l';
	}

	public void appendString(char c) {
		ensureCapacityInternal(off + 8);
		buf[off++] = '"';
		buf[off] = c;
		if (c < 128) {
			int code = escCodes[c];
			if (code != 0) {
				buf[off++] = '\\';
				buf[off] = (char) code;
				if (code == 'u') {
					buf[++off] = '0';
					buf[++off] = '0';
					buf[++off] = hexChars[(c >> 4)];
					buf[++off] = hexChars[c & 0xF];
				}
			}
		}
		off++;
		buf[off++] = '"';
	}

	public void appendString(String str) {
		final int len = str.length();
		if (len == 0) {
			ensureCapacityInternal(off + 2);
			buf[off++] = '"';
			buf[off++] = '"';
			return;
		}
		int i = off + len * 5;
		final int end = i + len;
		ensureCapacityInternal(off + len * 6 + 2);
		buf[off++] = '"';
		str.getChars(0, len, buf, i);
		for (; i < end; off++) {
			char c = buf[i++];
			buf[off] = c;
			if (c < 128) {
				int code = escCodes[c];
				if (code != 0) {
					buf[off++] = '\\';
					buf[off] = (char) code;
					if (code == 'u') {
						buf[++off] = '0';
						buf[++off] = '0';
						buf[++off] = hexChars[(c >> 4)];
						buf[++off] = hexChars[c & 0xF];
					}
				}
			}
		}
		buf[off++] = '"';
	}

	public void appendClose(char c) {
		if (buf[off - 1] == ',') {
			buf[off - 1] = c;
		} else {
			buf[off++] = c;
		}
	}
	
	public void flush() throws IOException {
		if (writer != null) {
			writer.write(buf, 0, off);
			off = 0;
		}
	}

	public int length() {
		return off;
	}

	public void setCharAt(int off, char c) {
		buf[off] = c;
	}

	public void setLength(int off) {
		this.off = off;
	}

	public String toString() {
		return new String(buf, 0, off);
	}

	private void ensureCapacityInternal(int size) {
		if (size > 1024 * 1024) {
			if (writer != null) {
				try {
					writer.write(buf, 0, off);
				} catch (IOException ex) {
					throw new RuntimeException("IOException");
				}
				size -= off;
				off = 0;
			}
		}
		int len = buf.length;
		if (size > len) {
			char[] newBuf = new char[Math.max(size * 2 - len, len * 2)];
			System.arraycopy(buf, 0, newBuf, 0, off);
			buf = newBuf;
		}
	}
}
