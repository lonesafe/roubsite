package com.roubsite.smarty4j.util.json.ser;

import java.io.IOException;

import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.JsonReader;
import com.roubsite.smarty4j.util.json.Provider;

public class BooleanSerializer implements Serializer {

	public static final BooleanSerializer instance = new BooleanSerializer();

	private BooleanSerializer() {
	}

	public static void $serialize(Boolean o, SimpleCharBuffer cb, Provider provider) {
		cb.append(o.booleanValue());
	}

	@Override
	public void serialize(Object o, SimpleCharBuffer cb, Provider provider) {
		$serialize((Boolean) o, cb, provider);
	}

	@Override
	public Object createObject(Object parent) {
		return null;
	}

	@Override
	public Object deserialize(Object o, JsonReader reader, Provider provider) throws IOException {
		return Boolean.valueOf(reader.readConst(false));
	}
}
