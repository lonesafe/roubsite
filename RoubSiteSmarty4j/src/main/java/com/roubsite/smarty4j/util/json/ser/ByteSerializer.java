package com.roubsite.smarty4j.util.json.ser;

import java.io.IOException;

import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.JsonReader;
import com.roubsite.smarty4j.util.json.Provider;

public class ByteSerializer implements Serializer {

	public static final ByteSerializer instance = new ByteSerializer();

	private ByteSerializer() {
	}

	public static void $serialize(Byte o, SimpleCharBuffer cb, Provider provider) {
		cb.append(o.intValue());
	}

	@Override
	public void serialize(Object o, SimpleCharBuffer cb, Provider provider) {
		$serialize((Byte) o, cb, provider);
	}

	@Override
	public Object createObject(Object parent) {
		return null;
	}

	@Override
	public Object deserialize(Object o, JsonReader reader, Provider provider) throws IOException {
		int value = reader.readInteger();
		if (value > Byte.MAX_VALUE || value < Byte.MIN_VALUE) {
			// TODO json数据错误
			throw new NullPointerException();
		}
		return Byte.valueOf((byte) value);
	}
}
