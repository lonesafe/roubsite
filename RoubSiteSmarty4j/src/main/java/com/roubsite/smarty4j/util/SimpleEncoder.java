package com.roubsite.smarty4j.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 简易的字符编码器，将字符流转换成字节流
 */
public abstract class SimpleEncoder {

	private static final Map<String, SimpleEncoder> encoders = new HashMap<String, SimpleEncoder>();

	static {
		encoders.put("UTF-8", new SimpleUTF8Encoder());
		encoders.put("GBK", new SimpleGBKEncoder());
		encoders.put("GB2312", new SimpleGBKEncoder());
	}

	public static SimpleEncoder forCharset(Charset charset) {
		SimpleEncoder encoder = encoders.get(charset.name());
		if (encoder != null) {
			return encoder;
		}
		return new SimpleCharsetEncoder(charset);
	}

	public abstract void encode(CharBuffer in, ByteBuffer out);
}
