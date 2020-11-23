package com.roubsite.smarty4j.expression.number;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;

/**
 * 整数表达式转换节点, 将表达式转换成整数表达式
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class IntegerAdapter extends IntegerExpression {

	/** 需要转换的表达式 */
	private Expression exp;

	/**
	 * 建立整数表达式转换节点
	 * 
	 * @param exp
	 *          需要转换的表达式
	 */
	public IntegerAdapter(Expression exp) {
		this.exp = exp;
	}

	@Override
	public void parseInteger(MethodVisitorProxy mv, int local, VariableManager vm) {
		exp.parseInteger(mv, local, vm);
	}
}
