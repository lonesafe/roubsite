package com.roubsite.web.filter.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.roubsite.holder.ActionClassBean;

public interface RSSecurityInterface {
	// 检查是否可以访问当前资源
	public boolean isPermitted(ServletRequest request, ServletResponse response, ActionClassBean acb) throws Exception;
}
