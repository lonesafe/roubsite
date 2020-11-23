package com.roubsite.security.securityInfo;

import java.util.LinkedList;

/**
 * @author Rick Jone 王振骁
 */
public class RSSecurityBaseUserInfo {
	private String uid;
	private String userName;
	private String userStatus;
	private String[] roleIds;
	private String organId;
	private String organCode;
	private String organName;
	private String organType;
	private String organParentId;
	private LinkedList<String> urls = new LinkedList<>();

	public String getOrganCode() {
		return organCode;
	}

	public String getOrganId() {
		return organId;
	}

	public String getOrganName() {
		return organName;
	}

	public String getOrganType() {
		return organType;
	}

	public String getOrganParentId() {
		return organParentId;
	}

	public String[] getRoleIds() {
		return roleIds;
	}

	public String getUid() {
		return uid;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setOrganCode(String organCode) {
		this.organCode = organCode;
	}

	public void setOrganId(String organId) {
		this.organId = organId;
	}

	public void setOrganName(String organName) {
		this.organName = organName;
	}

	public void setOrganType(String organType) {
		this.organType = organType;
	}

	public void setOrganParentId(String organParentId) {
		this.organParentId = organParentId;
	}

	public void setRoleIds(String... roleIds) {
		this.roleIds = roleIds;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public LinkedList<String> getUrls() {
		return urls;
	}

	public void setUrls(LinkedList<String> urls) {
		this.urls = urls;
	}

}
