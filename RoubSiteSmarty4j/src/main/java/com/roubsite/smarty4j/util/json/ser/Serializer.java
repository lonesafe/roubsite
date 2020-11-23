package com.roubsite.smarty4j.util.json.ser;

import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.JsonReader;
import com.roubsite.smarty4j.util.json.Provider;

public interface Serializer {
	public static final String NAME = Serializer.class.getName().replace('.', '/');

	public void serialize(Object o, SimpleCharBuffer cb, Provider provider);

	public Object createObject(Object parent) throws Exception;

	public Object deserialize(Object o, JsonReader reader, Provider provider) throws Exception;
}
