package com.roubsite.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class YmlUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(YmlUtils.class);
	private static Map<String, Object> config = new LinkedHashMap<>();
	static {
		config = initYaml();
	}

	private static Map<String, Object> initYaml() {
		// 返回的结果
		Map<String, Object> result = new LinkedHashMap<>();
		// 从classpath下获取配置文件路径
		Yaml yaml = new Yaml();
		// 获取class下所有yml文件
		String[] ymlList = new File(YmlUtils.class.getResource("/").getPath()).list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".yml") || name.endsWith(".yaml");
			}
		});
		// 通过yaml对象将配置文件的输入流转换成map原始map对象
		if (StringUtils.isNotEmptyObject(ymlList)) {
			for (String ymlName : ymlList) {
				String yamlContent = new FileUtils()
						.fileGetContent(YmlUtils.class.getResource("/").getPath() + ymlName);
				Object object = yaml.load(yamlContent);
				// 这里只是简单处理，需要多个方式可以自己添加
				if (object instanceof Map) {
					Map map = (Map) object;
					buildFlattenedMap(result, map, null);
				}
//				config.putAll(yaml.load(yamlContent));
			}
		}
		return result;
	}

	/**
	 * 
	 * @param result
	 * @param source
	 * @param path
	 */
	private static void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
		// 循环读取原数据
		source.forEach((key, value) -> {
			// 如果存在路径进行拼接
			if (StringUtils.hasText(path)) {
				if (key.startsWith("[")) {
					key = path + key;
				} else {
					key = path + '.' + key;
				}
			}
			// 数据类型匹配
			if (value instanceof String) {
				result.put(key, value);
			} else if (value instanceof Map) {
				// 如果是map,就继续读取
				Map<String, Object> map = (Map) value;
				buildFlattenedMap(result, map, key);
			} else if (value instanceof Collection) {
				Collection<Object> collection = (Collection) value;
				if (collection.isEmpty()) {
					result.put(key, "");
				} else {
					int count = 0;
					Iterator var7 = collection.iterator();

					while (var7.hasNext()) {
						Object object = var7.next();
						buildFlattenedMap(result, Collections.singletonMap("[" + count++ + "]", object), key);
					}
				}
			} else {
				result.put(key, value != null ? value : "");
			}
		});
	}

	public static Object getConfig(String keys) {
		Object value = config.get(keys);
		if (!StringUtils.isNotEmptyObject(value)) {
			LOGGER.warn("不存在的yaml配置" + keys);
			value = null;
		}
		return value;
	}

	public static Map<String, Object> getAllConfig() {
		return new HashMap<String, Object>(config);
	}
}
