package com.roubsite.holder;

import com.roubsite.context.RSFilterContext;
import com.roubsite.utils.ClassBean;

public class RSFilterContextHolder {
	private final static ThreadLocal<RSFilterContext> contexts = new ThreadLocal<RSFilterContext>();

	private RSFilterContextHolder() {
	}

	public static void setLocalRequestContext(ActionClassBean actionClassBean, ClassBean classBean) {
		RSFilterContext rc = new RSFilterContext();
		rc.setActionClassBean(actionClassBean);
		rc.setClassBean(classBean);
		contexts.set(rc);
	}

	/**
	 * 获取当前请求的上下文
	 * 
	 * @return
	 */
	public static RSFilterContext getRSFilterContext() {
		return contexts.get();
	}

	/**
	 * 清除当前线程对请求上下文对象的引用（即让GC回收当前请求上下文对象）
	 */
	public static void destoryRSFilterContext() {
		contexts.remove();
	}
}
