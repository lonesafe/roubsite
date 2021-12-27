package com.roubsite.web.filter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.roubsite.database.dao.DBUtils;
import com.roubsite.utils.StringUtils;
import com.roubsite.utils.UuidUtils;
import com.roubsite.web.error.RSErrorPage;
import com.roubsite.web.error.RSFrameworkException;
import com.roubsite.web.wrapper.SecurityRequestWrapper;

/**
 * 入参防注入拦截
 * 
 * @author lones 王振骁
 *
 */
public class RSParamCheck {

	// get拦截规则
	private final static String getFilter = "\\<.+javascript:window\\[.{1}\\\\x|<.*=(&#\\d+?;?)+?>|<.*(data|src)=data:text\\/html.*>|\\b(alert\\(|confirm\\(|expression\\(|prompt\\(|benchmark\\s*?"
			+ "\\(.*\\)|sleep\\s*?\\(.*\\)|\\b(group_)?concat[\\s\\/\\*]*?\\([^\\)]+?\\)|\\bcase[\\s\\/\\*]*?when[\\s\\/\\*]*?\\([^\\)]+?\\)|load_file\\s*?\\()|<[a-z]+?\\b[^>]*?"
			+ "\\bon([a-z]{4,})\\s*?=|^\\+\\/v(8|9)|\\b(and|or)\\b\\s*?([\\(\\)'\"\\d]+?=[\\(\\)'\"\\d]+?|[\\(\\)'\"a-zA-Z]+?=[\\(\\)'\"a-zA-Z]+?|>|<|\\s+?[\\w]+?\\s+?\\bin\\b\\s*?"
			+ "\\(|\\blike\\b\\s+?[\"'])|\\/\\*.*\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT\\s*(\\(.+\\)\\s*|@{1,2}.+?\\s*|\\s+?.+?|(`|'|\").*?(`|'|\")\\s*)|UPDATE\\s*(\\(.+\\)\\s"
			+ "*|@{1,2}.+?\\s*|\\s+?.+?|(`|'|\").*?(`|'|\")\\s*)SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE)@{0,2}(\\(.+\\)|\\s+?.+?\\s+?|(`|'|\").*?(`|'|\"))FROM(\\(.+\\)|\\s+?.+?|(`|'|\")"
			+ ".*?(`|'|\"))|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)";
	// post拦截规则
	private final static String postFilter = "<.*=(&#\\d+?;?)+?>|<.*data=data:text\\/html.*>|\\b(alert\\(|confirm\\(|expression\\(|prompt\\(|benchmark\\s*?\\(.*\\)|sleep\\s*?\\(.*\\)|\\b(group_)?concat"
			+ "[\\s\\/\\*]*?\\([^\\)]+?\\)|\\bcase[\\s\\/\\*]*?when[\\s\\/\\*]*?\\([^\\)]+?\\)|load_file\\s*?\\()|<[^>]*?\\b(onerror|onmousemove|onload|onclick|onmouseover)\\b|\\b"
			+ "(and|or)\\b\\s*?([\\(\\)'\"\\d]+?=[\\(\\)'\"\\d]+?|[\\(\\)'\"a-zA-Z]+?=[\\(\\)'\"a-zA-Z]+?|>|<|\\s+?[\\w]+?\\s+?\\bin\\b\\s*?\\(|\\blike\\b\\s+?[\"'])|\\/\\*.*\\*\\/|"
			+ "<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT\\s*(\\(.+\\)\\s*|@{1,2}.+?\\s*|\\s+?.+?|(`|'|\").*?(`|'|\")\\s*)|UPDATE\\s*(\\(.+\\)\\s*|@{1,2}.+?\\s*|\\s+?.+?|(`|'|\").*?(`|'|\")"
			+ "\\s*)SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE)(\\(.+\\)|\\s+?.+?\\s+?|(`|'|\").*?(`|'|\"))FROM(\\(.+\\)|\\s+?.+?|(`|'|\").*?(`|'|\"))|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)";
	// cookie拦截规则
	private final static String cookieFilter = "benchmark\\s*?\\(.*\\)|sleep\\s*?\\(.*\\)|load_file\\s*?\\(|\\b(and|or)\\b\\s*?([\\(\\)'\"\\d]+?=[\\(\\)'\"\\d]+?|[\\(\\)'\"a-zA-Z]+?=[\\(\\)'\"a-zA-Z]+?|>"
			+ "|<|\\s+?[\\w]+?\\s+?\\bin\\b\\s*?\\(|\\blike\\b\\s+?[\"'])|\\/\\*.*\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT\\s*(\\(.+\\)\\s*|@{1,2}.+?\\s*|\\s+?.+?|(`|'|\").*?(`|"
			+ "'|\")\\s*)|UPDATE\\s*(\\(.+\\)\\s*|@{1,2}.+?\\s*|\\s+?.+?|(`|'|\").*?(`|'|\")\\s*)SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE)@{0,2}(\\(.+\\)|\\s+?.+?\\s+?|(`|'|\").*?(`|'"
			+ "|\"))FROM(\\(.+\\)|\\s+?.+?|(`|'|\").*?(`|'|\"))|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)";

	public boolean check(ServletRequest req, ServletResponse resp) throws IOException, ServletException {
		SecurityRequestWrapper request = new SecurityRequestWrapper((HttpServletRequest) req);
		Map<String, String> get = request.$_get;
		Map<String, Object> post = request.$_post;
		// 检测get数据
		for (String key : get.keySet()) {
			if (stopAttack(key, get.get(key), getFilter, "GET", request)) {
				RSFrameworkException e = new RSFrameworkException(403, "GET检测到非法数据",
						((HttpServletRequest) request).getServletPath());
				new RSErrorPage((HttpServletResponse) resp, (HttpServletRequest) req, 403, null, "GET检测到非法数据").die(e);
				return false;
			}
		}

		// 检测post数据
		for (String key : post.keySet()) {
			if (stopAttack(key, post.get(key), postFilter, "POST", request)) {
				RSFrameworkException e = new RSFrameworkException(403, "POST检测到非法数据",
						((HttpServletRequest) request).getServletPath());
				new RSErrorPage((HttpServletResponse) resp, (HttpServletRequest) req, 403, null, "POST检测到非法数据").die(e);
				return false;
			}
		}

		// 检测cookie
		for (String key : post.keySet()) {
			if (stopAttack(key, post.get(key), postFilter, "COOKIE", request)) {
				RSFrameworkException e = new RSFrameworkException(403, "COOKIE检测到非法数据",
						((HttpServletRequest) request).getServletPath());
				new RSErrorPage((HttpServletResponse) resp, (HttpServletRequest) req, 403, null, "COOKIE检测到非法数据")
						.die(e);
				return false;
			}
		}
		return true;
	}

	/**
	 * 检测并拦截
	 *
	 * @param StrFiltKey   检测参数
	 * @param StrFiltValue 检测参数内容
	 * @param ArrFiltReq   拦截规则
	 * @param method       提交类型 post/get/cookie
	 */
	private boolean stopAttack(String StrFiltKey, Object StrFiltValue, String ArrFiltReq, String method,
			SecurityRequestWrapper request) {
		// 忽略大小写的写法
		Pattern pattern = Pattern.compile("/" + ArrFiltReq + "/is", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(StrFiltValue.toString());
		// 字符串是否与正则表达式相匹配
		boolean rs = matcher.find();
		if (rs) {
			try {
				addLog(getIpAddr(request), StringUtils.getUnixDate(), request.getServletPath(), method, StrFiltKey,
						StrFiltValue, request.getHeader("User-Agent"), request.getRequestURI());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	/**
	 * 增加防护日志
	 *
	 * @param ip
	 * @param time        攻击时间
	 * @param page        攻击页面
	 * @param method      提交类型 post、get、cookie
	 * @param rKey        被拦截的参数
	 * @param rData       被拦截参数携带的数据
	 * @param user_agent  用户标识
	 * @param request_uri 访问完整地址
	 * @throws SQLException
	 */
	private void addLog(String ip, String time, String page, String method, String rKey, Object rData,
			String user_agent, String request_uri) throws SQLException {
		String sql = "INSERT INTO RS_ATTACK_LOG(ID, IP, TIME, PAGE, METHOD, P_KEY, P_VALUE, UA, URL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String dataSource = "dataSource";
		DBUtils db = new DBUtils(dataSource);
		db.execUpdate(sql, new String[] { UuidUtils.getUuid(), ip, time, page, method, rKey, rData.toString(),
				user_agent, request_uri });

	}

	/**
	 * 获取用户真实IP地址，不使用request.getRemoteAddr()的原因是有可能用户使用了代理软件方式避免真实IP地址,
	 * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值
	 *
	 * @return ip
	 */
	private String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		System.out.println("x-forwarded-for ip: " + ip);
		if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个ip值，第一个ip才是真实ip
			if (ip.indexOf(",") != -1) {
				ip = ip.split(",")[0];
			}
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
			System.out.println("Proxy-Client-IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
			System.out.println("WL-Proxy-Client-IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
			System.out.println("HTTP_CLIENT_IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			System.out.println("HTTP_X_FORWARDED_FOR ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
			System.out.println("X-Real-IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			System.out.println("getRemoteAddr ip: " + ip);
		}
		System.out.println("获取客户端ip: " + ip);
		return ip;
	}
}
