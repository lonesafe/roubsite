package com.roubsite.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.roubsite.holder.RSFilterContextHolder;
import com.roubsite.utils.RequestURIFilter;
import com.roubsite.utils.StringUtils;
import com.roubsite.web.wrapper.RoubSiteRequestWrapper;
import com.roubsite.web.wrapper.RoubSiteResponseWrapper;

public class RSContextFilter implements Filter {

	private static RequestURIFilter excludes;
	private final static String isInclude = "__IS__INCLUDE__";

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		RoubSiteRequestWrapper request = new RoubSiteRequestWrapper((HttpServletRequest) req);
		RoubSiteResponseWrapper response = new RoubSiteResponseWrapper((HttpServletResponse) resp);
		// 判断静态资源
		if (excludes.matches((HttpServletRequest) req)
				|| (null != request.getAttribute(isInclude) && (boolean) request.getAttribute(isInclude))) {
			chain.doFilter(req, resp);
			return;
		} else {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
		}

		try {
			chain.doFilter(request, response);
		} finally {
			RSFilterContextHolder.destoryRSFilterContext();
		}
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		String excludes = fConfig.getInitParameter("excludes");
		RSContextFilter.excludes = new RequestURIFilter(excludes);
	}

}
