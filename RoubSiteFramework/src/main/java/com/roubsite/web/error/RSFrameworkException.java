package com.roubsite.web.error;

import com.roubsite.utils.StringUtils;

public class RSFrameworkException extends Exception {
	private static final long serialVersionUID = 1L;
	private String detailMessage;

	public RSFrameworkException(int errorCode, String message, String url) {
		StringBuffer m = new StringBuffer("[错误代码:");
		m.append(errorCode);
		m.append("],[错误类型:" + message + "]");
		if (StringUtils.isNotEmpty(url))
			m.append("[" + url + "]");
		detailMessage = m.toString();
	}

	@Override
	public String getMessage() {
		return detailMessage;
	}
}
