package com.roubsite.smarty4j.statement.modifier;

import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Modifier;

/**
 * A simple search and replace on a variable.
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
 * <td align="center">Yes</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>This is the string of text to be replaced.</td>
 * </tr>
 * <tr>
 * <td align="center">2</td>
 * <td align="center">string</td>
 * <td align="center">Yes</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>This is the string of text to replace with.</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "Child's Stool Great for Use in Garden.");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle}<br>
 * {$articleTitle|replace:'Garden':'Vineyard'}<br>
 * {$articleTitle|replace:' ':'   '}<br>
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
public class $replace extends Modifier {

	private static final Definition[] definitions = {
	    Definition.forModifier(Type.STROBJ),
	    Definition.forModifier(Type.STROBJ) };

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
		PARAMETERS[0].parseString(mv, local, vm);
		PARAMETERS[1].parseString(mv, local, vm);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "replace",
		    "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;");
	}
}