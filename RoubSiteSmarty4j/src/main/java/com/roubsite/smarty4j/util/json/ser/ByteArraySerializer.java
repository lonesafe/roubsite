package com.roubsite.smarty4j.util.json.ser;

import java.io.IOException;
import java.util.Arrays;

import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.JsonReader;
import com.roubsite.smarty4j.util.json.Provider;

public class ByteArraySerializer implements Serializer {

	public static final ByteArraySerializer instance = new ByteArraySerializer();

	private ByteArraySerializer() {
	}

	public static void $serialize(byte[] o, SimpleCharBuffer cb, Provider provider) {
		cb.append('[');
		for (byte item : o) {
			cb.append(item);
			cb.append(',');
		}
		cb.appendClose(']');
	}

	@Override
	public void serialize(Object o, SimpleCharBuffer cb, Provider provider) {
		$serialize((byte[]) o, cb, provider);
	}

	@Override
	public Object createObject(Object parent) {
		return null;
	}

	@Override
	public Object deserialize(Object o, JsonReader reader, Provider provider) throws IOException {
		if (reader.readIgnoreWhitespace() != '[') {
			// TODO json数据错误
			throw new NullPointerException();
		}
		byte[] list = new byte[16];
		int size = 0;
		if (reader.readIgnoreWhitespace() != ']') {
			reader.unread();
			while (true) {
				if (size == list.length) {
					list = Arrays.copyOf(list, size * 2);
				}
				int value = reader.readInteger();
				if (value > Byte.MAX_VALUE || value < Byte.MIN_VALUE) {
					// TODO json数据错误
					throw new NullPointerException();
				}
				list[size++] = (byte) value;
				int ch = reader.readIgnoreWhitespace();
				if (ch == ']') {
					break;
				}
				if (ch != ',') {
					// TODO 出错
				}
			}
		}
		return Arrays.copyOf(list, size);
	}
}
