package com.roubsite.smarty4j.statement.modifier;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import com.roubsite.smarty4j.statement.Modifier;

/**
 * 统计数组或字符串长度，或者集合的元素个数。
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $count extends Modifier {

	/**
	 * This is used to count the number of words in a variable.
	 * 
	 * @param obj
	 * @return
	 */
	public static Object execute(Object obj) {
		if (obj instanceof List) {
			return ((List<?>) obj).size();
		} else if (obj.getClass().isArray()) {
			return Array.getLength(obj);
		} else if (obj instanceof Map) {
			return ((Map<?, ?>) obj).size();
		} else if (obj instanceof String) {
			return obj.toString().length();
		} else {
			return 1;
		}
	}
}