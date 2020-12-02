package com.roubsite.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "deprecation"})
public class StringUtils {
    private static Logger log = Logger.getLogger(StringUtils.class);
    private static StringBuffer buffer = new StringBuffer(
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");

    /**
     * 编码转换
     *
     * @param input          待转换字符串
     * @param sourceEncoding 源编码
     * @param targetEncoding 目的编码
     * @return
     */
    public static String changeEncoding(String input, String sourceEncoding, String targetEncoding) {
        if ((input == null) || (input.equals(""))) {
            return input;
        }
        try {
            byte[] bytes = input.getBytes(sourceEncoding);
            return new String(bytes, targetEncoding);
        } catch (Exception localException) {
        }
        return input;
    }

    /**
     * 清理字符串中的空字符串和null
     *
     * @param input
     * @return
     */
    public static String clearNull(String input) {
        return (isEmpty(input)) ? "" : input;
    }

    public static String convertFnTag(String str) {
        if (isNotEmpty(str)) {
            if (str.contains("${fn:getLink('"))
                return getSplicStr(str, "\\$\\{fn\\:getLink\\(\\'", "')}");
            if (str.contains("${fn:getLink(")) {
                return getSplicStr(str, "\\$\\{fn\\:getLink\\(", ")}");
            }
            return null;
        }

        return null;
    }

    /**
     * 转换为数字字符串
     *
     * @param str
     * @return
     */
    public static String convertNumber(String str) {
        if (isEmpty(str))
            return "0";
        try {
            if (str.matches("^[0-9]*$"))
                return str;
            return new DecimalFormat("0.0").format(Double.parseDouble(str));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "0";
    }

    /**
     * 时间转文本
     *
     * @param date
     * @return
     */
    public static String dateTo14String(Date date) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        return dateFormat.format(date);
    }

    /**
     * 时间转文本yyyy年MM月dd日 HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String dateToChineseString(Date date) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

        return dateFormat.format(date);
    }

    public static String formatFraction(double num, int minFractionDigits, int maxFractionDigits) {
        NumberFormat nb = NumberFormat.getInstance();
        nb.setMaximumFractionDigits(maxFractionDigits);
        nb.setMinimumFractionDigits(minFractionDigits);
        nb.setGroupingUsed(false);
        String rate = nb.format(num);
        return rate;
    }

    public static String getBefDateString(int day_i) {
        Calendar day = Calendar.getInstance();
        day.add(5, day_i);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(day.getTime());
    }

    public static int getBytesLength(String input) {
        if (input == null) {
            return 0;
        }
        int bytesLength = input.getBytes().length;
        return bytesLength;
    }

    /**
     * 获取当前时间
     *
     * @return yyyy-MM-dd
     */
    public static String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }

    /**
     * 格式化当前时间
     *
     * @param dateFormat
     * @return
     */
    public static String getDateByFormat(String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime());
    }

    /**
     * 获取当前时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static int getDateTimeLen() {
        return "yyyy-MM-dd HH:mm:ss".length();
    }

    /**
     * 首字母转换为大写
     *
     * @param str
     * @return
     */
    public static String getInitialUpperCase(String str) {
        if ((str != null) && (str.length() > 0)) {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str;
    }

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr()的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值
     *
     * @return ip
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        log.debug("x-forwarded-for ip:" + ip);
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            log.debug("Proxy-Client-IP ip:" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            log.debug("WL-Proxy-Client-IP ip:" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            log.debug("HTTP_CLIENT_IP ip:" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            log.debug("HTTP_X_FORWARDED_FOR ip:" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
            log.debug("X-Real-IP ip:" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            log.debug("getRemoteAddr ip:" + ip);
        }
        log.debug("获取客户端ip:" + ip);
        return ip;
    }

    /**
     * 获取随机字符串
     *
     * @param length 字符串长度
     * @return
     */
    public static String getRandomString(int length) {
        StringBuffer sb = new StringBuffer();
        SecureRandom r = new SecureRandom();
        int range = buffer.length();
        for (int i = 0; i < length; ++i) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        return sb.toString();
    }

    private static String getSplicStr(String str, String start, String end) {
        StringBuffer sb = new StringBuffer();
        if (isNotEmpty(str)) {
            String[] s = str.split(start);
            for (int i = 0; i < s.length; ++i) {
                String temp = s[i];
                if (temp.contains(end)) {
                    sb.append(temp.substring(0, temp.lastIndexOf(end)));
                }
            }
        }
        return sb.toString();
    }

    public static int getSubTime(String str1, String str2) {
        return (int) (parseLongDate(str2) - parseLongDate(str1)) / 60000;
    }

    /**
     * 获取当前时间
     *
     * @return HH:mm:ss
     */
    public static String getTime() {
        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    /**
     * 获取13位时间戳
     *
     * @return
     */
    public static String getUnixDate() {
        return Long.toString(System.currentTimeMillis());
    }

    /**
     * 获取10位时间戳
     *
     * @return
     */
    public static String getUnixDate10() {
        return Long.toString(System.currentTimeMillis()).substring(0, 10);
    }

    /**
     * 获取工程在服务器的部署路径
     *
     * @return
     */
    public static String getWebContentPath() {
        String path = StringUtils.class.getClassLoader().getResource("").getPath();
        String webContentPath = path.substring(0, path.indexOf("/WEB-INF"));
        webContentPath = URLDecoder.decode(webContentPath);
        return webContentPath;
    }

    /**
     * 是否为空字符串
     *
     * @param input
     * @return
     */
    public static boolean isEmpty(String input) {
        return (input == null) || (input.length() == 0);
    }

    /**
     * 是否为空数组
     *
     * @param input
     * @return
     */
    public static boolean isEmptyArr(String[] input) {
        return (input == null) || (input.length == 0);
    }

    public static boolean isExist(String str1, String str2) {
        boolean b = false;
        if (str2.indexOf("\"" + str1 + "\"") > -1) {
            b = true;
        }
        return b;
    }

    public static boolean isExists(String str1, String str2) {
        boolean b = false;
        try {
            if ((isNotEmpty(str1)) && (isNotEmpty(str2)) && (str2.contains(str1)))
                b = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    public static boolean isExistsArray(String str, List list) {
        try {
            if ((!isNotEmptyList(list)) || (!isNotEmpty(str)))
                return false;
            return list.contains(str);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean isLetter(String str) {
        if (isEmpty(str))
            return false;
        return str.replaceAll("\\s*", "").matches("^[a-zA-Z]*");
    }

    /**
     * 是否不为空
     *
     * @param input
     * @return
     */
    public static boolean isNotEmpty(String input) {
        return !isEmpty(input);
    }

    /**
     * 是否不为空数组
     *
     * @param input
     * @return
     */
    public static boolean isNotEmptyArray(String[] input) {
        return (input != null) && (input.length > 0);
    }

    /**
     * 是否不为空List
     *
     * @param input
     * @return
     */
    public static boolean isNotEmptyList(List<?> input) {
        return (input != null) && (input.size() > 0);
    }

    /**
     * 是否不为空Map
     *
     * @param input
     * @return
     */
    public static boolean isNotEmptyMap(Map<?, ?> input) {
        return (input != null) && (input.size() > 0);
    }

    /**
     * 是否不为空Object
     *
     * @param obj
     * @return
     */
    public static boolean isNotEmptyObject(Object obj) {
        return (obj != null) && (isNotEmpty(obj.toString()));
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static Integer listLen(List<?> list) {
        if (isNotEmptyList(list))
            return Integer.valueOf(list.size());
        return Integer.valueOf(0);
    }

    public static Integer mapLen(Map<?, ?> map) {
        if (isNotEmptyMap(map))
            return Integer.valueOf(map.size());
        return Integer.valueOf(0);
    }

    /**
     * md5加密字符串方法
     *
     * @param plainText 明文
     * @return 32位密文
     */
    public static String md5(String plainText) {
        return DigestUtils.md5Hex(plainText);
    }

    public static String noLastComma(String str) {
        if (isNotEmpty(str)) {
            if ((str.endsWith(",")) || (str.endsWith("，"))) {
                return str.substring(0, str.length() - 1);
            }
            return str;
        }
        return "";
    }

    public static String objectToJson(Object obj) {
        if (obj == null)
            return null;
        try {
            return JsonUtils.convertToString(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * String转换为int类型
     *
     * @param str
     * @return
     */
    public static int parseInt(String str) {
        if (isNotEmpty(str)) {
            return Integer.parseInt(str);
        }
        return 0;
    }

    public static long parseLongDate(String str) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str).getTime();
        } catch (ParseException e) {
        }
        return 0L;
    }

    /**
     * rc4解密
     *
     * @param val 密文
     * @param key 秘钥
     * @return
     */
    public static String RC4Decode(byte[] val, String key) {
        return RC4.decry_RC4(val, key);
    }

    /**
     * rc4加密
     *
     * @param val 明文
     * @param key 秘钥
     * @return
     */
    public static byte[] RC4Encode(String val, String key) {
        return RC4.encry_RC4_byte(val, key);
    }

    /**
     * 替换br标签为换行符（\n）
     *
     * @param s
     * @return
     * @throws NullPointerException
     */
    public static String replacebr(String s) throws NullPointerException {
        return s.replaceAll("<br>", "\n");
    }

    public static String replaceChar(String s, char c, char c1) {
        if (s == null) {
            return "";
        }
        return s.replace(c, c1);
    }

    public static String replaceEnter(String s) throws NullPointerException {
        return s.replaceAll("\n", "<br>");
    }

    public static String replaceQuote(String s) throws NullPointerException {
        return s.replaceAll("'", "''");
    }

    public static String replaceString(String s, String s1, String s2) {
        if ((s == null) || (s1 == null) || (s2 == null)) {
            return "";
        }
        return s.replaceAll(s1, s2);
    }

    public static Date string14ToDate(String input) {
        if (isEmpty(input)) {
            return null;
        }

        if (input.length() != 14) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            return dateFormat.parse(input);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * 截取字符串并已...结尾
     *
     * @param str
     * @param count
     * @return
     */
    public static String subString(String str, int count) {
        if (isEmpty(str))
            return null;
        if (str.length() > count) {
            return str.substring(0, count) + "...";
        }
        return str;
    }

    public static String subString1(String str, int count) {
        if (isEmpty(str))
            return null;
        if (str.length() > count) {
            return str.substring(0, count);
        }
        return str;
    }

    public static String subString2(String str, int count) {
        if (isEmpty(str))
            return null;
        if (str.length() > count) {
            return str.substring(0, count) + "..";
        }
        return str;
    }

    public static String toBR(String s) {
        s = replaceString(s, "\n", "<br>\n");
        s = replaceString(s, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
        s = replaceString(s, "  ", "&nbsp;&nbsp;");
        return s;
    }

    public static String toChi(String input) {
        try {
            byte[] bytes = input.getBytes("ISO8859-1");
            return new String(bytes, "GBK");
        } catch (Exception localException) {
        }
        return input;
    }

    public static String toHtml(String s) {
        s = replaceString(s, "<", "&#60;");
        s = replaceString(s, ">", "&#62;");
        return s;
    }

    public static int toInteger(Object str) {
        return Integer.parseInt(str.toString());
    }

    public static String toISO(String input) {
        return changeEncoding(input, "GBK", "ISO8859-1");
    }

    public static String toSQL(String s) {
        s = replaceString(s, "\r\n", "\n");
        return s;
    }

    /**
     * url解码
     *
     * @param value
     * @return
     */
    public static String urlDecode(String value) {
        if (isEmpty(value)) {
            return "";
        }
        try {
            return URLDecoder.decode(value, "GB2312");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return value;
    }

    /**
     * url解码
     *
     * @param value 源文本
     * @param enc   编码格式（如：utf-8，gb2312等）
     * @return
     */
    public static String urlDecode(String value, String enc) {
        if (isEmpty(value)) {
            return "";
        }
        try {
            return URLDecoder.decode(value, enc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return value;
    }

    /**
     * url编码
     *
     * @param value
     * @return
     */
    public static String urlEncode(String value) {
        if (isEmpty(value)) {
            return "";
        }
        try {
            value = URLEncoder.encode(value, "GB2312");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return value;
    }

    /**
     * url编码
     *
     * @param value
     * @param enc   编码格式（如：utf-8，gb2312等）
     * @return
     */
    public static String urlEncode(String value, String enc) {
        if (isEmpty(value)) {
            return "";
        }
        try {
            value = URLEncoder.encode(value, enc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return value;
    }

}