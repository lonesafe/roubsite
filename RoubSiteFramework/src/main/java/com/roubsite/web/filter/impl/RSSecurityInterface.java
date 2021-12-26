package com.roubsite.web.filter.impl;

public interface RSSecurityInterface {
	//检查是否可以访问当前资源
	public boolean isPermitted();
}
