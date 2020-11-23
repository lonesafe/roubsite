package com.roubsite.smarty4j.util.json.ser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.JsonReader;
import com.roubsite.smarty4j.util.json.JsonSerializer;
import com.roubsite.smarty4j.util.json.Provider;

public class ListSerializer implements Serializer, Generic {

	public static final ListSerializer instance = new ListSerializer();

	protected ListSerializer() {
	}

	public static void $serialize(List<?> o, SimpleCharBuffer cb, Provider provider, Class<?> generic) {
		Serializer serializer = provider.getSerializer(generic);
		cb.append('[');
		for (Object item : o) {
			serializer.serialize(item, cb, provider);
			cb.append(',');
		}
		cb.appendClose(']');
	}

	public static void $serialize(List<?> o, SimpleCharBuffer cb, Provider provider) {
		cb.append('[');
		for (Object item : o) {
			JsonSerializer.serializeValue(item, cb, provider);
			cb.append(',');
		}
		cb.appendClose(']');
	}

	@Override
	public void serialize(Object o, SimpleCharBuffer cb, Provider provider) {
		$serialize((List<?>) o, cb, provider);
	}

	@Override
	public Object createObject(Object parent) {
		return parent;
	}

	@Override
	public Object deserialize(Object o, JsonReader reader, Provider provider) throws Exception {
		return deserialize(o, reader, provider, null);
	}

	@Override
	public Type getGeneric(Type type) {
		if (type instanceof ParameterizedType) {
			return ((ParameterizedType) type).getActualTypeArguments()[0];
		}
		return null;
	}

	@Override
	public Object deserialize(Object o, JsonReader reader, Provider provider, Type generic) throws Exception {
		if (reader.readIgnoreWhitespace() != '[') {
			// TODO 出错
			throw new NullPointerException();
		}
		List<Object> list = new ArrayList<Object>();
		if (reader.readIgnoreWhitespace() != ']') {
			reader.unread();
			while (true) {
				if (generic instanceof Class) {
					Serializer serializer = provider.getSerializer((Class<?>) generic);
					list.add(serializer.deserialize(serializer.createObject(o), reader, provider));
				} else if (generic instanceof ParameterizedType) {
					Serializer serializer = provider
							.getSerializer((Class<?>) ((ParameterizedType) generic).getRawType());
					if (serializer instanceof Generic) {
						list.add(((Generic) serializer).deserialize(serializer.createObject(o), reader, provider,
								((Generic) serializer).getGeneric(generic)));
					} else {
						list.add(serializer.deserialize(serializer.createObject(o), reader, provider));
					}
				} else {
					int ch = reader.readIgnoreWhitespace();
					reader.unread();
					if (ch == '{') {
						list.add(MapSerializer.instance.deserialize(MapSerializer.instance.createObject(o), reader,
								provider));
					} else if (ch == '[') {
						list.add(deserialize(createObject(o), reader, provider));
					} else {
						list.add(reader.readObject());
					}
				}
				int ch = reader.readIgnoreWhitespace();
				if (ch == ']') {
					break;
				}
				if (ch != ',') {
					// TODO 出错
					throw new NullPointerException();
				}
			}
		}
		return list;
	}
}
