package com.roubsite.holder;

import com.roubsite.context.RSFilterContext;

public class RSFilterContextHolder {
	private final static ThreadLocal<RSFilterContext> contexts = new ThreadLocal<RSFilterContext>();

	private RSFilterContextHolder() {
	}

	public static RSFilterContext initRequestContext() {
		RSFilterContext rc = new RSFilterContext();
		contexts.set(rc);
		return rc;
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
