package com.roubsite.holder;

import java.lang.reflect.Method;

public class ActionClassBean {
	private Class<?> clazz;
	private Object actionObject;
	private Method method;

	public ActionClassBean(String classPath, String methodName) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, NoSuchMethodException, SecurityException {
		setClazz(Class.forName(classPath));
		setActionObject(getClazz().newInstance());
		setMethod(getClazz().getDeclaredMethod(methodName, new Class[] {}));
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
