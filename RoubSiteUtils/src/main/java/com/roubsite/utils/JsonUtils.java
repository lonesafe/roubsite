package com.roubsite.utils;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {
	static ObjectMapper objectMapper;
	static {
		objectMapper = new ObjectMapper();
		// 从JSON到java object
		// 没有匹配的属性名称时不作失败处理
		objectMapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);

		// 反序列化
		// 禁止遇到空原始类型时抛出异常，用默认值代替。
		objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
		objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		// 禁止遇到未知（新）属性时报错，支持兼容扩展
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

		objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
		objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
		objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
		objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

		// 序列化
		// 禁止序列化空值
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);
		objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);

		objectMapper.configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, true);
		objectMapper.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, true);
	}

	/**
	 * 将Bean转换成json字符串
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static String convertToString(Object obj) throws IOException {
		if (obj == null)
			return null;

		return objectMapper.writeValueAsString(obj);
	}

	/**
	 * 将json字符串转换成Bean
	 * 
	 * @param str
	 *            json字符串
	 * @param cla
	 *            Bean类
	 * @return
	 * @throws IOException
	 */
	public static <T> T readToObject(String str, Class<T> cla) throws IOException {
		if (str == null)
			return null;

		return objectMapper.readValue(str, cla);
	}
}
