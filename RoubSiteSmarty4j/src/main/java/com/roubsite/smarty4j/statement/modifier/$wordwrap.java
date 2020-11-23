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
 * Wraps a string to a column width, the default is 80. As an optional second parameter, you can
 * specify a string of text to wrap the text to the next line, the default is a carriage return
 * "\n". By default, wordwrap will attempt to wrap at a word boundary. If you want to cut off at the
 * exact character length, pass the optional third parameter as TRUE.
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
 * <td>This determines how many columns to wrap to.</td>
 * </tr>
 * <tr>
 * <td align="center">2</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center">\n</td>
 * <td>This is the string used to wrap words with.</td>
 * </tr>
 * <tr>
 * <td align="center">3</td>
 * <td align="center">boolean</td>
 * <td align="center">No</td>
 * <td align="center"><code class="constant">FALSE</code></td>
 * <td>This determines whether or not to wrap at a word boundary (
 * <code class="constant">FALSE</code>), or at the exact character (
 * <code class="constant">TRUE</code>).</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "Blind woman gets new kidney from dad she hasn't seen in years.");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle}<br>
 * {$articleTitle|wordwrap:30}<br>
 * {$articleTitle|truncate:20}<br>
 * {$articleTitle|truncate:30:'&lt;br /&gt;\n'}<br>
 * {$articleTitle|truncate:25:'\n':true}<br>
 * <br>
 * <b>Output:</b><br>
 * Blind woman gets new kidney from dad she hasn't seen in years.<br>
 * Blind woman gets new kidney<br>
 * from dad she hasn't seen in<br>
 * years.<br>
 * Blind woman gets new<br>
 * kidney from dad she<br>
 * hasn't seen in<br>
 * years.<br>
 * Blind woman gets new kidney&lt;br /&gt;<br>
 * from dad she hasn't seen in&lt;br /&gt;<br>
 * years.<br>
 * Blind woman gets new kidn<br>
 * ey from dad she hasn't se<br>
 * en in years.<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $wordwrap extends Modifier {

	private static final Definition[] definitions = {
	    Definition.forModifier(Type.INTOBJ, new ConstInteger(80)),
	    Definition.forModifier(Type.STROBJ, new StringExpression("\n")),
	    Definition.forModifier(Type.BOOLEAN, FalseCheck.VALUE) };

	private static Pattern p = Pattern.compile(" *(([A-Za-z_']+|[0-9]+\\.[0-9]+)([\\.,?!%])?|.)");

	public static Object execute(Object obj, int len, String replacement, boolean wordBoundary) {
		String s = obj.toString();
		StringBuilder buf = new StringBuilder(s.length() * 2);

		if (wordBoundary) {
			while (s.length() > len) {
				buf.append(s.substring(0, len));
				buf.append(replacement);
				s = s.substring(len);
			}
		} else {
			while (s.length() > len) {
				Matcher m = p.matcher(s);
				while (m.find()) {
					if (m.end() > len) {
						int index = m.start();
						if (index == 0) {
							buf.append(s.substring(m.start(1), m.end()));
							buf.append(replacement);
							s = s.substring(m.end());
						} else {
							buf.append(s.substring(0, index));
							buf.append(replacement);
							s = s.substring(m.start(1));
						}
						break;
					}
				}
			}
		}
		buf.append(s);
		return buf.toString();
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}