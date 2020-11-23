package com.roubsite.web.error;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.roubsite.utils.StringUtils;

public class RSErrorPage {
	private int errorCode;
	private String errorPage;
	private String message;
	private HttpServletResponse resp;
	private HttpServletRequest req;

	public RSErrorPage(HttpServletResponse resp, HttpServletRequest req) {
		this.resp = resp;
		this.req = req;
	}

	public RSErrorPage(HttpServletResponse resp, HttpServletRequest req, int errorCode, String errorPage,
			String message) {
		this.resp = resp;
		this.errorCode = errorCode;
		this.errorPage = errorPage;
		this.message = message;
	}

	public RSErrorPage errorCode(int errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public RSErrorPage errorPage(String errorPage) {
		this.errorPage = errorPage;
		return this;
	}

	public RSErrorPage message(String message) {
		this.message = message;
		return this;
	}

	public void die(Exception e) {
		resp.setStatus(500);
		if (StringUtils.isEmpty(errorPage)) {
			String html = "<html>\r\n" + 
					" <head>\r\n" + 
					"  <title>RoubSite - Error report</title>\r\n" + 
					"  <style><!--H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;} H2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;} H3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;} BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;} B {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;} P {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;}A {color : black;}A.name {color : black;}HR {color : #525D76;}--></style> \r\n" + 
					" </head>\r\n" + 
					" <body>\r\n" + 
					"  <h1>HTTP 状态码 "+this.errorCode+" - "+e.toString()+"</h1>\r\n" + 
					"  <hr size=\"1\" noshade=\"noshade\" />\r\n" + 
					"  <p><b>类型</b> 异常</p>\r\n" + 
					"  <p><b>信息</b> <u>"+e.toString()+"</u></p>\r\n" + 
					"  <p><b>exception</b> </p>\r\n" + 
					"  <pre>"+this.message+
					"  </pre>\r\n" + 
					"  <p></p>\r\n" + 
					"  <p><b>note</b> <u>The full stack trace of the root cause is available in the Apache Tomcat logs.</u></p>\r\n" + 
					"  <hr size=\"1\" noshade=\"noshade\" />\r\n" + 
					"  <h3><a href='http://www.roubsite.com'>RoubSite V2.0</a></h3>\r\n" + 
					" </body>\r\n" + 
					"</html>";
			try {
				resp.getWriter().println(html);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			try {
				File file = new File(StringUtils.getWebContentPath() + "/" + this.errorPage);// 定义一个file对象，用来初始化FileReader
				FileReader reader = new FileReader(file);// 定义一个fileReader对象，用来初始化BufferedReader
				BufferedReader bReader = new BufferedReader(reader);// new一个BufferedReader对象，将文件内容读取到缓存
				StringBuilder sb = new StringBuilder();// 定义一个字符串缓存，将字符串存放缓存中
				String s = "";
				while ((s = bReader.readLine()) != null) {// 逐行读取文件内容，不读取换行符和末尾的空格
					sb.append(s + "\n");// 将读取的字符串添加换行符后累加存放在缓存中
				}
				bReader.close();
				String str = sb.toString();
				str.replaceAll("\\{\\$errorCode\\}", Integer.toString(this.errorCode));
				str.replaceAll("\\{\\$message\\}", this.message);
				resp.getWriter().println(str);
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
		try {
			resp.getWriter().flush();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

	}
}
