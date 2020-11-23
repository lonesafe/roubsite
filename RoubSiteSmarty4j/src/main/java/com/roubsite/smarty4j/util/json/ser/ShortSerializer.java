package com.roubsite.smarty4j.util.json.ser;

import java.io.IOException;

import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.JsonReader;
import com.roubsite.smarty4j.util.json.Provider;
import com.roubsite.smarty4j.util.json.ser.Serializer;
import com.roubsite.smarty4j.util.json.ser.ShortSerializer;

public class ShortSerializer implements Serializer {

	public static final ShortSerializer instance = new ShortSerializer();

	private ShortSerializer() {
	}

	public static void $serialize(Short o, SimpleCharBuffer cb, Provider provider) {
		cb.append(o.intValue());
	}

	@Override
	public void serialize(Object o, SimpleCharBuffer cb, Provider provider) {
		$serialize((Short) o, cb, provider);
	}

	@Override
	public Object createObject(Object parent) {
		return null;
	}

	@Override
	public Object deserialize(Object o, JsonReader reader, Provider provider) throws IOException {
		int value = reader.readInteger();
		if (value > Short.MAX_VALUE || value < Short.MIN_VALUE) {
			// TODO json数据错误
			throw new NullPointerException();
		}
		return Short.valueOf((short) value);
	}
}
