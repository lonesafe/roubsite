package com.roubsite.smarty4j.statement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记节点合法的父节点类型，通过在class前声明@ParentType(name="...")
 * 的方式定义。
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParentType {

	/**
	 * 父节点的名称。
	 * 
	 * @return 父节点的名称
	 */
	String name();
}
