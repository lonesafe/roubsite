package com.roubsite.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

/**
 * 与操作对象表达式节点, 如果某一个对象表达式为<tt>false</tt>, 返回这个对象,
 * 否则返回最后一个对象
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class AndObject extends CheckObject {

	/**
	 * 创建与操作布尔表达式节点
	 * 
	 * @param exp1
	 *          表达式1
	 * @param exp2
	 *          表达式2
	 */
	public AndObject(Expression exp1, Expression exp2) {
		super(exp1, exp2);
	}

	@Override
	public void parseSelf(MethodVisitorProxy mv, int local, VariableManager vm) {
		Label end = new Label();

		exp1.parseObject(mv, local, vm);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESTATIC, NAME, "o2b", "(Ljava/lang/Object;)Z");
		mv.visitJumpInsn(IFEQ, end);

		mv.visitInsn(POP);
		exp2.parseObject(mv, local, vm);

		mv.visitLabel(end);
	}

	@Override
	public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue, Label lblFalse) {
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