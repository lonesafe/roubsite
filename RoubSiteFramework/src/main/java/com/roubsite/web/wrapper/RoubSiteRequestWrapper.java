package com.roubsite.web.wrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

import com.roubsite.utils.StringUtils;

public class RoubSiteRequestWrapper extends HttpServletRequestWrapper {
	private Map<String, String> $_get = new HashMap<String, String>();
	private Map<String, Object> $_post = new HashMap<>();
	private HttpServletRequest request;
	private HttpServletRequest clone_request;

	public RoubSiteRequestWrapper(HttpServletRequest request) throws UnsupportedEncodingException {
		super(request);
		clone_request = new HttpServletRequestWrapper(request);
		request.setCharacterEncoding("UTF-8");
		this.request = request;
		if (StringUtils.isNotEmpty(this.request.getQueryString())) {
			this.URLRequest(this.request.getQueryString());
		}
		if (this.request.getMethod().equalsIgnoreCase("post")) {
			this.postRequest(request);
		}
	}

	public HttpServletRequest getHttpServletRequest() {
		return clone_request;
	}

	public String get$_get(String param) {
		return $_get.get(param);
	}

	public Object get$_post(String param) {
		return $_post.get(param);
	}

	/**
	 * 解析出url参数中的键值对 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
	 * 
	 * @param URL url参数列表
	 * @return url请求参数部分
	 * @throws UnsupportedEncodingException
	 */
	private void URLRequest(String queryString) throws UnsupportedEncodingException {

		String[] arrSplit = null;
		// 每个键值为一组
		arrSplit = queryString.split("[&]");
		for (String strSplit : arrSplit) {
			String[] arrSplitEqual = null;
			arrSplitEqual = strSplit.split("[=]");

			// 解析出键值
			if (arrSplitEqual.length > 1) {
				String word = "";
				word = URLDecoder.decode(arrSplitEqual[1], "UTF-8");
				// 正确解析
				this.$_get.put(arrSplitEqual[0], word);

			} else {
				if (arrSplitEqual[0] != "") {
					// 只有参数没有值，不加入
					this.$_get.put(arrSplitEqual[0], "");
				}
			}
		}
	}

	private void postRequest(HttpServletRequest request) {
		Enumeration<String> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (!$_get.containsKey(key)) {
				$_post.put(key, request.getParameter(key));
			}
		}

	}

	public String getPostJsonString() {
		return new String(getPostData());
	}

	public byte[] getPostData() {
		String contentType = request.getContentType();
		if (null != contentType && contentType.toUpperCase().contains("APPLICATION/JSON")) {
			try {
				return IOUtils.toByteArray(request.getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new byte[] {};
	}

}
