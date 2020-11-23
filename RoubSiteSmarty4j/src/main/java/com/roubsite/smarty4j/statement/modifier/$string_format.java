package com.roubsite.smarty4j.statement.modifier;

import static org.objectweb.asm.Opcodes.*;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Modifier;

/**
 * This is a way to format strings, such as decimal numbers and such. Use the syntax for
 * java.lang.String.format() for the formatting.
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
 * <td>This is what format to use.</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("number", 23.5787446);<br>
 * <br>
 * <b>Template:</b><br>
 * {$number}<br>
 * {$number|string_format:"%.2f"}<br>
 * <br>
 * <b>Output:</b><br>
 * 23.5787446<br>
 * 23.58<br>
 * </code>
 *
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class $string_format extends Modifier {

	private static final Definition[] definitions = { Definition.forModifier(Type.STRING) };

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitVarInsn(ASTORE, local);
		PARAMETERS[0].parseString(mv, local, vm);
		mv.visitLdcInsn(1);
		mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
		mv.visitInsn(DUP);
		mv.visitLdcInsn(0);
		mv.visitVarInsn(ALOAD, local);
		mv.visitInsn(AASTORE);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "format",
		    "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;");
	}
}
