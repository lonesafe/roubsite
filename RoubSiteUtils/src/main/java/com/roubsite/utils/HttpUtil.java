package com.roubsite.utils;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtil {
    private static Logger log = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 使用Get方式获取数据
     *
     * @param url     URL包括参数，http://HOST/XX?XX=XX&XXXX=XXXX
     * @param charset
     * @return
     */
    public static String sendGet(String url, String charset) {
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            // 设置通用的请求属性
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            result = IOUtils.toString(connection.getInputStream(), charset);
        } catch (Exception e) {
            log.error("发送GET请求出现异常:", e);
        }
        return result;
    }

    /**
     * POST请求，字符串形式数据
     *
     * @param url     请求地址
     * @param param   请求数据
     * @param charset 编码方式
     */
    public static String sendPostUrl(String url, String param, String charset) {
        try {
            return IOUtils.toString(sendPostUrl_I(url, param, charset), charset);
        } catch (IOException e) {
            log.error("转码失败:", e);
            return "";
        }
    }

    /**
     * POST请求，Map形式数据
     *
     * @param url     请求地址
     * @param param   请求数据
     * @param charset 编码方式
     */
    @SuppressWarnings("deprecation")
    public static String sendPost(String url, Map<String, String> param, String charset) {

        StringBuffer buffer = new StringBuffer();
        if (param != null && !param.isEmpty()) {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                buffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue())).append("&");

            }
        }
        if (buffer.length() > 2) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        String result = "";
        try {
            return sendPostUrl(url, buffer.toString(), charset);
        } catch (Exception e) {
            log.error("发送 POST 请求出现异常！", e);
        }
        return result;
    }

    public static InputStream sendPostUrl_I(String url, String param, String charset) {
        PrintWriter out = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
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