package com.roubsite.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

/**
 * 字符串表达式节点, 向JVM语句栈内放入字符串
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class StringExpression extends ObjectExpression {

	public static final StringExpression EMPTY = new StringExpression("");
	
	/** 字符串常量 */
	private String value;

	/**
	 * 创建字符串表达式节点
	 * 
	 * @param value
	 *          字符串常量
	 */
	public StringExpression(String value) {
		this.value = value;
	}

	void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public void parseSelf(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitLdcInsn(value);
	}

	@Override
	public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue, Label lblFalse) {
		if (value.length() == 0) {
			// 字符串没有内容相当于false
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
		if (extendeds != null) {
			super.parseInteger(mv, local, vm);
		} else {
			mv.visitLdcInsn(Integer.parseInt(value));
		}
	}

	@Override
	public void parseDouble(MethodVisitorProxy mv, int local, VariableManager vm) {
		if (extendeds != null) {
			super.parseDouble(mv, local, vm);
		} else {
			mv.visitLdcInsn(Double.parseDouble(value));
		}
	}

	@Override
	public void parseString(MethodVisitorProxy mv, int local, VariableManager vm) {
		if (extendeds != null) {
			super.parseString(mv, local, vm);
		} else {
			mv.visitLdcInsn(value);
		}
	}

	@Override
	public void parseObject(MethodVisitorProxy mv, int local, VariableManager vm) {
		if (extendeds != null) {
			super.parseObject(mv, local, vm);
		} else {
			mv.visitLdcInsn(value);
		}
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		parseString(mv, local, vm);
	}

	@Override
	public String toString() {
		return value;
	}
}