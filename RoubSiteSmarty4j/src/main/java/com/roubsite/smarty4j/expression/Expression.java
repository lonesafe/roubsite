package com.roubsite.smarty4j.expression;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.check.FalseCheck;
import com.roubsite.smarty4j.expression.check.TrueCheck;

/**
 * 表达式节点, 向JVM语句栈内放入一个数据
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public abstract class Expression extends Node {

	public static Expression forString(String s) {
		if (s.equals("true")) {
			return TrueCheck.VALUE;
		} else if (s.equals("false")) {
			return FalseCheck.VALUE;
		} else if (s.equals("null")) {
			return NullExpression.VALUE;
		} else {
			return new StringExpression(s);
		}
	}
	
	/**
	 * 根据当前表达式向JVM语句栈内放入一个布尔值
	 * 
	 * @param mv
	 *          ASM方法访问对象
	 * @param local
	 *          ASM方法内部的语句栈局部变量起始位置
	 * @param vm
	 *          需要缓存的变量名列表, 缓存的实际位置是LOCAL_START + i
	 */
	public abstract void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue,
	    Label lblFalse);

	/**
	 * 根据当前表达式向JVM语句栈内放入一个整数
	 * 
	 * @param mv
	 *          ASM方法访问对象
	 * @param local
	 *          ASM方法内部的语句栈局部变量起始位置
	 * @param vm
	 *          需要缓存的变量名列表, 缓存的实际位置是LOCAL_START + i
	 */
	public abstract void parseInteger(MethodVisitorProxy mv, int local, VariableManager vm);

	/**
	 * 根据当前表达式向JVM语句栈内放入一个浮点数
	 * 
	 * @param mv
	 *          ASM方法访问对象
	 * @param local
	 *          ASM方法内部的语句栈局部变量起始位置
	 * @param vm
	 *          需要缓存的变量名列表, 缓存的实际位置是LOCAL_START + i
	 */
	public abstract void parseDouble(MethodVisitorProxy mv, int local, VariableManager vm);

	/**
	 * 根据当前表达式向JVM语句栈内放入一个字符串
	 * 
	 * @param mv
	 *          ASM方法访问对象
	 * @param local
	 *          ASM方法内部的语句栈局部变量起始位置
	 * @param vm
	 *          需要缓存的变量名列表, 缓存的实际位置是LOCAL_START + i
	 */
	public abstract void parseString(MethodVisitorProxy mv, int local, VariableManager vm);

	/**
	 * 根据当前表达式向JVM语句栈内放入一个对象, 如果是基本数据类型, 将转义成最接近的包装对象
	 * 
	 * @param mv
	 *          ASM方法访问对象
	 * @param local
	 *          ASM方法内部的语句栈局部变量起始位置
	 * @param vm
	 *          需要缓存的变量名列表, 缓存的实际位置是LOCAL_START + i
	 */
	public abstract void parseObject(MethodVisitorProxy mv, int local, VariableManager vm);
}