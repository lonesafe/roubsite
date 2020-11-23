package com.roubsite.smarty4j.expression.number;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

/**
 * 浮点数表达式节点, 向JVM语句栈内放入一个浮点数值
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public abstract class DoubleExpression extends NumberExpression {

	/** ASM名称 */
	public static final String NAME = DoubleExpression.class.getName().replace('.', '/');

	@Override
	public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue, Label lblFalse) {
		parseDouble(mv, local, vm);
		mv.visitInsn(DCONST_0);
		mv.visitInsn(DCMPL);
		mv.visitSCJumpInsn(IFNE, lblTrue, lblFalse);
	}

	@Override
	public void parseInteger(MethodVisitorProxy mv, int local, VariableManager vm) {
		parseDouble(mv, local, vm);
		mv.visitInsn(D2I);
	}

	@Override
	public void parseString(MethodVisitorProxy mv, int local, VariableManager vm) {
		parseDouble(mv, local, vm);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "toString", "(D)Ljava/lang/String;");
	}

	@Override
	public void parseObject(MethodVisitorProxy mv, int local, VariableManager vm) {
		parseDouble(mv, local, vm);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		parseDouble(mv, local, vm);
	}
}
