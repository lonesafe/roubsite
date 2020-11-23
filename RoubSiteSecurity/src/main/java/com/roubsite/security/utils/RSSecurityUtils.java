package com.roubsite.security.utils;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.roubsite.security.securityInfo.RSSecurityBaseUserInfo;
import com.roubsite.utils.StringUtils;

/**
 * @author Rick Jone 王振骁
 */
public class RSSecurityUtils {
	/**
	 * 用户登录
	 * 
	 * @param session
	 * @param userInfo
	 */
	public static void login(HttpSession session, RSSecurityBaseUserInfo userInfo) {
		session.setAttribute("rsSecUserInfo", userInfo);
	}

	/**
	 * 用户退出
	 * 
	 * @param session
	 */
	public static void logout(HttpSession session) {
		session.setAttribute("rsSecUserInfo", null);
	}

	/**
	 * 检查用户是否登录
	 * 
	 * @param session
	 * @return
	 */
	public static boolean checkUserLogin(HttpSession session) {
		RSSecurityBaseUserInfo rsSecUserInfo = (RSSecurityBaseUserInfo) session.getAttribute("rsSecUserInfo");
		if (null == rsSecUserInfo) {
			return false;
		}
		if (StringUtils.isEmpty(rsSecUserInfo.getUid())) {
			return false;
		}
		return true;
	}

	/**
	 * 检查用户是否拥有某个权限集合的权限
	 * @param userInfo
	 * @param roles
	 * @return
	 */
	public static boolean checkUserRoles(RSSecurityBaseUserInfo userInfo, String... roles) {
		List<String> userRoleList = Arrays.asList(userInfo.getRoleIds());
		for (String str : roles) {
			if (userRoleList.contains(str)) {
				return true;
			}
		}
		return false;
	}

}
