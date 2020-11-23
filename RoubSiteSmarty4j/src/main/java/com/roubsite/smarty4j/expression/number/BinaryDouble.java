package com.roubsite.smarty4j.expression.number;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;

/**
 * 二元浮点数操作表达式, 向JVM语句栈内放入一个浮点数值表示两个对象的操作结果
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class BinaryDouble extends DoubleExpression {

	private int opcode;
	private Expression exp1;
	private Expression exp2;

	public BinaryDouble(int opcode, Expression exp1, Expression exp2) {
		this.opcode = opcode;
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	public void parseDouble(MethodVisitorProxy mv, int local, VariableManager vm) {
		exp1.parseDouble(mv, local, vm);
		exp2.parseDouble(mv, local, vm);
		mv.visitInsn(opcode);
	}
}