package com.roubsite.smarty4j.statement.modifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.roubsite.smarty4j.statement.Modifier;

/**
 * This is used to count the number of words in a variable.
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "Dealers Will Hear Car Talk at Noon.");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle}<br>
 * {$articleTitle|count_words}<br>
 * <br>
 * <b>Output:</b><br>
 * Dealers Will Hear Car Talk at Noon.<br>
 * 7<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $count_words extends Modifier {

	private static Pattern p = Pattern.compile("\\p{Alpha}+");

	/**
	 * This is used to count the number of words in a variable.
	 * 
	 * @param obj
	 * @return
	 */
	public static Object execute(Object obj) {
		Matcher m = p.matcher(obj.toString());
		int i = 0;
		while (m.find()) {
			i++;
		}
		return i;
	}
}