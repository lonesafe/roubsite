package com.roubsite.holder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.roubsite.web.error.RSErrorPage;

public class ActionClassBean {
	private final static Logger logger = LoggerFactory.getLogger(ActionClassBean.class);
	private Class<?> clazz;
	private Object actionObject;
	private Method method;
	private HttpServletResponse resp;
	private HttpServletRequest req;

	public ActionClassBean(String classPath, String methodName) {
		try {
			setClazz(Class.forName(classPath));
			setMethod(getClazz().getDeclaredMethod(methodName, new Class[] {}));
		} catch (ClassNotFoundException e) {
			logger.error("没有找到对应的action类:" + classPath, e);
			new RSErrorPage(resp, req, 500, null, "系统错误，请稍后再试！").die(e);
		} catch (NoSuchMethodException e) {
			logger.error("在action类中没有找到方法:" + methodName, e);
			new RSErrorPage(resp, req, 500, null, "在action类中没有找到方法：" + methodName).die(e);
		} catch (SecurityException e) {
			logger.error("无法访问该方法:" + methodName, e);
			new RSErrorPage(resp, req, 500, null, "无法访问该方法:" + methodName).die(e);
		}

	}

	public boolean newAction() {
		try {
			setActionObject(getClazz().getDeclaredConstructor().newInstance());
			return true;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			logger.error("创建action失败", e);
			new RSErrorPage(resp, req, 500, null, "创建action失败").die(e);
		}
		return false;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	private void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Object getActionObject() {
		return actionObject;
	}

	private void setActionObject(Object actionObject) {
		this.actionObject = actionObject;
	}

	public Method getMethod() {
		return method;
	}

	private void setMethod(Method method) {
		this.method = method;
	}

}
