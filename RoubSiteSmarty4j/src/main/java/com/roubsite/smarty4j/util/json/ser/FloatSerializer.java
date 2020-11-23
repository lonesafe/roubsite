package com.roubsite.smarty4j.util.json.ser;

import java.io.IOException;

import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.JsonReader;
import com.roubsite.smarty4j.util.json.Provider;
import com.roubsite.smarty4j.util.json.ser.FloatSerializer;

public class FloatSerializer implements Serializer {

	public static final FloatSerializer instance = new FloatSerializer();
	
	private FloatSerializer() {		
	}

	public static void $serialize(Float o, SimpleCharBuffer cb, Provider provider) {
		cb.append(o.toString());
	}

	@Override
	public void serialize(Object o, SimpleCharBuffer cb, Provider provider) {
		$serialize((Float) o, cb, provider);
	}

	@Override
	public Object createObject(Object parent) {
		return null;
	}

	@Override
	public Object deserialize(Object o, JsonReader reader, Provider provider) throws IOException {
		return Float.valueOf(reader.readConst(true));
	}
}
