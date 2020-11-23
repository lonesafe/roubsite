package com.roubsite.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.Context;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.MapExtended;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.LineFunction;

/**
 * This is used for creating or appending template variable arrays during the
 * execution of a template.
 * 
 * <table border="1">
 * <colgroup> <col align="center" class="param">
 * <col align="center" class="type"> <col align="center" class="required">
 * <col align="center" class="default"> <col class="desc"> </colgroup> <thead>
 * <tr>
 * <th align="center">Attribute Name</th>
 * <th align="center">Type</th>
 * <th align="center">Required</th>
 * <th align="center">Default</th>
 * <th>Description</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td align="center">var</td>
 * <td align="center">string</td>
 * <td align="center">Yes</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>The name of the variable being assigned</td>
 * </tr>
 * <tr>
 * <td align="center">value</td>
 * <td align="center">string</td>
 * <td align="center">Yes</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>The value being assigned</td>
 * </tr>
 * <tr>
 * <td align="center">index</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>The index for the new array element. If not specified the value is append
 * to the end of the array.</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $append extends LineFunction {

	private static final Definition[] definitions = { Definition.forFunction("var", Type.STRING),
			Definition.forFunction("value", Type.OBJECT), Definition.forFunction("index", Type.STROBJ) };

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		String varName = PARAMETERS[0].toString();
		int index = vm.getIndex(varName);
		if (!vm.hasCached() || vm.requiredRewrite() || index != VariableManager.NOEXIST) {
			PARAMETERS[1].parseObject(mv, local, vm);

			if (index == VariableManager.NOCACHE || index == VariableManager.NOEXIST) {
				mv.visitVarInsn(ALOAD, CONTEXT);
				mv.visitLdcInsn(varName);
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get", "(Ljava/lang/String;)Ljava/lang/Object;");
			} else {
				mv.visitVarInsn(ALOAD, vm.getIndex(varName));
			}
			Label start = new Label();
			mv.visitInsn(DUP);
			mv.visitJumpInsn(IFNONNULL, start);
			mv.visitInsn(POP);
			mv.visitTypeInsn(NEW, "java/util/HashMap");
			mv.visitInsn(DUP);
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
			this.writeVariable(mv, local, vm, varName, null);
			mv.visitLabel(start);

			PARAMETERS[2].parseString(mv, local, vm);
			mv.visitMethodInsn(INVOKESTATIC, MapExtended.NAME, "setValue",
					"(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;)V");
		}
	}
}