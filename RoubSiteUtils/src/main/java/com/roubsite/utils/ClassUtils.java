package com.roubsite.utils;

import java.util.ArrayList;
import java.util.List;

public class ClassUtils {
	/**
	 * 根据servletPath字符串获取类名
	 *
	 * @param servletPath
	 * @return
	 */
	public ClassBean getClassBean(String servletPath) {
		ClassBean cb = new ClassBean();
		String defaultGroup = ConfUtils.getStringConf("RoubSite.global.defaultGroup", "");
		String[] servlet_path = servletPath.split("/");
		List<String> list = new ArrayList<>();
		list.add("");
		if (servlet_path.length < 2) {
			list.add(defaultGroup);
			list.add("index");
			list.add("excute");
		} else if (servlet_path.length < 3) {
			list.add(servlet_path[1]);
			list.add("index");
			list.add("excute");
		} else if (servlet_path.length < 4) {
			list.add(servlet_path[1]);
			list.add(servlet_path[2]);
			list.add("excute");
		} else if (servlet_path.length < 5) {
			list.add(servlet_path[1]);
			list.add(servlet_path[2]);
			list.add(servlet_path[3]);
		} else if (servlet_path.length >= 5) {
			list.add(servlet_path[1]);
			list.add(servlet_path[2]);
			list.add(servlet_path[3]);
		} else {
			list.add(defaultGroup);
			list.add("index");
			list.add("excute");
		}
		Object[] s = list.toArray();
		// 获取分组名
		cb.setTemplate(s[1].toString());
		// 获取类前缀
		String className = ConfUtils.getStringConf("RoubSite.global.group." + s[1], "");
		if (StringUtils.isNotEmpty(className)) {
			className = className + ".action";
			// 获取类名
			className = className + "." + capitalize(s[2].toString()) + "Action";
			cb.setClassPath(className);
			try {
				if ("excute".equals(s[3].toString())) {
					cb.setMethod("execute");
				} else {
					cb.setMethod("do" + capitalize(s[3].toString()));
				}
			} catch (Exception e) {
				cb.setMethod("execute");
			}

		} else {
			cb.setErroMessage("未找到分组:[" + s[1] + "]");
		}
		return cb;
	}

	/**
	 * 将字符串的首字母大写
	 */
	public String capitalize(String str) {
		StringBuilder sb = new StringBuilder();
		if (str != null && str.length() > 0) {
			sb.append(str.substring(0, 1).toUpperCase());
			if (str.length() > 1) {
				sb.append(str.substring(1));
			}
			return sb.toString();
		}
		return str;
	}
}
