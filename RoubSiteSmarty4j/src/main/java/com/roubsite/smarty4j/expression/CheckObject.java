package com.roubsite.smarty4j.expression;

/**
 * 针对对象进行逻辑AND,OR运算, 实现短路输出
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public abstract class CheckObject extends ObjectExpression {

	/** 表达式1 */
	Expression exp1;

	/** 表达式2 */
	Expression exp2;

	/**
	 * 创建二元布尔表达式节点
	 * 
	 * @param exp1
	 *          表达式1
	 * @param exp2
	 *          表达式2
	 */
	public CheckObject(Expression exp1, Expression exp2) {
		this.exp1 = exp1;
		this.exp2 = exp2;
	}
}
