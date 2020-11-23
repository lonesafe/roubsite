package com.roubsite.database.bean;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.roubsite.utils.JsonUtils;

public class Record {
	public static final int STATE_NONE = 0;
	public static final int STATE_INSERT = 1;
	public static final int STATE_UPDATE = 3;
	public static final int STATE_DELETED = 2;
	private HashMap<String, Object> data = new HashMap<String, Object>();
	private int state = 0;

	public Record() {
	}

	public Record(Object bean) {
		fromBean(bean);
	}

	public HashMap<String, Object> getData() {
		return data;
	}

	public void fromBeanMap(Map BeabMap) {
		Map map = new HashMap<>();
		Iterator<Map.Entry> it = BeabMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = it.next();
			String key = (String) entry.getKey();
			Pattern pattern = Pattern.compile("[A-Z]");
			Matcher matcher = pattern.matcher(key);
			StringBuffer sbr = new StringBuffer();
			while (matcher.find()) {
				matcher.appendReplacement(sbr, "_" + matcher.group());
			}
			matcher.appendTail(sbr);
			key = sbr.toString();
			key = key.toUpperCase();
			map.put(key, entry.getValue());
		}
		this.data = (HashMap<String, Object>) map;
	}

	public void fromMap(Map map) {
		Map retMap = new HashMap<>();
		Iterator<Map.Entry> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = it.next();
			String key = (String) entry.getKey();
			key = key.toUpperCase();
			retMap.put(key, entry.getValue());
		}
		this.data = (HashMap<String, Object>) retMap;
	}

	public void fromJson(String json) {
		Map jsonMap = new HashMap<>();
		try {
			jsonMap = JsonUtils.readToObject(json, HashMap.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map map = new HashMap<>();
		Iterator<Map.Entry> it = jsonMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = it.next();
			String key = (String) entry.getKey();
			Pattern pattern = Pattern.compile("[A-Z]");
			Matcher matcher = pattern.matcher(key);
			StringBuffer sbr = new StringBuffer();
			while (matcher.find()) {
				matcher.appendReplacement(sbr, "_" + matcher.group());
			}
			matcher.appendTail(sbr);
			key = sbr.toString();
			key = key.toUpperCase();

			map.put(key, entry.getValue());

		}
		this.data = (HashMap<String, Object>) map;
	}

	public void fromBean(Object bean) {
		Class type = bean.getClass();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				String propertyName = descriptor.getName();
				if (!propertyName.equals("class")) {

					// 将驼峰命名法转为数据库命名法begin
					Pattern pattern = Pattern.compile("[A-Z]");
					Matcher matcher = pattern.matcher(propertyName);
					StringBuffer sbr = new StringBuffer();
					while (matcher.find()) {
						matcher.appendReplacement(sbr, "_" + matcher.group());
					}
					matcher.appendTail(sbr);
					propertyName = sbr.toString();
					propertyName = propertyName.toUpperCase();
					// 将驼峰命名法转为数据库命名法end
					
					Method readMethod = descriptor.getReadMethod();
					Object result = readMethod.invoke(bean, new Object[0]);
					if (result != null) {
						this.data.put(propertyName, result);
					} else {
						this.data.put(propertyName, "");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object toBean(Class clazz) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz); // 获取类属性
			Object obj = clazz.newInstance(); // 创建 JavaBean 对象
			if (null == this.data) {
				return obj;
			}
			// 给 JavaBean 对象的属性赋值
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				String propertyName = descriptor.getName();

				Pattern pattern = Pattern.compile("[A-Z]");
				Matcher matcher = pattern.matcher(propertyName);
				StringBuffer sbr = new StringBuffer();
				while (matcher.find()) {
					matcher.appendReplacement(sbr, "_" + matcher.group());
				}
				matcher.appendTail(sbr);
				propertyName = sbr.toString();
				propertyName = propertyName.toUpperCase();
				if (this.data.containsKey(propertyName)) {
					try {
						Object value = this.data.get(propertyName);
						if (null != value) {
							Object[] args = new Object[1];
							args[0] = transform(descriptor.getPropertyType(), value);
							descriptor.getWriteMethod().invoke(obj, args);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("转换Bean失败！");
		}
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void set(String name, Object o) {
		this.data.put(name, o);
	}

	public Iterator keys() {
		return this.data.keySet().iterator();
	}

	public boolean containsKey(String key) {
		return this.data.containsKey(key);
	}

	public Object get(String name) {
		return this.data.get(name);
	}

	private Object transform(Class paramClass, Object paramObject) {
		if (paramObject == null)
			return null;
		if (paramClass == String.class)
			return String.valueOf(paramObject);
		if ((paramClass == Character.TYPE) || (paramClass == Character.class)) {
			String str = String.valueOf(paramObject);
			if (str.length() > 0)
				return Character.valueOf(str.charAt(0));
			return null;
		}
		if ((paramClass == Boolean.TYPE) || (paramClass == Boolean.class)) {
			if (paramObject instanceof Boolean)
				return paramObject;
			return Boolean.valueOf(Boolean.parseBoolean(String.valueOf(paramObject)));
		}
		if ((paramClass.isPrimitive()) || (Number.class.isAssignableFrom(paramClass)))
			return toNumber(paramClass, paramObject);
		if (paramClass == Timestamp.class) {
			if ("".equals(paramObject))
				return null;
			if (paramObject instanceof java.util.Date)
				return new Timestamp(((java.util.Date) paramObject).getTime());
			return paramObject;
		}
		if (paramClass == java.sql.Date.class) {
			if ("".equals(paramObject))
				return null;
			if (paramObject instanceof java.util.Date)
				return new java.sql.Date(((java.util.Date) paramObject).getTime());
			return paramObject;
		}
		if ("".equals(paramObject))
			return null;
		return paramObject;
	}

	private Object toNumber(Class paramClass, Object paramObject) {
		if ((paramObject == null) || (paramObject.equals("")))
			return null;
		if ((paramClass == Integer.TYPE) || (paramClass == Integer.class)) {
			if (paramObject instanceof Number)
				return Integer.valueOf(((Number) paramObject).intValue());
			return Integer.valueOf(Integer.parseInt(String.valueOf(paramObject)));
		}
		if ((paramClass == Float.TYPE) || (paramClass == Float.class)) {
			if (paramObject instanceof Number)
				return Float.valueOf(((Number) paramObject).floatValue());
			return Float.valueOf(Float.parseFloat(String.valueOf(paramObject)));
		}
		if ((paramClass == Double.TYPE) || (paramClass == Double.class)) {
			if (paramObject instanceof Number)
				return Double.valueOf(((Number) paramObject).doubleValue());
			return Double.valueOf(Double.parseDouble(String.valueOf(paramObject)));
		}
		if ((paramClass == Long.class) || (paramClass == Long.TYPE)) {
			if (paramObject instanceof Number)
				return Long.valueOf(((Number) paramObject).longValue());
			return Long.valueOf(Long.parseLong(String.valueOf(paramObject)));
		}
		if ((paramClass == Short.TYPE) || (paramClass == Short.class)) {
			if (paramObject instanceof Number)
				return Short.valueOf(((Number) paramObject).shortValue());
			return Short.valueOf(Short.parseShort(String.valueOf(paramObject)));
		}
		if ((paramClass == Byte.TYPE) || (paramClass == Byte.class)) {
			if (paramObject instanceof Number)
				return Byte.valueOf(((Number) paramObject).byteValue());
			return Byte.valueOf(Byte.parseByte(String.valueOf(paramObject)));
		}
		if (paramClass == BigDecimal.class)
			return new BigDecimal(String.valueOf(paramObject));
		if (paramClass == BigInteger.class)
			return new BigInteger(String.valueOf(paramObject));
		return paramObject;
	}
}
