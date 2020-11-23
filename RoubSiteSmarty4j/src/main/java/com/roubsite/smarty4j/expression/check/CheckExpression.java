package com.roubsite.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;

/**
 * 布尔表达式节点, 向JVM语句栈内放入一个整数值, 1表示<tt>true</tt>, 0表示 <tt>false</tt>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public abstract class CheckExpression extends Expression {

	@Override
	public void parseInteger(MethodVisitorProxy mv, int local, VariableManager vm) {
		parseCheck(mv, local, vm, null, null);
	}

	@Override
	public void parseDouble(MethodVisitorProxy mv, int local, VariableManager vm) {
		parseCheck(mv, local, vm, null, null);
		mv.visitInsn(I2D);
	}

	@Override
	public void parseString(MethodVisitorProxy mv, int local, VariableManager vm) {
		parseCheck(mv, local, vm, null, null);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "toString", "(Z)Ljava/lang/String;");
	}

	@Override
	public void parseObject(MethodVisitorProxy mv, int local, VariableManager vm) {
		parseCheck(mv, local, vm, null, null);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		parseCheck(mv, local, vm, null, null);
	}
}
