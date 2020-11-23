package com.roubsite.smarty4j.util.json.ser;

import java.io.IOException;
import java.util.Arrays;

import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.JsonReader;
import com.roubsite.smarty4j.util.json.Provider;
import com.roubsite.smarty4j.util.json.ser.Serializer;
import com.roubsite.smarty4j.util.json.ser.ShortArraySerializer;

public class ShortArraySerializer implements Serializer {

	public static final ShortArraySerializer instance = new ShortArraySerializer();

	private ShortArraySerializer() {
	}

	public static void $serialize(short[] o, SimpleCharBuffer cb, Provider provider) {
		cb.append('[');
		for (short item : o) {
			cb.append(item);
			cb.append(',');
		}
		cb.appendClose(']');
	}

	@Override
	public void serialize(Object o, SimpleCharBuffer cb, Provider provider) {
		$serialize((short[]) o, cb, provider);
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
		short[] list = new short[16];
		int size = 0;
		if (reader.readIgnoreWhitespace() != ']') {
			reader.unread();
			while (true) {
				if (size == list.length) {
					list = Arrays.copyOf(list, size * 2);
				}
				int value = reader.readInteger();
				if (value > Short.MAX_VALUE || value < Short.MIN_VALUE) {
					// TODO json数据错误
					throw new NullPointerException();
				}
				list[size++] = (short) value;
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
