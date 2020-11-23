package com.roubsite.smarty4j.statement.modifier;

import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.expression.number.ConstInteger;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Modifier;

/**
 * This indents a string on each line, default is 4. As an optional parameter, you can specify the
 * number of characters to indent. As an optional second parameter, you can specify the character to
 * use to indent with eg use "\t" for a tab.
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
 * <td align="center">4</td>
 * <td>This determines how many characters to indent to.</td>
 * </tr>
 * <tr>
 * <td align="center">2</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center">(one space)</td>
 * <td>This is the character used to indent with.</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "NJ judge to rule on nude beach.\nSun or rain expected today, dark tonight.\nStatistics show that teen pregnancy drops off significantly after 25.");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle}<br>
 * {$articleTitle|indent}<br>
 * {$articleTitle|indent:10}<br>
 * {$articleTitle|indent:1:'\t'}<br>
 * <br>
 * <b>Output:</b><br>
 * NJ judge to rule on nude beach.<br>
 * Sun or rain expected today, dark tonight.<br>
 * Statistics show that teen pregnancy drops off significantly after 25.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;NJ judge to rule on nude beach.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Sun or rain expected today, dark tonight.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Statistics show that teen pregnancy drops off significantly after 25.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;NJ judge to rule on nude beach.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Sun or rain expected today, dark tonight.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Statistics show that teen pregnancy drops off significantly after 25.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;NJ judge to rule on nude beach.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Sun or rain expected today, dark tonight.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Statistics show that teen pregnancy drops off significantly after 25.<br>
 * </code>
 * 
 * <pre>
 * { &quot;kick\ntest\n&quot; | indent }
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 *     kick
 *     test
 * </pre>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class $indent extends Modifier {

	private static final Definition[] definitions = {
	    Definition.forModifier(Type.INTOBJ, new ConstInteger(4)),
	    Definition.forModifier(Type.STROBJ, new StringExpression(" ")) };

	public static Object execute(Object obj, int count, String indent) {
		StringBuilder buf = new StringBuilder(indent.length() * count);
		for (int i = 0; i < count; i++) {
			buf.append(indent);
		}
		indent = buf.toString();
		return indent + obj.toString().replace("\n", "\n" + indent);
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}