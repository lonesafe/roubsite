package com.roubsite.smarty4j.expression;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

/**
 * 字符串转换节点, 将表达式转换成字符表达式
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class StringAdapter extends ObjectExpression {

	/** 需要转换的表达式 */
	private Expression exp;

	/**
	 * 建立字符串转换节点
	 * 
	 * @param exp
	 *          字符串常量
	 */
	public StringAdapter(Expression exp) {
		this.exp = exp;
	}

	@Override
	public void parseSelf(MethodVisitorProxy mv, int local, VariableManager vm) {
		exp.parseString(mv, local, vm);
	}

	@Override
	public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue,
	    Label lblFalse) {
		exp.parseCheck(mv, local, vm, lblTrue, lblFalse);
	}
}
