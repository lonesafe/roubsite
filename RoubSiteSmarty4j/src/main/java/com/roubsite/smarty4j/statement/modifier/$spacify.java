package com.roubsite.smarty4j.statement.modifier;

import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Modifier;

/**
 * This is a way to insert a space between every character of a variable. You can optionally pass a
 * different character or string to insert.
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
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>one space</em></span></td>
 * <td>This what gets inserted between each character of the variable.</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "Something Went Wrong in Jet Crash, Experts Say.");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle}<br>
 * {$articleTitle|spacify}<br>
 * {$articleTitle|spacify:"^^"}<br>
 * <br>
 * <b>Output:</b><br>
 * Child's Stool Great for Use in Garden.<br>
 * Child's Stool Great for Use in Vineyard.<br>
 * Child's   Stool   Great   for   Use   in   Garden.<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $spacify extends Modifier {

	private static final Definition[] definitions = { Definition.forModifier(Type.STROBJ,
	    new StringExpression(" ")) };

	public static Object execute(Object obj, String value) {
		StringBuilder buf = new StringBuilder(64);

		String s = obj.toString();
		buf.append(s.charAt(0));
		for (int i = 1, len = s.length(); i < len; i++) {
			buf.append(value).append(s.charAt(i));
		}

		return buf.toString();
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}