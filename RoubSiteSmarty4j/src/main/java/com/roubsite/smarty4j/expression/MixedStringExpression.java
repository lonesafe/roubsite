package com.roubsite.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.List;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

/**
 * 混合字符串表达式节点, 向JVM语句栈内放入字符串
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class MixedStringExpression extends ObjectExpression {

	/** 所有需要混合的表达式列表 */
	private List<Expression> expressions = new ArrayList<Expression>();

	/**
	 * 向混合字符串表达式中增加一个新的字符串
	 * 
	 * @param expression
	 *          需要一起显示的表达式
	 */
	public void add(String text) {
		expressions.add(new StringExpression(text));
	}

	/**
	 * 向混合字符串表达式中增加一个新的表达式
	 * 
	 * @param expression
	 *          需要一起显示的表达式
	 */
	public void add(Expression expression) {
		expressions.add(expression);
	}

	@Override
	public void parseSelf(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
		mv.visitInsn(DUP);
		mv.visitLdcInsn(64);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(I)V");
		for (Expression exp : expressions) {
			exp.parseString(mv, local, vm);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
			    "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
		}
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
	}
}
