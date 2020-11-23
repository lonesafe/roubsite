package com.roubsite.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Rick Jone 王振骁
 */

@Retention(RetentionPolicy.RUNTIME)
@Documented // 说明该注解将被包含在javadoc中
@Target(ElementType.METHOD) // 定义注解的作用目标**作用范围字段、枚举的常量/方法
public @interface RSSecurity {
	/**
	 * 权限id数组
	 * 
	 * @return
	 */
	String[] role() default {};

	/**
	 * 未登录返回类型
	 * 
	 * @return
	 */
	RSSecRetType retType() default RSSecRetType.url;

	String redirectUrl() default "";
	
}
