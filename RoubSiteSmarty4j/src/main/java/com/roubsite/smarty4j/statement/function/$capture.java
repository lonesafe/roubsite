package com.roubsite.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import com.roubsite.smarty4j.Context;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.TemplateWriter;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.NullExpression;
import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.statement.Block;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;

/**
 * This is used to collect the output of the template between the tags into a variable instead of
 * displaying it. Any content between {capture name='foo'} and {/capture} is collected into the
 * variable specified in the name attribute.<br>
 * The captured content can be used in the template from the variable $smarty.capture.foo where
 * “foo” is the value passed in the name attribute. If you do not supply the name attribute, then
 * “default” will be used as the name ie $smarty.capture.default.<br>
 * 
 * <table border="1">
 * <colgroup> <col align="center" class="param"> <col align="center" class="type"> <col
 * align="center" class="required"> <col align="center" class="default"> <col class="desc">
 * </colgroup> <thead>
 * <tr>
 * <th align="center">Attribute Name</th>
 * <th align="center">Type</th>
 * <th align="center">Required</th>
 * <th align="center">Default</th>
 * <th>Description</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td align="center">name</td>
 * <td align="center">string</td>
 * <td align="center">Yes</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>The name of the captured block</td>
 * </tr>
 * <tr>
 * <td align="center">assign</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>The variable name where to assign the captured output to</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $capture extends Block {

	private static final Definition[] definitions = {
	    Definition.forFunction("name", Type.STRING, new StringExpression("default")),
	    Definition.forFunction("assign", Type.STRING, NullExpression.VALUE) };

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {

		/**
		 * <code>
		 * if (assign != null) {
		 *   #set(assign, childWriter.toString());
		 * } else {
		 *   ctx.getCaptures().put(name, childWriter);
		 * }
		 * </code>
		 */
		mv.visitVarInsn(ALOAD, WRITER);
		mv.visitMethodInsn(INVOKESTATIC, TemplateWriter.NAME, "getTemporaryWriter", "()L"
		    + TemplateWriter.NAME + ";");
		mv.visitVarInsn(ASTORE, WRITER);

		mv.visitVarInsn(ALOAD, CONTEXT);
		mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "getCapture", "()Ljava/util/Map;");
		PARAMETERS[0].parse(mv, local, vm);
		super.parse(mv, local, vm);
		mv.visitVarInsn(ALOAD, WRITER);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "toString", "()Ljava/lang/String;");

		if (PARAMETERS[1] != NullExpression.VALUE) {
			mv.visitInsn(DUP_X2);
		}
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put",
		    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
		mv.visitInsn(POP);
		if (PARAMETERS[1] != NullExpression.VALUE) {
			writeVariable(mv, local, vm, PARAMETERS[1].toString(), null);
		}

		mv.visitVarInsn(ASTORE, WRITER);
	}
}