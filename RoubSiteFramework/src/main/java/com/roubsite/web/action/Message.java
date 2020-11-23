package com.roubsite.web.action;

public class Message {
	private String message;
	private String jumpUrl;
	private boolean isAjax;
	private int errorCode;
	private int waitSecond;

	public int getErrorCode() {
		return errorCode;
	}

	public String getJumpUrl() {
		return jumpUrl;
	}

	public String getMessage() {
		return message;
	}

	public int getWaitSecond() {
		return waitSecond;
	}

	public boolean isAjax() {
		return isAjax;
	}

	public void setAjax(boolean isAjax) {
		this.isAjax = isAjax;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public void setJumpUrl(String jumpUrl) {
		this.jumpUrl = jumpUrl;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setWaitSecond(int waitSecond) {
		this.waitSecond = waitSecond;
	}

}
