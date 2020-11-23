package com.roubsite.smarty4j.util.json.ser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.JsonReader;
import com.roubsite.smarty4j.util.json.JsonSerializer;
import com.roubsite.smarty4j.util.json.Provider;

public class MapSerializer implements Serializer, Generic {

	public static final MapSerializer instance = new MapSerializer();
	
	private MapSerializer() {		
	}

	public static void $serialize(Map<?, ?> o, SimpleCharBuffer cb, Provider provider, Class<?> generic) {
		Serializer serializer = provider.getSerializer(generic);
		cb.append('{');
		for (Map.Entry<?, ?> entry : o.entrySet()) {
			Object key = entry.getKey();
			if (key != null) {
				cb.appendString(key.toString());
				cb.append(':');
				serializer.serialize(entry.getValue(), cb, provider);
				cb.append(',');
			}
		}
		cb.appendClose('}');
	}

	public static void $serialize(Map<?, ?> o, SimpleCharBuffer cb, Provider provider) {
		cb.append('{');
		for (Map.Entry<?, ?> entry : o.entrySet()) {
			Object key = entry.getKey();
			if (key != null) {
				cb.appendString(key.toString());
				cb.append(':');
				JsonSerializer.serializeValue(entry.getValue(), cb, provider);
				cb.append(',');
			}
		}
		cb.appendClose('}');
	}

	@Override
	public void serialize(Object o, SimpleCharBuffer cb, Provider provider) {
		$serialize((Map<?, ?>) o, cb, provider);
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
			return ((ParameterizedType) type).getActualTypeArguments()[1];
		}
		return null;
	}

	@Override
	public Object deserialize(Object o, JsonReader reader, Provider provider, Type generic) throws Exception {
		if (reader.readIgnoreWhitespace() != '{') {
			// TODO 出错
			throw new NullPointerException();
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if (reader.readIgnoreWhitespace() != '}') {
			reader.unread();
			while (true) {
				String name = reader.readString();
				if (reader.readIgnoreWhitespace() != ':') {
					// TODO 异常
					throw new NullPointerException();
				}
				if (generic instanceof Class) {
					Serializer serializer = provider.getSerializer((Class<?>) generic);
					map.put(name, serializer.deserialize(serializer.createObject(o), reader, provider));
				} else if (generic instanceof ParameterizedType) {
					Serializer serializer = provider
							.getSerializer((Class<?>) ((ParameterizedType) generic).getRawType());
					if (serializer instanceof Generic) {
						map.put(name, ((Generic) serializer).deserialize(serializer.createObject(o), reader, provider,
								((Generic) serializer).getGeneric(generic)));
					} else {
						map.put(name, serializer.deserialize(serializer.createObject(o), reader, provider));
					}
				} else {
					int ch = reader.readIgnoreWhitespace();
					reader.unread();
					if (ch == '{') {
						map.put(name, deserialize(createObject(o), reader, provider));
					} else if (ch == '[') {
						map.put(name, ListSerializer.instance.deserialize(ListSerializer.instance.createObject(o), reader, provider));
					} else {
						map.put(name, reader.readObject());
					}
				}
				int ch = reader.readIgnoreWhitespace();
				if (ch == '}') {
					break;
				}
				if (ch != ',') {
					// TODO 出错
					throw new NullPointerException();
				}
			}
		}
		return map;
	}
}
