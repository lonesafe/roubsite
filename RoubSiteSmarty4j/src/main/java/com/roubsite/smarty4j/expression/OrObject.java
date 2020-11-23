package com.roubsite.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

/**
 * 或操作对象表达式节点, 如果某一个对象表达式为<tt>true</tt>, 返回这个对象,
 * 否则返回最后一个对象
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class OrObject extends CheckObject {

	/**
	 * 创建或操作布尔表达式节点
	 * 
	 * @param exp1
	 *          表达式1
	 * @param exp2
	 *          表达式2
	 */
	public OrObject(Expression exp1, Expression exp2) {
		super(exp1, exp2);
	}

	@Override
	public void parseSelf(MethodVisitorProxy mv, int local, VariableManager vm) {
		Label end = new Label();

		exp1.parseObject(mv, local, vm);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESTATIC, NAME, "o2b", "(Ljava/lang/Object;)Z");
		mv.visitJumpInsn(IFNE, end);

		mv.visitInsn(POP);
		exp2.parseObject(mv, local, vm);

		mv.visitLabel(end);
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