package com.roubsite.smarty4j.statement.modifier;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Modifier;

/**
 * This is used to set a default value for a variable. If the variable is unset or an empty string,
 * the given default value is printed instead. Default takes the one argument.
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
 * <td align="center"><span class="emphasis"><em>empty</em></span></td>
 * <td>This is the default value to output if the variable is empty.</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "Dealers Will Hear Car Talk at Noon.");<br>
 * context.set("email", "");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle|default:'no title'}<br>
 * {$myTitle|default:'no title'}<br>
 * {$email|default:'No email address available'}<br>
 * <br>
 * <b>Output:</b><br>
 * Dealers Will Hear Car Talk at Noon.<br>
 * no title<br>
 * No email address available<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $default extends Modifier {

	private static final Definition[] definitions = { Definition.forModifier(Type.OBJECT,
	    new StringExpression("")) };

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		Label def = new Label();
		Label end = new Label();
		mv.visitInsn(DUP);
		mv.visitJumpInsn(IFNULL, def);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I");
		mv.visitJumpInsn(IFEQ, def);
		mv.visitJumpInsn(GOTO, end);
		mv.visitLabel(def);
		mv.visitInsn(POP);
		PARAMETERS[0].parseObject(mv, local, vm);
		mv.visitLabel(end);
	}
}