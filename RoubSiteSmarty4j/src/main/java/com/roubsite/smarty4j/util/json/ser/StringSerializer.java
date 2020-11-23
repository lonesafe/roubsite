package com.roubsite.smarty4j.util.json.ser;

import java.io.IOException;

import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.JsonReader;
import com.roubsite.smarty4j.util.json.Provider;
import com.roubsite.smarty4j.util.json.ser.Serializer;

public class StringSerializer implements Serializer {

	public static final StringSerializer instance = new StringSerializer();
	
	private StringSerializer() {		
	}

	public static void $serialize(String o, SimpleCharBuffer cb, Provider provider) {
		cb.appendString(o);
	}

	@Override
	public void serialize(Object o, SimpleCharBuffer cb, Provider provider) {
		$serialize((String) o, cb, provider);
	}

	@Override
	public Object createObject(Object parent) {
		return null;
	}

	@Override
	public Object deserialize(Object o, JsonReader reader, Provider provider) throws IOException {
		return reader.readString();
	}
}
