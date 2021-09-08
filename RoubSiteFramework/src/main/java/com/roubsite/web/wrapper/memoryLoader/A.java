package com.roubsite.web.wrapper.memoryLoader;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import com.roubsite.license.RoubSiteLicense;
import com.roubsite.web.wrapper.ResponseWrapperInterface;

public class A implements ResponseWrapperInterface {
	@Override
	public void preseInterface(HttpServletResponse response) {
		response.setHeader("roubsite_version", RoubSiteLicense.getVersion());
		response.setHeader("roubsite_license", RoubSiteLicense.getLicense());
		response.addCookie(new Cookie("roubsite_version", RoubSiteLicense.getVersion()));
		response.addCookie(new Cookie("roubsite_license", RoubSiteLicense.getLicense()));
	}
}