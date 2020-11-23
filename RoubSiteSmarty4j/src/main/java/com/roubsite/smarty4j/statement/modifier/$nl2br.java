package com.roubsite.smarty4j.statement.modifier;

import com.roubsite.smarty4j.statement.Modifier;

/**
 * All "\n" line breaks will be converted to html &lt;br /&gt; tags in the given variable.
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "Sun or rain expected\ntoday, dark tonight");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle|nl2br}<br>
 * <br>
 * <b>Output:</b><br>
 * Sun or rain expected&lt;br /&gt;today, dark tonight<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $nl2br extends Modifier {

	public static Object execute(Object obj) {
		StringBuilder buf = new StringBuilder(64);
		String text = obj.toString();
		int size = text.length();
		for (int i = 0; i < size; i++) {
			char c = text.charAt(i);
			if (c == '\n') {
				buf.append("<br />");
			} else {
				buf.append(c);
			}
		}
		return buf.toString();
	}
}