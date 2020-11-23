package com.roubsite.smarty4j.util.json.ser;

import java.io.IOException;

import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.JsonReader;
import com.roubsite.smarty4j.util.json.Provider;

public class CharacterSerializer implements Serializer {

	public static final CharacterSerializer instance = new CharacterSerializer();
	
	private CharacterSerializer() {		
	}

	public static void $serialize(Character o, SimpleCharBuffer cb, Provider provider) {
		cb.appendString(o.charValue());
	}

	@Override
	public void serialize(Object o, SimpleCharBuffer cb, Provider provider) {
		$serialize((Character) o, cb, provider);
	}

	@Override
	public Object createObject(Object parent) {
		return null;
	}

	@Override
	public Object deserialize(Object o, JsonReader reader, Provider provider) throws IOException {
		String s = reader.readString();
		if (s.length() != 1) {
			// TODO 错误
			throw new NullPointerException();
		}
		return Character.valueOf(s.charAt(0));
	}
}
