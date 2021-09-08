package com.roubsite.license;

/**
 * @category本类为版权授权信息类 
 * 存储了框架的版本信息和许可证信息<br>许可证相关代码还在研发中<h1>恳请大家不要删除相关许可信息</h1>
 * 
 * @author lones
 *
 */
public class RoubSiteLicense {
	private final static String version = "V4";
	private static String license = "Open-Source(www.roubsite.com)";

	public static String getVersion() {
		return version;
	}

	public static String getLicense() {
		return license;
	}

}
