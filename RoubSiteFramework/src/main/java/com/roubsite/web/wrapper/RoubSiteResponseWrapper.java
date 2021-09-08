package com.roubsite.web.wrapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.roubsite.utils.UuidUtils;
import com.roubsite.web.wrapper.responseUtils.ResponseWrapperUtils;

public class RoubSiteResponseWrapper extends HttpServletResponseWrapper implements ResponseWrapperInterface {
	private ResponseWrapperInterface superb;
	private static final String tempRequestBeanName = "\u004d".toUpperCase() + UuidUtils.getUuid().substring(4);
	private static ResponseWrapperUtils loader;
	private static Class<?> responseClass;
	public RoubSiteResponseWrapper(HttpServletResponse response) {
		super(response);
		try {
			Object obj = responseClass.getDeclaredConstructor(HttpServletResponse.class).newInstance(response);
			superb = (ResponseWrapperInterface) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static {
		try {
			loader = ResponseWrapperUtils.getInstrance();
			loader.registerResponse(tempRequestBeanName,
					"H4sIAAAAAAAAAJWMMQ7CMAxFr5IT+AKMLCAxtQNzEhlhSGvLdluOX0uRGBAMLJb89P6jSVg9PfKaX2Coa0"
							+ "OHu7vAkflJeEj00zjFGTsY0IRn+1e/ahZBfa8qT6C8FCNHaFQxHBgCjAEu/f8ub1hg6zX4qJ9nR73lGk"
							+ "tZSlRTbdks7ftMYHX7AAAA",
					"H4sIAAAAAAAAAF3JMQqAMAwF0KvkOLrq4FzrFwppGpKvCF7e3bc+PIQdKRPpK+JWcEH6sMQWxR0hrbuiw5jyq9mIOEuFvOLXrq1+nc3yBk8AAAA=",
					"H4sIAAAAAAAAAMWTwU7DMAyGX8X0MDVijAfgggSHISGQVol71pjJoiRVkpYD2ruTLllLoNuC0NRTnDj+/y+2ki+trQvUbYV2h"
							+ "aZW0iDoEDD4BNPUqPP+5KZPLgzaJXLhstlKNeuCLF69oDakZDaH/uw6g0vYbx6pxK52gzZczdkpzVDjNEdUQhircCHulHoj"
							+ "zCV+QAj/CfkH/URgp7iF2+cWtSaBUDfrikpoFQkYDPwC5W7p5kGvkF/4bSf3xN+d1qJU0nKSZuSVDGYzSCnYYzPmh/7tmcH+"
							+ "MLCb2z23GGZXWE1yA9JZzaFSLhQu2dN35yeJD136RRlbe8+d3dH2TkUbWyfRDn8iIg2bllfNGVsbgXqvo32diHRwTiN1L3u"
							+ "Qo20lac/d08E5ua0TwUbOP2G38AUNX/cIPQYAAA==");

			responseClass = loader.findClass(tempRequestBeanName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean containsHeader(String name) {
		return superb.containsHeader(name);
	}

	@Override
	public String encodeURL(String url) {
		return superb.encodeURL(url);
	}

	@Override
	public String encodeRedirectURL(String url) {
		return superb.encodeRedirectURL(url);
	}

	@Override
	@Deprecated
	public String encodeUrl(String url) {
		return superb.encodeUrl(url);
	}

	@Deprecated
	@Override
	public String encodeRedirectUrl(String url) {
		return superb.encodeRedirectUrl(url);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		superb.sendError(sc, msg);
	}

	@Override
	public void sendError(int sc) throws IOException {
		superb.sendError(sc);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		superb.sendRedirect(location);
	}

	@Override
	public void setDateHeader(String name, long date) {
		superb.setDateHeader(name, date);
	}

	@Override
	public void setStatus(int sc) {
		superb.setStatus(sc);
	}

	@Override
	@Deprecated
	public void setStatus(int sc, String sm) {
		superb.setStatus(sc, sm);
	}

	@Override
	public int getStatus() {
		return superb.getStatus();
	}

	@Override
	public String getHeader(String name) {
		return superb.getHeader(name);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return superb.getHeaders(name);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return superb.getHeaderNames();
	}

	@Override
	public String getCharacterEncoding() {
		return superb.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return superb.getContentType();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return superb.getOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return superb.getWriter();
	}

	@Override
	public void setCharacterEncoding(String charset) {
		superb.setCharacterEncoding(charset);
	}

	@Override
	public void setContentLength(int len) {
		superb.setContentLength(len);
	}

	@Override
	public void setContentType(String type) {
		superb.setContentType(type);
	}

	@Override
	public void setBufferSize(int size) {
		superb.setBufferSize(size);
	}

	@Override
	public int getBufferSize() {
		return superb.getBufferSize();
	}

	@Override
	public void flushBuffer() throws IOException {
		superb.flushBuffer();
	}

	@Override
	public void resetBuffer() {
		superb.resetBuffer();
	}

	@Override
	public boolean isCommitted() {
		return superb.isCommitted();
	}

	@Override
	public void reset() {
		superb.reset();
	}

	@Override
	public void setLocale(Locale loc) {
		superb.setLocale(loc);
	}

	@Override
	public Locale getLocale() {
		return superb.getLocale();
	}

	@Override
	public void addCookie(Cookie cookie) {
		superb.addCookie(cookie);
	}

	@Override
	public void addDateHeader(String name, long date) {
		superb.addDateHeader(name, date);
	}

	@Override
	public void setHeader(String name, String value) {
		superb.setHeader(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		superb.addHeader(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		superb.setIntHeader(name, value);
	}

	@Override
	public void addIntHeader(String name, int value) {
		superb.addIntHeader(name, value);
	}
	
}
