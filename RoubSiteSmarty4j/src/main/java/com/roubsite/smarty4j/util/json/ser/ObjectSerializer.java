package com.roubsite.smarty4j.util.json.ser;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.roubsite.smarty4j.util.json.JsonReader;
import com.roubsite.smarty4j.util.json.Provider;
import com.roubsite.smarty4j.util.json.ser.Generic;
import com.roubsite.smarty4j.util.json.ser.Serializer;

public abstract class ObjectSerializer implements Serializer {

	public static class BeanItem {
		private int index;
		private Serializer serializer;
		private Type generic;
		
		public BeanItem(int index, Serializer serializer, Type generic) {
			this.index = index;
			this.serializer = serializer;
			this.generic = generic;
		}
	}

	public static final String NAME = ObjectSerializer.class.getName().replace('.', '/');

	private Map<String, BeanItem> items = new HashMap<String, BeanItem>();

	public abstract void setValue(Object o, int index, Object value);

	public void setNameIndex(String name, BeanItem item) {
		items.put(name, item);
	}

	@Override
	public Object deserialize(Object o, JsonReader reader, Provider provider) throws Exception {
		if (reader.read() != '{') {
			// TODO 异常
			throw new NullPointerException();
		}
		int ch = reader.read();
		if (ch == '}') {
			return o;
		}
		reader.unread();
		while (true) {
			BeanItem item = items.get(reader.readString());
			if (item == null) {
				// TODO 不存在的属性名
				throw new NullPointerException();
			}
			if (reader.readIgnoreWhitespace() != ':') {
				// TODO 异常
				throw new NullPointerException();
			}

			Object value;
			if (item.generic != null) {
				value = ((Generic) item.serializer).deserialize(item.serializer.createObject(o), reader, provider, item.generic);
			} else {
				value = item.serializer.deserialize(item.serializer.createObject(o), reader, provider);
			}
			setValue(o, item.index, value);
			ch = reader.readIgnoreWhitespace();
			if (ch == '}') {
				return o;
			}
			if (ch != ',') {
				// TODO
				throw new NullPointerException();
			}
		}
	}
}
