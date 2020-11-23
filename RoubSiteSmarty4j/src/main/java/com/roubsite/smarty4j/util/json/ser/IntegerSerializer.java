package com.roubsite.smarty4j.util.json.ser;

import java.io.IOException;

import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.JsonReader;
import com.roubsite.smarty4j.util.json.Provider;

public class IntegerSerializer implements Serializer {

	public static final IntegerSerializer instance = new IntegerSerializer();
	
	private IntegerSerializer() {		
	}

	public static void $serialize(Integer o, SimpleCharBuffer cb, Provider provider) {
		cb.append(o.intValue());
	}

	@Override
	public void serialize(Object o, SimpleCharBuffer cb, Provider provider) {
		$serialize((Integer) o, cb, provider);
	}

	@Override
	public Object createObject(Object parent) {
		return null;
	}

	@Override
	public Object deserialize(Object o, JsonReader reader, Provider provider) throws IOException {
		return Integer.valueOf(reader.readInteger());
	}
}
