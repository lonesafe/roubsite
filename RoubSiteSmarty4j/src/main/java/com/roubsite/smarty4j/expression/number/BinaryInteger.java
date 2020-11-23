package com.roubsite.smarty4j.expression.number;

import static org.objectweb.asm.Opcodes.*;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;

/**
 * 二元整数操作表达式, 向JVM语句栈内放入一个整数值表示两个对象的操作结果
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class BinaryInteger extends IntegerExpression {

	private int opcode;
	private Expression exp1;
	private Expression exp2;

	/**
	 * 建立二元整数操作表达式节点
	 * 
	 * @param exp1
	 *            表达式1
	 * @param exp2
	 *            表达式2
	 */
	public BinaryInteger(int opcode, Expression exp1, Expression exp2) {
		this.opcode = opcode;
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	public void parseInteger(MethodVisitorProxy mv, int local, VariableManager vm) {
		if (opcode == ISUB && exp1 instanceof ConstInteger && ((ConstInteger) exp1).getValue() == 0) {
			exp2.parseInteger(mv, local, vm);
			mv.visitInsn(INEG);
		} else {
			exp1.parseInteger(mv, local, vm);
			exp2.parseInteger(mv, local, vm);
			mv.visitInsn(opcode);
		}
	}
}