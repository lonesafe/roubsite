package com.roubsite.smarty4j.expression.number;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

/**
 * 浮点数常数表达式节点, 向JVM语句栈内放入一个浮点数常量值
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class ConstDouble extends DoubleExpression {

	public static final ConstDouble ZERO = new ConstDouble(0);  
	
	/** 常量值 */
	private double value = 0;

	/**
	 * 创建浮点数常数表达式节点
	 * 
	 * @param value
	 *          常量值
	 */
	public ConstDouble(double value) {
		this.value = value;
	}

	/**
	 * 设置成相反数
	 */
	public void inverse() {
		value = -value;
	}

	@Override
	public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue, Label lblFalse) {
		if (value == 0) {
			if (lblFalse == null) {
				mv.visitLdcInsn(false);
			} else {
				mv.visitJumpInsn(GOTO, lblFalse);
			}
		} else {
			if (lblTrue == null) {
				mv.visitLdcInsn(true);
			} else {
				mv.visitJumpInsn(GOTO, lblTrue);
			}
		}
	}

  @Override
	public void parseInteger(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitLdcInsn((int) value);
	}

  @Override
	public void parseDouble(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitLdcInsn(value);
	}

	@Override
	public void parseString(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitLdcInsn(Double.toString(value));
	}
}