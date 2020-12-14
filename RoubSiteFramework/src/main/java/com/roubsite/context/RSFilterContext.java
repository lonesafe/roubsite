package com.roubsite.context;

import com.roubsite.holder.ActionClassBean;
import com.roubsite.utils.ClassBean;

public class RSFilterContext {
	private ClassBean classBean;
	private ActionClassBean actionClassBean;

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
}
