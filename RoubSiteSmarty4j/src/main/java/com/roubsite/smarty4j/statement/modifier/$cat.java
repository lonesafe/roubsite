package com.roubsite.smarty4j.statement.modifier;

import com.roubsite.smarty4j.expression.NullExpression;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Modifier;

/**
 * This value is concatenated to the given variable.
 * 
 * <table border="1">
 * <colgroup> <col align="center" class="param"> <col align="center" class="type"> <col
 * align="center" class="required"> <col align="center" class="cat"> <col class="desc"> </colgroup>
 * <thead>
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
 * <td align="center"><span class="emphasis"><em>empty</em></span></td>
 * <td>This value to catenate to the given variable.</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "Psychics predict world didn't end");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle|cat:' yesterday.'}<br>
 * <br>
 * <b>Output:</b><br>
 * Psychics predict world didn't end yesterday.<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $cat extends Modifier {

	private static final Definition[] definitions = { Definition.forModifier(Type.OBJECT,
	    NullExpression.VALUE) };

	/**
	 * This value is concatenated to the given variable.
	 * 
	 * @param obj
	 * @param value
	 * @return
	 */
	public static Object execute(Object obj, Object value) {
		if (value == null) {
			return obj;
		}
		String v = value.toString();
		if (v.length() > 0) {
			return obj.toString() + v;
		} else {
			return obj;
		}
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}