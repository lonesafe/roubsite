package com.roubsite.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.TemplateWriter;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.VariableExpression;
import com.roubsite.smarty4j.statement.Function;
import com.roubsite.smarty4j.util.SimpleStack;

/**
 * 用于输出指定的字节数组。
 * 如果要使用它，在merge时必须使用OutputStream作为输出，而不是Writer。
 * 
 * <b>Syntax:</b>
 * 
 * <pre>
 * {bytes [Object(byte[])]}
 * </pre>
 * 
 * <b>Example:</b>
 * 
 * <pre>
 * {bytes $array}
 * </pre>
 * 
 * @see com.roubsite.smarty4j.Template#merge(com.roubsite.smarty4j.Context,
 *      java.io.OutputStream)
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $bytes extends Function {

	/** 需要输出的表达式 */
	private Expression exp;

	@Override
	public void syntax(Analyzer analyzer, SimpleStack tokens) throws ParseException {
		Object value = tokens.get(1);
		if ((tokens.size() != 2) || !(value instanceof VariableExpression)) {
			throw new ParseException(String.format(MessageFormat.NOT_CORRECT, "参数格式"));
		}
		exp = (Expression) value;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {

		/**
		 * out.write((byte[]) exp);
		 */
		mv.visitVarInsn(ALOAD, WRITER);
		mv.visitLdcInsn(null);
		exp.parse(mv, local, vm);
		mv.visitTypeInsn(CHECKCAST, "[B");
		mv.visitMethodInsn(INVOKEVIRTUAL, TemplateWriter.NAME, "write", "(Ljava/lang/String;[B)V");
	}
}