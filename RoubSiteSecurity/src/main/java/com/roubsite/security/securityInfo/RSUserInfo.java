package com.roubsite.security.securityInfo;

/**
 * @author Rick Jone 王振骁
 */
public class RSUserInfo {
	private String uid;
	private String userName;
	private String userStatus;
	private String[] roleIds;
	private String organId;
	private String organCode;
	private String organName;
	private String organType;
	private String organParentId;

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

	public RSUserInfo(RSSecurityBaseUserInfo rsSecUserInfo) {
		if (null != rsSecUserInfo) {
			this.uid = rsSecUserInfo.getUid();
			this.userName = rsSecUserInfo.getUserName();
			this.userStatus = rsSecUserInfo.getUserStatus();
			this.roleIds = rsSecUserInfo.getRoleIds();
			this.organId = rsSecUserInfo.getOrganId();
			this.organCode = rsSecUserInfo.getOrganCode();
			this.organName = rsSecUserInfo.getOrganName();
			this.organType = rsSecUserInfo.getOrganType();
			this.organParentId = rsSecUserInfo.getOrganParentId();
		} else {
			this.uid = null;
			this.userName = null;
			this.userStatus = null;
			this.roleIds = null;
			this.organId = null;
			this.organCode = null;
			this.organName = null;
			this.organType = null;
			this.organParentId = null;
		}

	}

}
