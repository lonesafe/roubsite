package com.roubsite.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfUtils.class);

	/**
	 * 获取yaml配置项
	 *
	 * @param key 配置项路径（通过.分割）
	 * @param defaultValue 默认值
	 * @return 值
	 */
	public static String getStringConf(String key, String defaultValue) {
		Object obj = YmlUtils.getConfig(key);
		if (StringUtils.isNotEmptyObject(obj)) {
			try {
				return obj.toString();
			} catch (Exception e) {
				LOGGER.warn("读取配置项[" + key + "]错误", e);
				return defaultValue;
			}
		} else {
			return defaultValue;
		}

	}

	public static List<Object> getListConf(String key) {
		Object obj = YmlUtils.getConfig(key);
		if (StringUtils.isNotEmptyObject(obj)) {
			try {
				return (List<Object>) obj;
			} catch (Exception e) {
				LOGGER.warn("读取配置项[" + key + "]错误", e);
				return new ArrayList<Object>();
			}
		} else {
			return new ArrayList<Object>();
		}
	}

	public static int getIntConf(String key) {
		Object obj = YmlUtils.getConfig(key);
		if (StringUtils.isNotEmptyObject(obj)) {
			try {
				return Integer.parseInt(String.valueOf(obj));
			} catch (Exception e) {
				LOGGER.warn("读取配置项[" + key + "]错误", e);
				return 0;
			}
		} else {
			return 0;
		}
	}

	public static long getLongConf(String key) {
		Object obj = YmlUtils.getConfig(key);
		if (StringUtils.isNotEmptyObject(obj)) {
			try {
				return Long.parseLong(String.valueOf(obj));
			} catch (Exception e) {
				LOGGER.warn("读取配置项[" + key + "]错误", e);
				return 0;
			}
		} else {
			return 0;
		}
	}

	public static double getDoubleConf(String key) {
		Object obj = YmlUtils.getConfig(key);
		if (StringUtils.isNotEmptyObject(obj)) {
			try {
				return Double.parseDouble(String.valueOf(obj));
			} catch (Exception e) {
				LOGGER.warn("读取配置项[" + key + "]错误", e);
				return 0;
			}
		} else {
			return 0;
		}
	}

	public static boolean getBooleanConf(String key) {
		Object obj = YmlUtils.getConfig(key);
		if (StringUtils.isNotEmptyObject(obj)) {
			try {
				return Boolean.parseBoolean(String.valueOf(obj));
			} catch (Exception e) {
				LOGGER.warn("读取配置项[" + key + "]错误", e);
				return false;
			}
		} else {
			return false;
		}
	}

	public static Object getConfig(String key) {
		return YmlUtils.getConfig(key);
	}
}
