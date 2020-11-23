package com.roubsite.smarty4j.statement.modifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.roubsite.smarty4j.statement.Modifier;

/**
 * This is used to count the number of paragraphs in a variable.
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "War Dims Hope for Peace. Child's Death Ruins Couple's Holiday.\n\nMan is Fatally Slain. Death Causes Loneliness, Feeling of Isolation.");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle}<br>
 * {$articleTitle|count_paragraphs}<br>
 * <br>
 * <b>Output:</b><br>
 * War Dims Hope for Peace. Child's Death Ruins Couple's Holiday.<br>
 * <br>
 * Man is Fatally Slain. Death Causes Loneliness, Feeling of Isolation.<br>
 * 2<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $count_paragraphs extends Modifier {

	private static final Pattern p = Pattern
	    .compile(" *[\\x00-\\x09\\x0b-\\x1f\\u0021-\\uffff]+(\\x0a|\\z)");

	/**
	 * This is used to count the number of paragraphs in a variable.
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