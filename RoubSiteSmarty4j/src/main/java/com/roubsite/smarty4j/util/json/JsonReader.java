package com.roubsite.smarty4j.util.json;

import java.io.IOException;
import java.io.Reader;

import com.roubsite.smarty4j.util.SimpleCharBuffer;

public class JsonReader extends Reader {

	private static final int WHITESPACE = 1;
	private static final int NUMBER = 2;
	private static final int LETTER = 3;
	private static int[] codeTypes = new int[128];
	private static int[] escCodes = new int[128];
	private static int[] codeValues = new int[128];

	static {
		for (int i = 0; i < 128; i++) {
			codeValues[i] = -1;
		}
		for (int i = '0'; i <= '9'; i++) {
			codeTypes[i] = NUMBER;

			codeValues[i] = i - '0';
		}
		for (int i = 'A'; i <= 'Z'; i++) {
			codeTypes[i] = LETTER;
		}
		for (int i = 'a'; i <= 'z'; i++) {
			codeTypes[i] = LETTER;
		}
		codeTypes['.'] = NUMBER;
		codeTypes['-'] = NUMBER;
		codeTypes['\t'] = WHITESPACE;
		codeTypes['\n'] = WHITESPACE;
		codeTypes['\r'] = WHITESPACE;
		codeTypes[' '] = WHITESPACE;

		escCodes['\\'] = '\\';
		escCodes['"'] = '"';
		escCodes['b'] = '\b';
		escCodes['n'] = '\n';
		escCodes['r'] = '\r';
		escCodes['t'] = '\t';
		escCodes['f'] = '\f';

		codeValues['a'] = codeValues['A'] = 10;
		codeValues['b'] = codeValues['B'] = 11;
		codeValues['c'] = codeValues['C'] = 12;
		codeValues['d'] = codeValues['D'] = 13;
		codeValues['e'] = codeValues['E'] = 14;
		codeValues['f'] = codeValues['F'] = 15;
	}

	private SimpleCharBuffer buf = new SimpleCharBuffer(128);

	/** 文本输入流数据来源 */
	private Reader in;

	/** 文本缓冲区 */
	private char cb[] = new char[8192];

	/** 文本缓冲区全部的数据量 */
	private int nChars;

	/** 文本缓冲区有效的数据起始位置 */
	private int nextChar;

	/**
	 * 建立缓冲文本输入对象。
	 * 
	 * @param in 文本输入对象
	 */
	public void bind(Reader in) {
		this.in = in;
		nChars = 0;
		nextChar = 0;
	}

	public void unread() {
		nextChar--;
	}

	public int readIgnoreWhitespace() throws IOException {
		while (true) {
			int ch = read();
			if (ch < 128 && codeTypes[ch] != WHITESPACE) {
				return ch;
			}
		}
	}

	public long readLong() throws IOException {
		long value = 0;
		boolean sign = false;
		int ch = readIgnoreWhitespace();
		if (ch == '-') {
			sign = true;
			ch = read();
		}
		while (ch >= '0' && ch <= '9') {
			if (value > 922337203685477580L) {
				// TODO 数值超出范围
				throw new NullPointerException();
			} else if (value == 922337203685477580L) {
				if (sign && ch == '8') {
					ch = read();
					if (ch >= '0' && ch <= '9') {
						// TODO 数字格式不合法
						throw new NullPointerException();
					}
					unread();
					return Long.MIN_VALUE;
				} else if (ch > '7') {
					// TODO 数值超出范围
					throw new NullPointerException();
				}
			}
			value = value * 10 + ch - '0';
			ch = read();
		}
		unread();
		return sign ? -value : value;
	}

	public int readInteger() throws IOException {
		int value = 0;
		boolean sign = false;
		int ch = readIgnoreWhitespace();
		if (ch == '-') {
			sign = true;
			ch = read();
		}
		while (ch >= '0' && ch <= '9') {
			if (value > 214748364) {
				// TODO 数值超出范围
				throw new NullPointerException();
			} else if (value == 214748364) {
				if (sign && ch == '8') {
					ch = read();
					if (ch >= '0' && ch <= '9') {
						// TODO 数字格式不合法
						throw new NullPointerException();
					}
					unread();
					return Integer.MIN_VALUE;
				} else if (ch > '7') {
					// TODO 数值超出范围
					throw new NullPointerException();
				}
			}
			value = value * 10 + ch - '0';
			ch = read();
		}
		unread();
		return sign ? -value : value;
	}

	public String readConst(boolean isNumber) throws IOException {
		readIgnoreWhitespace();
		unread();
		if (nextChar > 8000) {
			fill();
		}
		int i = nextChar;
		int type = isNumber ? NUMBER : LETTER;
		while (true) {
			if (i >= nChars) {
				i = nChars - nextChar + 1;
				fill();
				if (i >= nChars) {
					// TODO json格式错误
					throw new NullPointerException();
				}
			}
			int c = cb[i];
			if (c < 128 && codeTypes[c] != type) {
				String ret = new String(cb, nextChar, i - nextChar);
				nextChar = i;
				return ret;
			}
			i++;
		}
	}

	public String readString() throws IOException {
		buf.setLength(0);
		if (readIgnoreWhitespace() != '"') {
			// TODO 异常
			throw new NullPointerException();
		}
		while (true) {
			int ch = read();
			if (ch == '"') {
				return buf.toString();
			}
			if (ch == '\\') {
				ch = read();
				if (ch == 'u') {
					ch = 0;
					for (int i = 0; i < 4; i++) {
						int c = read();
						if (c >= 128 || codeValues[c] < 0) {
							// TODO 格式错误
							throw new NullPointerException();
						}
						ch = ch * 16 + c;
					}
				} else {
					if (ch >= 128) {
						// TODO 格式错误
						throw new NullPointerException();
					}
					ch = escCodes[ch];
					if (ch == 0) {
						// TODO json格式错误
						throw new NullPointerException();
					}
				}
			}
			buf.append((char) ch);
		}
	}

	public Object readObject() throws IOException {
		int ch = readIgnoreWhitespace();
		if (ch >= 128) {
			// TODO json格式错误
			throw new NullPointerException();
		}
		unread();
		if (ch == '"') {
			return readString();
		}
		ch = codeTypes[ch];
		if (ch == NUMBER) {
			String s = readConst(true);
			int index = s.indexOf('.');
			if (index < 0) {
				long v = Long.parseLong(s);
				if (v <= Integer.MAX_VALUE && v >= Integer.MIN_VALUE) {
					return (int) v;
				}
				return v;
			}
			if (index == s.lastIndexOf('.')) {
				return Double.valueOf(s);
			}
		} else if (ch == LETTER) {
			return Boolean.valueOf(readConst(false));
		}
		// TODO 格式错误
		throw new NullPointerException();
	}

	@Override
	public int read() throws IOException {
		if (nextChar >= nChars) {
			fill();
			if (nextChar >= nChars) {
				// TODO 异常
				throw new NullPointerException();
			}
		}
		return cb[nextChar++];
	}

	@Override
	public boolean ready() throws IOException {
		if (nextChar >= nChars) {
			fill();
		}
		return nextChar < nChars;
	}

	@Override
	public long skip(long n) throws IOException {
		long r = n;
		while (true) {
			if (nChars - nextChar >= r) {
				nextChar += r;
				break;
			} else {
				r -= nChars - nextChar;
				nextChar = nChars;
				fill();
				if (nextChar >= nChars) {
					break;
				}
			}
		}
		return n - r;
	}

	@Override
	public void close() {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
			}
			in = null;
		}
	}

	@Override
	public int read(char cbuf[], int off, int len) throws IOException {
		int r = len;
		while (r > 0) {
			if (nextChar >= nChars) {
				fill();
				if (nextChar >= nChars) {
					return -1;
				}
			}
			int n = Math.min(r, nChars - nextChar);
			System.arraycopy(cb, nextChar, cbuf, off, n);
			nextChar += n;
			off += n;
			r -= n;
		}
		return len - r;
	}

	/**
	 * 向文本缓冲区内填充数据。
	 * 
	 * @throws IOException 数据读取产生异常
	 */
	private void fill() throws IOException {
		int len = nChars - nextChar + 1;
		if (nextChar > 0) {
			System.arraycopy(cb, nextChar - 1, cb, 0, len);
		}
		int n;
		do {
			n = in.read(cb, len, cb.length - len);
		} while (n == 0);
		if (n > 0) {
			nextChar = 1;
			nChars = len + n;
		}
	}
}