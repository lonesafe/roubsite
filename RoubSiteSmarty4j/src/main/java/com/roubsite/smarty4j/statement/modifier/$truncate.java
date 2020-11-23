package com.roubsite.smarty4j.statement.modifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.expression.check.FalseCheck;
import com.roubsite.smarty4j.expression.number.ConstInteger;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Modifier;

/**
 * This truncates a variable to a character length, the default is 80. As an optional second
 * parameter, you can specify a string of text to display at the end if the variable was truncated.
 * The characters in the string are included with the original truncation length. By default,
 * truncate will attempt to cut off at a word boundary. If you want to cut off at the exact
 * character length, pass the optional third parameter of TRUE.
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
 * <td align="center">integer</td>
 * <td align="center">No</td>
 * <td align="center">80</td>
 * <td>This determines how many characters to truncate to.</td>
 * </tr>
 * <tr>
 * <td align="center">2</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center">...</td>
 * <td>This is a text string that replaces the truncated text. Its length is included in the
 * truncation length setting.</td>
 * </tr>
 * <tr>
 * <td align="center">3</td>
 * <td align="center">boolean</td>
 * <td align="center">No</td>
 * <td align="center"><code class="constant">FALSE</code></td>
 * <td>This determines whether or not to truncate at a word boundary with
 * <code class="constant">FALSE</code>, or at the exact character with
 * <code class="constant">TRUE</code>.</td>
 * </tr>
 * <tr>
 * <td align="center">4</td>
 * <td align="center">boolean</td>
 * <td align="center">No</td>
 * <td align="center"><code class="constant">FALSE</code></td>
 * <td>This determines whether the truncation happens at the end of the string with
 * <code class="constant">FALSE</code>, or in the middle of the string with
 * <code class="constant">TRUE</code>. Note that if this setting is
 * <code class="constant">TRUE</code>, then word boundaries are ignored.</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "Two Sisters Reunite after Eighteen Years at Checkout Counter.");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle}<br>
 * {$articleTitle|truncate}<br>
 * {$articleTitle|truncate:30}<br>
 * {$articleTitle|truncate:30:''}<br>
 * {$articleTitle|truncate:30:'---'}<br>
 * {$articleTitle|truncate:30:'':true}<br>
 * {$articleTitle|truncate:30:'...':true}<br>
 * {$articleTitle|truncate:30:'..':true:true}<br>
 * <br>
 * <b>Output:</b><br>
 * Two Sisters Reunite after Eighteen Years at Checkout Counter.<br>
 * Two Sisters Reunite after Eighteen Years at Checkout Counter.<br>
 * Two Sisters Reunite after...<br>
 * Two Sisters Reunite after<br>
 * Two Sisters Reunite after---<br>
 * Two Sisters Reunite after Eigh<br>
 * Two Sisters Reunite after E...<br>
 * Two Sisters Re..ckout Counter.<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $truncate extends Modifier {

	private static final Definition[] definitions = {
	    Definition.forModifier(Type.INTOBJ, new ConstInteger(80)),
	    Definition.forModifier(Type.STROBJ, new StringExpression("...")),
	    Definition.forModifier(Type.BOOLEAN, FalseCheck.VALUE),
	    Definition.forModifier(Type.BOOLEAN, FalseCheck.VALUE) };

	private static Pattern p = Pattern.compile(" *([A-Za-z0-9_']+|.)");

	public static Object execute(Object obj, int len, String replacement, boolean wordBoundary,
	    boolean middle) {
		if(null == obj) {
			obj = "";
		}
		String s = obj.toString();
		int total = s.length();

		if (len >= total) {
			return s;
		}
		int size = replacement.length();
		if (size > len) {
			return replacement;
		}

		if (middle) {
			int index = (len - size) / 2;
			return s.substring(0, index) + replacement + s.substring(total - index);
		} else {
			int index = len - size;
			if (!wordBoundary) {
				Matcher m = p.matcher(s);
				while (m.find()) {
					if (m.end() > index) {
						index = m.start();
						break;
					}
				}
			}
			return s.substring(0, index) + replacement;
		}
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}