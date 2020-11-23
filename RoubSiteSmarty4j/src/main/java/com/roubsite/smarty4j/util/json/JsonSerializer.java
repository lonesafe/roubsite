package com.roubsite.smarty4j.util.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.ser.Serializer;

public class JsonSerializer {
	public static final String NAME = JsonSerializer.class.getName().replace('.', '/');

	private static class Recycler {
		private SimpleCharBuffer cb = new SimpleCharBuffer(4000);
		private JsonReader jr = new JsonReader();
	}

	private static final ThreadLocal<Recycler> threadLocal = new ThreadLocal<Recycler>();

	private Provider provider;

	public JsonSerializer() {
		this(new Provider());
	}

	public JsonSerializer(Provider provider) {
		this.provider = provider;
	}

	private Recycler getRecycler() {
		Recycler recycler = threadLocal.get();
		if (recycler == null) {
			recycler = new Recycler();
			threadLocal.set(recycler);
		}
		return recycler;
	}

	public String serialize(Object o) {
		SimpleCharBuffer cb = getRecycler().cb;
		serializeValue(o, cb, provider);
		String ret = cb.toString();
		cb.setLength(0);
		return ret;
	}

	public void serialize(Writer writer, Object o) throws IOException {
		SimpleCharBuffer cb = getRecycler().cb;
		cb.setWriter(writer);
		serializeValue(o, cb, provider);
		cb.flush();
	}

	public Object deserialize(Reader reader, Class<?> cc) throws Exception {
		Serializer serializer = provider.getSerializer(cc);
		JsonReader jsonReader = getRecycler().jr;
		jsonReader.bind(reader);
		if (jsonReader.readIgnoreWhitespace() == '[') {
			List<Object> list = new ArrayList<Object>();
			if (jsonReader.readIgnoreWhitespace() == ']') {
				return list;
			}
			jsonReader.unread();
			while (true) {
				list.add(serializer.deserialize(serializer.createObject(null), jsonReader, provider));
				int ch = jsonReader.readIgnoreWhitespace();
				if (ch == ']') {
					break;
				}
				if (ch != ',') {
					// TODO 出错
				}
			}
			return list;
		}
		jsonReader.unread();
		Object value = serializer.deserialize(serializer.createObject(null), jsonReader, provider);
		return value;
	}

	public static void serializeObject(Object o, SimpleCharBuffer cb, Provider provider) {
		provider.getSerializer(o.getClass()).serialize(o, cb, provider);
	}

	public static void serializeValue(Object o, SimpleCharBuffer cb, Provider provider) {
		if (o == null) {
			cb.appendNull();
		} else {
			serializeObject(o, cb, provider);
		}
	}
}