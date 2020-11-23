package com.roubsite.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;

/**
 * 或操作布尔表达式节点, 检测两个表达式的或操作结果
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class OrCheck extends CheckExpression {

	private Expression exp1;
	private Expression exp2;
	
	/**
	 * 创建或操作布尔表达式节点
	 * 
	 * @param exp1
	 *          表达式1
	 * @param exp2
	 *          表达式2
	 */
	public OrCheck(Expression exp1, Expression exp2) {
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue, Label lblFalse) {
		Label isTrue = lblTrue != null ? lblTrue : new Label();

		// exp1为假的时候不进行短路处理
		exp1.parseCheck(mv, local, vm, isTrue, null);
		mv.visitJumpInsn(IFNE, isTrue);

		// exp1已经为假, 因此exp2允许全部的短路处理
		exp2.parseCheck(mv, local, vm, isTrue, lblFalse);
		mv.visitJumpInsn(IFNE, isTrue);

		Label end = new Label();
		mv.visitLdcInsn(false);
		mv.visitJumpInsn(GOTO, end);

		if (lblTrue == null) {
			mv.visitLabel(isTrue);
			mv.visitLdcInsn(true);
		}

		mv.visitLabel(end);
	}
}