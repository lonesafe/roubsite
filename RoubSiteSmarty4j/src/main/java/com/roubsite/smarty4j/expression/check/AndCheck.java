package com.roubsite.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;

/**
 * 与操作布尔表达式节点, 检测两个表达式的与操作结果
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class AndCheck extends CheckExpression {

	private Expression exp1;
	private Expression exp2;

	/**
	 * 创建与操作布尔表达式节点
	 * 
	 * @param exp1
	 *          表达式1
	 * @param exp2
	 *          表达式2
	 */
	public AndCheck(Expression exp1, Expression exp2) {
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue,
	    Label lblFalse) {
		Label isFalse = lblFalse != null ? lblFalse : new Label();

		// exp1为真的时候不进行短路处理
		exp1.parseCheck(mv, local, vm, null, isFalse);
		mv.visitJumpInsn(IFEQ, isFalse);

		// exp1已经为真, 因此exp2允许全部的短路处理
		exp2.parseCheck(mv, local, vm, lblTrue, isFalse);
		mv.visitJumpInsn(IFEQ, isFalse);

		Label end = new Label();
		mv.visitLdcInsn(true);
		mv.visitJumpInsn(GOTO, end);

		if (lblFalse == null) {
			mv.visitLabel(isFalse);
			mv.visitLdcInsn(false);
		}

		mv.visitLabel(end);
	}
}