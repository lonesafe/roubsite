package com.roubsite.web.action;

import com.roubsite.holder.RSFilterContextHolder;
import com.roubsite.smarty4j.Context;
import com.roubsite.smarty4j.Engine;
import com.roubsite.smarty4j.Template;
import com.roubsite.utils.ClassBean;
import com.roubsite.utils.ConfUtils;
import com.roubsite.utils.JsonUtils;
import com.roubsite.utils.StringUtils;
import com.roubsite.web.wrapper.Method;
import com.roubsite.web.wrapper.RoubSiteRequestWrapper;
import com.roubsite.web.wrapper.RoubSiteResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class RSAction implements IRSAction {
	protected final static Logger logger = LoggerFactory.getLogger(RSAction.class);
	public RoubSiteRequestWrapper request;
	public HttpServletResponse response;
	private Context context = new Context();
	private Engine e = new Engine();

	public void init(RoubSiteRequestWrapper req, RoubSiteResponseWrapper resp) {
		this.request = req;
		this.response = resp;
		HttpSession session = req.getSession();

		e.setTemplatePath(StringUtils.getWebContentPath() + "/templates/");

		Object xsm_file_version = session.getAttribute("xsm_file_version");
		if (null == xsm_file_version || "".equals(xsm_file_version)) {
			session.setAttribute("xsm_file_version", Long.toString(System.currentTimeMillis()));
		}
		context.set("__WEBPATH__", getWebPath());

	}

	public String getWebPath() {
		String contextPath = request.getContextPath();
		String scheme = request.getScheme();
		String serverName = request.getServerName();
		int port = request.getServerPort();
		if (StringUtils.isNotEmpty(contextPath)) {
			if (contextPath.startsWith("/")) {
				return scheme + "://" + serverName + ":" + port + contextPath;
			} else {
				return scheme + "://" + serverName + ":" + port + "/" + contextPath;
			}
		} else {
			return scheme + "://" + serverName + ":" + port + "/";
		}
	}

	public boolean __init__(HttpServletRequest req, HttpServletResponse resp, ClassBean classBean) {
		return true;
	}

	/**
	 * 获取Attribute的值
	 *
	 * @param attr
	 * @return
	 */
	public Object a(String attr) {
		return request.getAttribute(attr);
	}

	/**
	 * 定义模板上的变量
	 *
	 * @param name  变量名
	 * @param value 变量值
	 * @throws ServletException
	 * @throws IOException
	 */
	public void assign(String name, Object value) {
		// this.request.setAttribute(name, value);
		context.set(name, value);
	}

	/**
	 * 定义模板上的变量
	 *
	 * @param map
	 * @throws ServletException
	 * @throws IOException
	 */
	public void assign(Map<String, Object> map) {
		// this.request.setAttribute(name, value);
		context.putAll(map);
	}

	/**
	 * 定义模板上的变量
	 *
	 * @param bean
	 * @throws ServletException
	 * @throws IOException
	 */
	public void assign(Object bean) {
		context.putBean(bean);
	}

	/**
	 * 输出模板
	 *
	 * @param templatePath 模板相对该Action分组的路径
	 * @throws ServletException
	 * @throws IOException
	 */
	public void display(String templatePath) throws ServletException, IOException {
		ClassBean cb = RSFilterContextHolder.getRSFilterContext().getClassBean();
		String jspPath = cb.getTemplate() + "/" + templatePath;
		if (StringUtils.isEmpty(response.getContentType())) {
			response.setContentType("text/html;charset=UTF-8");
		}
		try {
			PrintWriter out = this.response.getWriter();
			Template tpl = e.getTemplate(jspPath);
			tpl.merge(context, out);
		} catch (Exception e) {
			e.printStackTrace();
			String message_sys = e.getMessage();
			logger.error("模板输出错误", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			request.setAttribute("message_sys", message_sys);
			error(500, "系统错误，请稍后再试！");
		}
		return;
	}

	public Object getParamSet(boolean returnJsonString) {
		String _paramSet = this.I("__paramSet__");
		if (returnJsonString) {
			return _paramSet;
		}
		try {
			Map data = new HashMap<>();
			Map paramSet = JsonUtils.readToObject(_paramSet, HashMap.class);
			int page = getInt(request.get$_post("__offset__"));
			int limit = getInt(request.get$_post("__limit__"));
			int offset = (page - 1) * limit;
			if (StringUtils.isNotEmptyObject(paramSet.get("__data__"))) {
				data = (Map) paramSet.get("__data__");
			}
			paramSet.put("__offset__", offset);
			paramSet.put("__limit__", limit);
			paramSet.put("__data__", data);
			return paramSet;
		} catch (IOException e) {
			return null;
		}
	}

	private int getInt(Object val) {
		try {
			return Integer.parseInt(val.toString());
		} catch (Exception e) {
			return 1;
		}
	}

	/**
	 * 获取Request的值(不建议使用该方法，某些第三方测试公司会检测post和get的严谨性)
	 *
	 * @param param
	 * @return
	 */
	@Deprecated
	public String I(String param) {
		return request.getParameter(param);
	}

	/**
	 * 获取Request的值(已废弃，不建议使用，只为兼容旧版本)
	 *
	 * @param param
	 * @return
	 */
	@Deprecated
	public String g(String param) {
		return request.getParameter(param);
	}

	/**
	 * 根据提交方式获取Request的值
	 *
	 * @param param
	 * @return
	 */
	public Object I(String param, Method method) {
		if (method == Method.GET) {
			return this.$_G(param);
		} else if (method == Method.POST) {
			return this.$_P(param);
		}
		return request.getParameter(param);
	}

	/**
	 * get方式获取url参数
	 *
	 * @param param
	 * @return
	 */
	public String $_G(String param) {
		return request.get$_get(param);
	}

	/**
	 * 获取post的参数
	 *
	 * @param param
	 * @return
	 */
	public Object $_P(String param) {
		return request.get$_post(param);
	}

	/**
	 * 获取一个session对象
	 *
	 * @return
	 */
	public HttpSession getSession() {
		return request.getSession();
	}

	/**
	 * 获取一个session对象
	 *
	 * @param bool
	 * @return
	 */
	public HttpSession getSession(boolean bool) {
		return request.getSession(bool);
	}

	/**
	 * 向客户端输出内容(该方法于display方法冲突，当有模板输出时，该方法将失效)
	 *
	 * @param obj 输出的内容
	 * @throws IOException
	 */
	public void print(Object obj) throws IOException {
		response.getWriter().print(obj);
		response.getWriter().flush();
	}

	/**
	 * 向客户端输出内容(该方法于display方法冲突，当有模板输出时，该方法将失效)
	 *
	 * @param obj 输出的内容
	 * @throws IOException
	 */
	public void println(Object obj) throws IOException {
		response.getWriter().println(obj);
		response.getWriter().flush();
	}

	/**
	 * 获取Session值
	 *
	 * @param sessionAttrName
	 * @return
	 */
	public Object s(String sessionAttrName) {
		return request.getSession().getAttribute(sessionAttrName);
	}

	/**
	 * 输出错误页面（在config.properties中定义errorPage的值）
	 *
	 * @param errorCode  错误码
	 * @param message    错误信息
	 * @param jumpUrl    跳转链接
	 * @param isAjax     是否ajax
	 * @param waitSecond 跳转时间
	 * @throws IOException
	 */
	public void error(int errorCode, String message, String jumpUrl, boolean isAjax, int waitSecond)
			throws IOException {
		this.displayAndJump(message, 0, jumpUrl, isAjax, errorCode, waitSecond);
	}

	/**
	 * 输出错误页面（在config.properties中定义errorPage的值）
	 *
	 * @param errorCode 错误码
	 * @param message   错误信息
	 * @throws IOException
	 */
	public void error(int errorCode, String message) throws IOException {
		this.displayAndJump(message, 0, null, false, errorCode, 3);
	}

	/**
	 * 输出错误页面（在config.properties中定义errorPage的值）
	 *
	 * @param message 错误信息
	 * @throws IOException
	 */
	public void error(Message message) throws IOException {
		this.displayAndJump(message.getMessage(), 0, message.getJumpUrl(), message.isAjax(), message.getErrorCode(),
				message.getWaitSecond());
	}

	/**
	 * 输出成功页面（在config.properties中定义successPage的值）
	 *
	 * @param message    提示信息
	 * @param jumpUrl    页面跳转地址
	 * @param isAjax     是否为Ajax方式
	 * @param waitSecond 跳转时间
	 * @throws IOException
	 */
	public void success(String message, String jumpUrl, boolean isAjax, int waitSecond) throws IOException {
		this.displayAndJump(message, 1, jumpUrl, isAjax, 200, waitSecond);
	}

	/**
	 * 输出成功页面（在config.properties中定义successPage的值）
	 *
	 * @param message 提示
	 * @throws IOException
	 */
	public void success(String message) throws IOException {
		this.displayAndJump(message, 1, null, false, 200, 3);
	}

	/**
	 * 输出成功页面（在config.properties中定义successPage的值）
	 *
	 * @param message Message
	 * @throws IOException
	 */
	public void success(Message message) throws IOException {
		this.displayAndJump(message.getMessage(), 1, message.getJumpUrl(), message.isAjax(), 200,
				message.getWaitSecond());
	}

	/**
	 * 默认跳转操作 支持错误导向和正确跳转 提示页面为可配置 支持模板标签
	 *
	 * @param message    提示信息
	 * @param type       1，success 0 error
	 * @param jumpUrl    跳转url
	 * @param isAjax     是否是ajax
	 * @param errorCode  错误码
	 * @param waitSecond 等待时间
	 * @throws IOException
	 */
	private void displayAndJump(String message, int type, String jumpUrl, boolean isAjax, int errorCode, int waitSecond)
			throws IOException {
		String successPage = "";
		String errorPage = "";

		if (StringUtils.isNotEmpty(ConfUtils.getStringConf("RoubSite.global.errorPage", ""))) {
			errorPage = ConfUtils.getStringConf("RoubSite.global.errorPage", "");
		}
		if (StringUtils.isNotEmpty(ConfUtils.getStringConf("RoubSite.global.successPage", ""))) {
			successPage = ConfUtils.getStringConf("RoubSite.global.successPage", "");
		}

		this.response.reset();
		this.response.setCharacterEncoding("UTF-8");
		this.response.setContentType("text/html;charset=UTF-8");
		String topHtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + "<head>\r\n"
				+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r\n"
				+ "<title>跳转提示</title>\r\n" + "<style type=\"text/css\">\r\n" + "*{ padding: 0; margin: 0; }\r\n"
				+ "body{ background: #fff; font-family: '微软雅黑'; color: #333; font-size: 16px; }\r\n"
				+ ".system-message{ padding: 24px 48px; }\r\n"
				+ ".system-message h1{ font-size: 100px; font-weight: normal; line-height: 120px; margin-bottom: 12px; }\r\n"
				+ ".system-message .jump{ padding-top: 10px}\r\n" + ".system-message .jump a{ color: #333;}\r\n"
				+ ".system-message .success,.system-message .error{ line-height: 1.8em; font-size: 36px }\r\n"
				+ ".system-message .detail{ font-size: 12px; line-height: 20px; margin-top: 12px; display:none}\r\n"
				+ "</style>\r\n" + "</head>\r\n" + "<body>\r\n" + "<div class=\"system-message\">";
		String html = "";
		if (type == 1) {
			this.response.setStatus(200);
			if (StringUtils.isEmpty(successPage)) {
				String bottomHtml = "<p class=\"detail\"></p>\r\n" + "<p class=\"jump\">\r\n"
						+ "页面自动 <a id=\"href\" href=\""
						+ (StringUtils.isEmpty(jumpUrl)
								? request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
										+ request.getContextPath()
								: jumpUrl)
						+ "\">跳转</a> 等待时间： <b id=\"wait\">" + Integer.toString((0 <= waitSecond ? 3 : waitSecond))
						+ "</b>\r\n" + "</p>\r\n" + "</div>\r\n" + "<script type=\"text/javascript\">\r\n"
						+ "(function(){\r\n"
						+ "var wait = document.getElementById('wait'),href = document.getElementById('href').href;\r\n"
						+ "var interval = setInterval(function(){\r\n" + "	var time = --wait.innerHTML;\r\n"
						+ "	if(time <= 0) {\r\n" + "		location.href = href;\r\n"
						+ "		clearInterval(interval);\r\n" + "	};\r\n" + "}, 1000);\r\n" + "})();\r\n"
						+ "</script>\r\n" + "</body>\r\n" + "</html>";
				html = "<h1>(〃'▽'〃)</h1>\r\n" + "<p class=\"success\">" + message + "</p>\r\n";
				response.getWriter().println(topHtml + html + bottomHtml);
			} else {
				try {
					File file = new File(StringUtils.getWebContentPath() + "/" + successPage);// 定义一个file对象，用来初始化FileReader
					FileReader reader = new FileReader(file);// 定义一个fileReader对象，用来初始化BufferedReader
					BufferedReader bReader = new BufferedReader(reader);// new一个BufferedReader对象，将文件内容读取到缓存
					StringBuilder sb = new StringBuilder();// 定义一个字符串缓存，将字符串存放缓存中
					String s = "";
					while ((s = bReader.readLine()) != null) {// 逐行读取文件内容，不读取换行符和末尾的空格
						sb.append(s + "\n");// 将读取的字符串添加换行符后累加存放在缓存中
					}
					bReader.close();
					String str = sb.toString();
					str.replaceAll("\\{\\$message\\}", message);
					response.getWriter().println(str);
				} catch (Exception e2) {
				}

			}

		} else {

			this.response.setStatus(errorCode);
			if (StringUtils.isEmpty(errorPage)) {
				String bottomHtml = "<p class=\"detail\"></p>\r\n" + "<p class=\"jump\">\r\n"
						+ "页面自动 <a id=\"href\" href=\""
						+ (StringUtils.isEmpty(jumpUrl) ? "javascript: history.back(-1);" : jumpUrl)
						+ "\">跳转</a> 等待时间： <b id=\"wait\">" + Integer.toString((0 <= waitSecond ? 3 : waitSecond))
						+ "</b>\r\n" + "</p>\r\n" + "</div>\r\n" + "<script type=\"text/javascript\">\r\n"
						+ "(function(){\r\n"
						+ "var wait = document.getElementById('wait'),href = document.getElementById('href').href;\r\n"
						+ "var interval = setInterval(function(){\r\n" + "	var time = --wait.innerHTML;\r\n"
						+ "	if(time <= 0) {\r\n" + "		location.href = href;\r\n"
						+ "		clearInterval(interval);\r\n" + "	};\r\n" + "}, 1000);\r\n" + "})();\r\n"
						+ "</script>\r\n" + "</body>\r\n" + "</html>";
				html = "<h1>o(╥﹏╥)o</h1>\r\n" + "<p class=\"error\">" + message + "</p>\r\n";
				try {
					response.getWriter().println(topHtml + html + bottomHtml);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				try {
					File file = new File(StringUtils.getWebContentPath() + "/" + errorPage);// 定义一个file对象，用来初始化FileReader
					FileReader reader = new FileReader(file);// 定义一个fileReader对象，用来初始化BufferedReader
					BufferedReader bReader = new BufferedReader(reader);// new一个BufferedReader对象，将文件内容读取到缓存
					StringBuilder sb = new StringBuilder();// 定义一个字符串缓存，将字符串存放缓存中
					String s = "";
					while ((s = bReader.readLine()) != null) {// 逐行读取文件内容，不读取换行符和末尾的空格
						sb.append(s + "\n");// 将读取的字符串添加换行符后累加存放在缓存中
					}
					bReader.close();
					String str = sb.toString();
					str.replaceAll("\\{\\$errorCode\\}", Integer.toString(errorCode));
					str.replaceAll("\\{\\$message\\}", message);
					response.getWriter().println(str);

				} catch (Exception e2) {
				}

			}
		}
		try {

			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public String getString(Object obj) {
		if (StringUtils.isNotEmptyObject(obj)) {
			return obj.toString();
		}
		return null;
	}

}
