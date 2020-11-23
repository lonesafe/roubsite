package com.roubsite.database.dao;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

import com.roubsite.database.RSConnection;

public class RSDaoFactory implements InvocationHandler {
	static Logger log = Logger.getLogger(RSDaoFactory.class);
	Object obj;
	/**
	 * 获取dao（自定义数据源）
	 * 
	 * @param className      类的全路径
	 * @param dataSourceName 数据源名称
	 * @return Dao接口
	 */
	public Object getDao(Class<?> clazz, String dataSourceName, RSConnection conn) {
		try {
			Constructor<?> cons = clazz.getDeclaredConstructor((Class[]) null);
			obj = cons.newInstance();
			Method init = clazz.getMethod("init", new Class[] { RSConnection.class, String.class });
			init.invoke(obj, new Object[] { conn, dataSourceName });
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this.bind(obj);
	}

	/**
	 * 
	 * @param obj Dao类
	 * @return
	 */
	private Object bind(Object obj) {
		return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		try {
			// 代理执行方法
			result = method.invoke(this.obj, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
