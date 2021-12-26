package com.roubsite.context;

import java.util.HashMap;
import java.util.Map;

import com.roubsite.database.RSConnection;
import com.roubsite.holder.ActionClassBean;
import com.roubsite.utils.ClassBean;
import com.roubsite.web.wrapper.RoubSiteRequestWrapper;
import com.roubsite.web.wrapper.RoubSiteResponseWrapper;

public class RSFilterContext {
	private ClassBean classBean;
	private ActionClassBean actionClassBean;
	private RoubSiteRequestWrapper request;
	private RoubSiteResponseWrapper response;
	private Map<String, RSConnection> dbConnList = new HashMap<String, RSConnection>();;
	private boolean isTrans = false;

	public ActionClassBean getActionClassBean() {
		return actionClassBean;
	}

	public void setActionClassBean(ActionClassBean actionClassBean) {
		this.actionClassBean = actionClassBean;
	}

	public ClassBean getClassBean() {
		return classBean;
	}

	public void setClassBean(ClassBean classBean) {
		this.classBean = classBean;
	}

	public RoubSiteRequestWrapper getRequest() {
		return request;
	}

	public void setRequest(RoubSiteRequestWrapper request) {
		this.request = request;
	}

	public RoubSiteResponseWrapper getResponse() {
		return response;
	}

	public void setResponse(RoubSiteResponseWrapper response) {
		this.response = response;
	}

	public Map<String, RSConnection> getDbConnList() {
		return dbConnList;
	}

	public void addDbConnList(String name, RSConnection connection) {
		this.dbConnList.put(name, connection);
	}

	public boolean isTrans() {
		return isTrans;
	}

	public void setTrans(boolean isTrans) {
		this.isTrans = isTrans;
	}

}
