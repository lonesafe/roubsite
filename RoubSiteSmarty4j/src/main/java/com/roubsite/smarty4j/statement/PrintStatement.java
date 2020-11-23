package com.roubsite.smarty4j.statement;

import static org.objectweb.asm.Opcodes.*;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;

/**
 * 表达式输出语句。
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class PrintStatement extends Node {

	/** 需要输出的对象表达式 */
	private Expression expression;

	/**
	 * 建立变量输出语句。
	 * 
	 * @param expression
	 *          待输出的表达式
	 */
	public PrintStatement(Expression expression) {
		this.expression = expression;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitVarInsn(ALOAD, WRITER);
		expression.parseString(mv, local, vm);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "write", "(Ljava/lang/String;)V");
	}
}