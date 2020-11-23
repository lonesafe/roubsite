package com.roubsite.smarty4j.util.json.ser;

import java.lang.reflect.Type;

import com.roubsite.smarty4j.util.json.JsonReader;
import com.roubsite.smarty4j.util.json.Provider;

public interface Generic {
	public Type getGeneric(Type type);

	public Object deserialize(Object o, JsonReader reader, Provider provider, Type generic) throws Exception;
}
