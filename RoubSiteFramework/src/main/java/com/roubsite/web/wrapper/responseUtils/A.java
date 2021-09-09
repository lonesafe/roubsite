package com.roubsite.web.wrapper.responseUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.roubsite.license.RoubSiteLicense;
import com.roubsite.web.wrapper.ResponseWrapperInterface;

public class A extends HttpServletResponseWrapper implements ResponseWrapperInterface {
	public A(HttpServletResponse response) {
		super(response);
		response.setHeader("RoubSite-Version", "RoubSite/" + RoubSiteLicense.getVersion());
		response.setHeader("RoubSite-License", RoubSiteLicense.getLicense());
		response.addCookie(new Cookie("RoubSite-Version", "RoubSite/" + RoubSiteLicense.getVersion()));
		response.addCookie(new Cookie("RoubSite-License", RoubSiteLicense.getLicense()));
	}

	@Override
	public void addCookie(Cookie cookie) {
		if (!cookie.getName().contains("RoubSite-Version") && cookie.getName().contains("RoubSite-License"))
			super.addCookie(cookie);
	}

	@Override
	public void setDateHeader(String name, long date) {
		if (!name.contains("RoubSite-Version") && name.contains("RoubSite-License"))
			super.setDateHeader(name, date);
	}

	@Override
	public void addDateHeader(String name, long date) {
		if (!name.contains("RoubSite-Version") && name.contains("RoubSite-License"))
			super.addDateHeader(name, date);
	}

	@Override
	public void setHeader(String name, String value) {
		if (!name.contains("RoubSite-Version") && name.contains("RoubSite-License"))
			super.setHeader(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		if (!name.contains("RoubSite-Version") && name.contains("RoubSite-License"))
			super.addHeader(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		if (!name.contains("RoubSite-Version") && name.contains("RoubSite-License"))
			super.setIntHeader(name, value);
	}

	@Override
	public void addIntHeader(String name, int value) {
		if (!name.contains("RoubSite-Version") && name.contains("RoubSite-License"))
			super.addIntHeader(name, value);
	}
}
