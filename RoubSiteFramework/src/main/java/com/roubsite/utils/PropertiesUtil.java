package com.roubsite.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.roubsite.web.filter.RoubSiteInjectDatabaseFilter;

public class PropertiesUtil {
	public static String getConfigString(String key) {
		return getString(key, "config.properties");
	}

	/**
	 * 指定文件读取配置
	 * 
	 * @param key
	 * @param propertiesFile
	 */
	public static String getString(String key, String propertiesFile) {

		Properties tmpProperties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(propertiesFile);
			BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
			tmpProperties.load(bf);
		} catch (Exception e) {
			RoubSiteInjectDatabaseFilter.logger.error("获取配置文件：" + propertiesFile + "的配置项失败", e);
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
					inputStream = null;
				} catch (Exception e) {
					RoubSiteInjectDatabaseFilter.logger.error("关闭流失败", e);
				}
			}
		}
		Object obj;
		return key == null || "".equals(key) ? "" : ((obj = tmpProperties.get(key)) == null ? "" : (String) obj);
	}

}
