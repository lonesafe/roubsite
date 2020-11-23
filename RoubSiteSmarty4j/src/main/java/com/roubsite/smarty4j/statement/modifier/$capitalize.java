package com.roubsite.smarty4j.statement.modifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.roubsite.smarty4j.expression.check.FalseCheck;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Modifier;

/**
 * This is used to capitalize the first letter of all words in a variable.
 * 
 * <table border="1">
 * <colgroup> <col align="center" class="param"> <col align="center" class="type"> <col
 * align="center" class="required"> <col align="center" class="default"> <col class="desc">
 * </colgroup> <thead>
 * <tr>
 * <th align="center">Parameter Position</th>
 * <th align="center">Type</th>
 * <th align="center">Required</th>
 * <th align="center">Default</th>
 * <th>Description</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td align="center">1</td>
 * <td align="center">boolean</td>
 * <td align="center">No</td>
 * <td align="center"><code class="constant">FALSE</code></td>
 * <td>This determines whether or not words with digits will be uppercased</td>
 * </tr>
 * <tr>
 * <td align="center">2</td>
 * <td align="center">boolean</td>
 * <td align="center">No</td>
 * <td align="center"><code class="constant">FALSE</code></td>
 * <td>This determines whether or not Capital letters within words should be lowercased, e.g. "aAa"
 * to "Aaa"</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "next x-men film, x3, delayed.");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle}<br>
 * {$articleTitle|capitalize}<br>
 * {$articleTitle|capitalize:true}<br>
 * <br>
 * <b>Output:</b><br>
 * next x-men film, x3, delayed.<br>
 * Next X-Men Film, x3, Delayed.<br>
 * Next X-Men Film, X3, Delayed.<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $capitalize extends Modifier {

	private static final Definition[] definitions = {
	    Definition.forModifier(Type.BOOLEAN, FalseCheck.VALUE),
	    Definition.forModifier(Type.BOOLEAN, FalseCheck.VALUE) };

	private static final Pattern p1 = Pattern.compile("\\p{Alpha}+([\\s\\p{Punct}]|$)");
	private static final Pattern p2 = Pattern.compile("\\p{Alpha}\\w*([\\s\\p{Punct}]|$)");

	/**
	 * This is used to capitalize the first letter of all words in a variable.
	 * 
	 * @param obj
	 * @param allowNumber
	 * @param autoToLower
	 * @return
	 */
	public static Object execute(Object obj, boolean allowNumber, boolean autoToLower) {
		Pattern p = allowNumber ? p2 : p1;
		StringBuilder buf = new StringBuilder(obj.toString());
		Matcher m = p.matcher(buf);
		while (m.find()) {
			int start = m.start();
			int end = m.end();
			buf.setCharAt(start, Character.toUpperCase(buf.charAt(start)));
			if (autoToLower) {
				for (int i = start + 1; i < end; i++) {
					char c = buf.charAt(i);
					if (Character.isUpperCase(c)) {
						buf.setCharAt(i, Character.toLowerCase(c));
					}
				}
			}
		}
		return buf.toString();
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}