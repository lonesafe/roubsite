package com.roubsite.smarty4j.util;

import java.io.Writer;

/**
 * 框架默认的字符串缓存输出对象，使用java.lang.StringBuilder，避免线程锁定的开销。
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class SimpleStringWriter extends Writer {

	private SimpleCharBuffer buf = new SimpleCharBuffer(64);

	@Override
	public void write(int c) {
		buf.append((char) c);
	}

	@Override
	public void write(char cbuf[], int off, int len) {
		buf.append(cbuf, off, len);
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() {
	}

	@Override
	public String toString() {
		return buf.toString();
	}
}
