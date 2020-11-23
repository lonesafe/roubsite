package com.roubsite.smarty4j.statement.modifier;

import com.roubsite.smarty4j.expression.check.FalseCheck;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Modifier;

/**
 * This is used to count the number of characters in a variable.
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
 * <td>This determines whether or not to include whitespace characters in the count.</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "Cold Wave Linked to Temperatures.");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle}<br>
 * {$articleTitle|count_characters}<br>
 * {$articleTitle|count_characters:true}<br>
 * <br>
 * <b>Output:</b><br>
 * Cold Wave Linked to Temperatures.<br>
 * 29<br>
 * 33<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $count_characters extends Modifier {

	private static final Definition[] definitions = { Definition.forModifier(Type.BOOLEAN, FalseCheck.VALUE) };

	/**
	 * This is used to count the number of characters in a variable.
	 * 
	 * @param obj
	 * @param countSpace
	 * @return
	 */
	public static Object execute(Object obj, boolean countSpace) {
		String s = obj.toString();
		int ret = s.length();
		if (!countSpace) {
			for (int i = ret - 1; i >= 0; i--) {
				if (Character.isSpaceChar(s.charAt(i))) {
					ret--;
				}
			}
		}
		return ret;
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}