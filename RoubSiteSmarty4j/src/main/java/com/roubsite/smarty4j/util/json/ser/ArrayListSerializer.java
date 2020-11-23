package com.roubsite.smarty4j.util.json.ser;

import java.util.ArrayList;

import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.JsonSerializer;
import com.roubsite.smarty4j.util.json.Provider;


public class ArrayListSerializer extends ListSerializer implements Serializer, Generic {

	public static final ArrayListSerializer instance = new ArrayListSerializer();

	private ArrayListSerializer() {
	}

	public static void $serialize(ArrayList<?> o, SimpleCharBuffer cb, Provider provider, Class<?> generic) {
		Serializer serializer = provider.getSerializer(generic);
		cb.append('[');
		for (int i = 0, len = o.size(); i < len; i++) {
			serializer.serialize(o.get(i), cb, provider);
			cb.append(',');
		}
		cb.appendClose(']');
	}

	public static void $serialize(ArrayList<?> o, SimpleCharBuffer cb, Provider provider) {
		cb.append('[');
		for (int i = 0, len = o.size(); i < len; i++) {
			JsonSerializer.serializeValue(o.get(i), cb, provider);
			cb.append(',');
		}
		cb.appendClose(']');
	}

	@Override
	public void serialize(Object o, SimpleCharBuffer cb, Provider provider) {
		$serialize((ArrayList<?>) o, cb, provider);
	}
}
