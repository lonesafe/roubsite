package com.roubsite.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
	private static Logger log = LoggerFactory.getLogger(HttpUtil.class);

	/**
	 * 使用Get方式获取数据
	 *
	 * @param url URL包括参数
	 * @return 字符串数据
	 * @throws IOException 异常
	 */
	public static String sendGet(String url) throws IOException {
		return sendGet(url, null, "utf-8");
	}

	/**
	 * 使用Get方式获取数据
	 *
	 * @param url    URL包括参数
	 * @param header 头部信息
	 * @return 字符串数据
	 * @throws IOException 异常
	 */
	public static String sendGet(String url, Map<String, String> header) throws IOException {
		return sendGet(url, header, "utf-8");
	}

	/**
	 * 使用Get方式获取数据
	 *
	 * @param url     URL包括参数
	 * @param header  头部信息
	 * @param charset 编码
	 * @return 字符串数据
	 * @throws IOException 异常
	 */
	public static String sendGet(String url, Map<String, String> header, String charset) throws IOException {
		return IOUtils.toString(sendGet_I(url, header), charset);
	}

	/**
	 * 使用Get方式获取数据
	 *
	 * @param url     URL包括参数
	 * @return 取回的流数据
	 * @throws IOException
	 */
	public static InputStream sendGet_I(String url) throws IOException {
		return sendGet_I(url, null);
	}

	/**
	 * 使用Get方式获取数据
	 *
	 * @param url     URL包括参数
	 * @param header  头部信息
	 * @return 取回的流数据
	 */
	public static InputStream sendGet_I(String url, Map<String, String> header) {
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			if (StringUtils.isNotEmptyObject(header)) {
				for (String key : header.keySet()) {
					String value = header.get(key);
					connection.setRequestProperty(key, value);
				}
			}
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			// 设置通用的请求属性
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
			// 建立实际的连接
			connection.connect();
			// 定义 BufferedReader输入流来读取URL的响应
			return connection.getInputStream();
		} catch (Exception e) {
			log.error("发送GET请求出现异常:", e);
		}
		return null;
	}

	/**
	 * POST请求，字符串形式数据
	 *
	 * @param url   请求地址
	 * @param param 请求数据
	 * @return 字符串数据
	 */
	public static String sendPostUrl(String url, String param) {
		return sendPostUrl(url, param, null, "utf-8");
	}

	/**
	 * POST请求，字符串形式数据
	 *
	 * @param url    请求地址
	 * @param param  请求数据
	 * @param header 头部信息
	 * @return 字符串数据
	 */
	public static String sendPostUrl(String url, String param, Map<String, String> header) {
		return sendPostUrl(url, param, header, "utf-8");
	}

	/**
	 * POST请求，字符串形式数据
	 *
	 * @param url     请求地址
	 * @param param   请求数据
	 * @param header  头部信息
	 * @param charset 编码方式
	 * @return 字符串数据
	 */
	public static String sendPostUrl(String url, String param, Map<String, String> header, String charset) {
		try {
			return IOUtils.toString(sendPostUrl_I(url, param, header), charset);
		} catch (IOException e) {
			log.error("转码失败:", e);
			return "";
		}
	}

	/**
	 * POST请求，字符串形式数据
	 *
	 * @param url   请求地址
	 * @param param 请求数据
	 * @return 取回的流数据
	 */
	public static InputStream sendPostUrl_I(String url, String param) {
		return sendPostUrl_I(url, param, null);
	}

	/**
	 * POST请求，字符串形式数据
	 *
	 * @param url     请求地址
	 * @param param   请求数据
	 * @param header  头部信息
	 * @return 取回的流数据
	 */
	public static InputStream sendPostUrl_I(String url, String param, Map<String, String> header) {
		PrintWriter out = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
			if (StringUtils.isNotEmptyObject(header)) {
				for (String key : header.keySet()) {
					String value = header.get(key);
					conn.setRequestProperty(key, value);
				}
			}

			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			return conn.getInputStream();
		} catch (Exception e) {
			log.error("发送 POST 请求出现异常:", e);
		}
		return null;
	}
}