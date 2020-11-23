package com.roubsite.smarty4j.statement.modifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.roubsite.smarty4j.statement.Modifier;

/**
 * This is used to count the number of sentences in a variable. A sentence being delimited by a dot,
 * question- or exclamation-mark (.?!).
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "Two Soviet Ships Collide - One Dies. Enraged Cow Injures Farmer with Axe.");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle}<br>
 * {$articleTitle|count_sentences}<br>
 * <br>
 * <b>Output:</b><br>
 * Two Soviet Ships Collide - One Dies. Enraged Cow Injures Farmer with Axe.<br>
 * 2<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $count_sentences extends Modifier {

	private static Pattern p = Pattern.compile(" *[\\x00-\\x1f\\x21-\\x2d\\u002f-\\uffff]+[\\.\\?!]");

	/**
	 * This is used to count the number of sentences in a variable.
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