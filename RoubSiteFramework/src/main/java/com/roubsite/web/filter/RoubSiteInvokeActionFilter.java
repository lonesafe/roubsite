package com.roubsite.web.filter;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.roubsite.holder.ActionClassBean;
import com.roubsite.holder.RSFilterContextHolder;
import com.roubsite.utils.RequestURIFilter;
import com.roubsite.utils.StringUtils;
import com.roubsite.web.classBean.ClassBean;
import com.roubsite.web.error.RSErrorPage;
import com.roubsite.web.wrapper.RoubSiteRequestWrapper;
import com.roubsite.web.wrapper.RoubSiteResponseWrapper;

public class RoubSiteInvokeActionFilter implements Filter {
	static Logger logger = Logger.getLogger(RoubSiteInvokeActionFilter.class);
	private static RequestURIFilter excludes;
	private final static String isInclude = "__IS__INCLUDE__";

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		RoubSiteRequestWrapper req = (RoubSiteRequestWrapper) request;
		RoubSiteResponseWrapper resp = (RoubSiteResponseWrapper) response;

		req.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// 判断静态资源
		if (excludes.matches(req)
				|| (null != request.getAttribute(isInclude) && (boolean) request.getAttribute(isInclude))) {
			chain.doFilter(request, response);
			return;
		} else {
			if (!StringUtils.isNotEmptyObject(RSFilterContextHolder.getRSFilterContext())) {
				chain.doFilter(request, response);
				return;
			}
			ClassBean cb = RSFilterContextHolder.getRSFilterContext().getClassBean();
			if (StringUtils.isNotEmpty(cb.getErroMessage())) {
				// 获取ClassBean失败
				chain.doFilter(request, response);
				return;
			}
			ActionClassBean acb = RSFilterContextHolder.getRSFilterContext().getActionClassBean();
			Class<?> clazz = acb.getClazz();
			Object action = acb.getActionObject();
			Method method = acb.getMethod();
			// 判断静态资源
			if (excludes.matches(req)
					|| (null != request.getAttribute(isInclude) && (boolean) request.getAttribute(isInclude))) {
				chain.doFilter(request, response);
				return;
			} else {
				if (StringUtils.isNotEmpty(cb.getErroMessage())) {
					// 获取ClassBean失败
					chain.doFilter(request, response);
					return;
				}
				// 默认初始化
				try {
					if (initAction(clazz, cb, req, resp, action)) {
						// 执行action方法
						try {
							method.invoke(action, new Object[] {});
						} catch (Exception e) {
							logger.error("action执行方法失败：", e);
							new RSErrorPage(resp, req, 500, null, "系统错误，请稍后再试！").die(e);
						}
					}
				} catch (Exception e2) {
					logger.error("初始化action出错", e2);
					new RSErrorPage(resp, req, 500, null, "系统错误，请稍后再试！").die(e2);
					return;
				}
				return;
			}
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		String excludes = fConfig.getInitParameter("excludes");
		RoubSiteInvokeActionFilter.excludes = new RequestURIFilter(excludes);
	}

	// 初始化action
	private boolean initAction(Class<?> clazz, ClassBean cb, RoubSiteRequestWrapper req, RoubSiteResponseWrapper resp,
			Object action) throws Exception {
		Method init = clazz.getMethod("init", new Class[] { RoubSiteRequestWrapper.class, RoubSiteResponseWrapper.class });
		init.setAccessible(true);
		init.invoke(action, new Object[] { req, resp });

		// 自定义初始化
		Method __init__ = clazz.getMethod("__init__",
				new Class[] { HttpServletRequest.class, HttpServletResponse.class, ClassBean.class });
		__init__.setAccessible(true);
		return (boolean) __init__.invoke(action, new Object[] { req, resp, cb });
	}
}
