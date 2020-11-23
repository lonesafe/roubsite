package com.roubsite.smarty4j.util.json.ser;

import java.io.IOException;

import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.JsonReader;
import com.roubsite.smarty4j.util.json.Provider;

public class LongSerializer implements Serializer {

	public static final LongSerializer instance = new LongSerializer();

	private LongSerializer() {
	}

	public static void $serialize(Long o, SimpleCharBuffer cb, Provider provider) {
		cb.append(o.longValue());
	}

	@Override
	public void serialize(Object o, SimpleCharBuffer cb, Provider provider) {
		$serialize((Long) o, cb, provider);
	}

	@Override
	public Object createObject(Object parent) {
		return null;
	}

	@Override
	public Object deserialize(Object o, JsonReader reader, Provider provider) throws IOException {
		return Long.valueOf(reader.readLong());
	}
}
