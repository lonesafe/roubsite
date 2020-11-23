package com.roubsite.web.wrapper;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class RoubSiteResponseWrapper extends HttpServletResponseWrapper {

	public RoubSiteResponseWrapper(HttpServletResponse response) {
		super(response);
		
	}

}
