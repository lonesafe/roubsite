package com.roubsite.web.filter;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.roubsite.holder.ActionClassBean;
import com.roubsite.utils.ClassBean;
import com.roubsite.web.error.RSErrorPage;
import com.roubsite.web.wrapper.RoubSiteRequestWrapper;
import com.roubsite.web.wrapper.RoubSiteResponseWrapper;

/**
 * action注入类
 * 
 * @author lones 王振骁
 *
 */
public class RSInvokeAction {
	private Logger logger = LoggerFactory.getLogger(RSInvokeAction.class);
	private ClassBean cb;
	private ActionClassBean acb;
	private RoubSiteRequestWrapper req;
	private RoubSiteResponseWrapper resp;

	public RSInvokeAction(ClassBean cb, ActionClassBean acb, RoubSiteRequestWrapper request,
			RoubSiteResponseWrapper response) {
		this.cb = cb;
		this.acb = acb;
		this.resp = response;
		this.req = request;
	}

	public boolean invokeActon() throws IOException, ServletException {
		Class<?> clazz = acb.getClazz();
		Object action = acb.getActionObject();
		Method method = acb.getMethod();
		// 默认初始化
		try {
			if (initAction(clazz, cb, this.req, this.resp, action)) {
				// 执行action方法
				try {
					method.invoke(action, new Object[] {});
					return true;
				} catch (Exception e) {
					logger.error("action执行方法失败：", e);
					new RSErrorPage(this.resp, this.req, 500, null, "系统错误，请稍后再试！").die(e);
				}
			}
		} catch (Exception e2) {
			logger.error("初始化action出错", e2);
			new RSErrorPage(this.resp, this.req, 500, null, "系统错误，请稍后再试！").die(e2);
			return false;
		}
		return false;
	}

	// 初始化action
	private boolean initAction(Class<?> clazz, ClassBean cb, RoubSiteRequestWrapper req, RoubSiteResponseWrapper resp,
			Object action) throws Exception {
		Method init = clazz.getMethod("init",
				new Class[] { RoubSiteRequestWrapper.class, RoubSiteResponseWrapper.class });
		init.setAccessible(true);
		init.invoke(action, new Object[] { req, resp });

		// 自定义初始化
		Method __init__ = clazz.getMethod("__init__",
				new Class[] { HttpServletRequest.class, HttpServletResponse.class, ClassBean.class });
		__init__.setAccessible(true);
		return (boolean) __init__.invoke(action, new Object[] { req, resp, cb });
	}
}
