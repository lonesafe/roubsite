package com.roubsite.smarty4j.util;

import java.io.Reader;

/**
 * 框架默认的字符串缓存输入对象，非线程安全，避免线程锁定的开销。
 * 
 * @see com.roubsite.smarty4j.Template
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class SimpleStringReader extends Reader {

	private String s;
	private int index;
	private int size;

	/**
	 * 创建字符串缓存输入
	 * 
	 * @param s
	 *          字符串缓存
	 */
	public SimpleStringReader(String s) {
		this.s = s;
		size = s.length();
	}

	@Override
	public void close() {
	}

	@Override
	public int read(char[] cbuf, int off, int len) {
		if (index >= size) {
			return -1;
		}
		int start = index;
		len = Math.min(len, size - index);
		index += len;
		s.getChars(start, index, cbuf, off);
		return len;
	}

	@Override
	public int read() {
		if (index >= size) {
			return -1;
		}
		char c = s.charAt(index);
		index++;
		return c;
	}
}
