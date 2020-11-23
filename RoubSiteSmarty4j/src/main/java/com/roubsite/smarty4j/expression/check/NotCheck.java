package com.roubsite.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;

/**
 * 非操作布尔表达式节点, 将JVM语句栈内的布尔表达式逻辑值设置成相反的值
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class NotCheck extends CheckExpression {

	/** 需要非操作的表达式 */
	private Expression exp;

	/**
	 * 创建非操作布尔表达式节点
	 * 
	 * @param exp
	 *          需要非操作的表达式
	 */
	public NotCheck(Expression exp) {
		this.exp = exp;
	}

	@Override
	public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue,
	    Label lblFalse) {
		exp.parseCheck(mv, local, vm, lblFalse, lblTrue);
		mv.visitLdcInsn(true);
		mv.visitInsn(SWAP);
		mv.visitInsn(ISUB);
	}
}