package com.roubsite.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP_X1;
import static org.objectweb.asm.Opcodes.DUP_X2;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;

import java.util.Map;
import java.util.Map.Entry;

import com.roubsite.smarty4j.Context;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.SafeContext;
import com.roubsite.smarty4j.Template;
import com.roubsite.smarty4j.TemplateWriter;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.MapExpression;
import com.roubsite.smarty4j.expression.NullExpression;
import com.roubsite.smarty4j.expression.VoidExpression;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.LineFunction;

/**
 * The tag is used to call a template function defined by the {function} tag just like a plugin
 * function.
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
 * <td>The name of the template function</td>
 * </tr>
 * <tr>
 * <td align="center">assign</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>The name of the variable that the output of called template function will be assigned to</td>
 * </tr>
 * <tr>
 * <td align="center">return</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>需要返回的变量名，使用,号分隔</td>
 * </tr>
 * <tr>
 * <td align="center">[var ...]</td>
 * <td align="center">[var type]</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>variable to pass local to template function</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $call extends LineFunction {

	private static final Definition[] definitions = {
	    Definition.forFunction("name", Type.STRING),
	    Definition.forFunction("assign", Type.STRING, NullExpression.VALUE),
	    Definition.forFunction("return", Type.STRING, NullExpression.VALUE, ""),
	    Definition.forFunction("", Type.MAP, NullExpression.VALUE) };

	public static Object execute(SafeContext ctx, TemplateWriter writer, String name, String assign,
	    Map<String, Object> data) throws Exception {
		if (assign != null) {
			writer = TemplateWriter.getTemporaryWriter();
		}

		Context cc = new Context((Context) ctx);
		cc.putAll(data);
		ctx.getTemplate().getFunction(name).merge(cc, writer);

		if (assign != null) {
			return writer.toString();
		}
		return null;
	}

	@Override
	public void createParameters(Definition[] parameters, Map<String, Expression> fields)
	    throws ParseException {
		super.createParameters(parameters, fields);
		fields.remove("name");
		fields.remove("assign");
		fields.remove("return");
		PARAMETERS[3] = new MapExpression(fields);
		if (PARAMETERS[2] == NullExpression.VALUE) {
			PARAMETERS[2] = VoidExpression.VALUE;
		}
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		if (PARAMETERS[2] == VoidExpression.VALUE) {
			// 不需要回写走传统的调用方式，减小编译的结果文件体积
			super.parse(mv, local, vm);
		} else {
			mv.visitVarInsn(ALOAD, TEMPLATE);
			PARAMETERS[0].parseString(mv, local, vm);
			mv.visitMethodInsn(INVOKEVIRTUAL, Template.NAME, "getFunction",
			    "(Ljava/lang/String;)L" + Template.NAME + ";");

			mv.visitTypeInsn(NEW, Context.NAME);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, CONTEXT);
			mv.visitMethodInsn(INVOKESPECIAL, Context.NAME, "<init>", "(L" + Context.NAME + ";)V");
			mv.visitInsn(DUP_X1);

			for (Entry<String, Expression> entry : ((MapExpression) PARAMETERS[3]).getValue().entrySet()) {
				mv.visitInsn(DUP);
				mv.visitLdcInsn(entry.getKey());
				entry.getValue().parseObject(mv, local, vm);
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
				    "(Ljava/lang/String;Ljava/lang/Object;)V");
			}

			if (PARAMETERS[1] != NullExpression.VALUE) {
				mv.visitMethodInsn(INVOKESTATIC, TemplateWriter.NAME, "getTemporaryWriter", "()L"
				    + TemplateWriter.NAME + ";");
				mv.visitInsn(DUP_X2);
			} else {
				mv.visitVarInsn(ALOAD, WRITER);
			}

			mv.visitMethodInsn(INVOKEVIRTUAL, Template.NAME, "merge", "(L" + Context.NAME + ";L"
			    + TemplateWriter.NAME + ";)V");

			if (PARAMETERS[1] != NullExpression.VALUE) {
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "toString", "()Ljava/lang/String;");
				writeVariable(mv, local, vm, PARAMETERS[1].toString(), null);
			}

			for (String name : PARAMETERS[2].toString().trim().split("\\s*,\\s*")) {
				if (name.length() > 0) {
					mv.visitInsn(DUP);
					mv.visitLdcInsn(name);
					mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get",
					    "(Ljava/lang/String;)Ljava/lang/Object;");
					writeVariable(mv, local, vm, name, null);
				}
			}
			mv.visitInsn(POP);
		}
	}
}