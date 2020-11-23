package com.roubsite.code.dao;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeUtils {
	public static String getModeName(String tableName) {
		// 快速检查
		if (tableName == null || tableName.isEmpty()) {
			// 没必要转换
			return "";
		} else {
			tableName = tableName.toLowerCase();
		}
		if (!tableName.contains("_")) {
			// 不含下划线，仅将首字母大写
			return tableName.substring(0, 1).toUpperCase() + tableName.substring(1);
		}
		// 首字母大写
		tableName = tableName.substring(0, 1).toUpperCase() + tableName.substring(1);
		Pattern linePattern = Pattern.compile("_(\\w)");
		Matcher matcher = linePattern.matcher(tableName);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
}
