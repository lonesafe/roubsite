package com.roubsite.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class GZipUtils {

	/**
	 * 压缩 注：压缩后得到的byte[]数组不可直接转为String，否则将无法解压
	 */
	public static byte[] gZip(String s) {
		if (s == null || "".equals(s.trim()))
			return null;
		try {
			byte[] b = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(bos);
			gzip.write(s.getBytes());
			gzip.finish();
			gzip.close();
			b = bos.toByteArray();
			bos.close();
			return b;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 解压
	 * 
	 * @param data
	 * @return
	 */
	public static String unGZip(byte[] data) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			GZIPInputStream gzip = new GZIPInputStream(bis);
			byte[] buf = new byte[1024];
			int num = -1;
			while ((num = gzip.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, num);
			}
			baos.flush();
			baos.close();
			gzip.close();
			bis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new String(baos.toByteArray());
	}

	/**
	 * 用BASE64编码
	 */
	public static String base64Encoder(byte[] b) {
		if (b == null)
			return null;
		return Base64.getEncoder().encodeToString(b);
	}

	/**
	 * 用BASE64解码
	 */
	public static byte[] base64Decoder(String s) throws Exception {
		if (s == null)
			return null;
		return Base64.getDecoder().decode(s);
	}

	public static void main(String[] args) throws Exception {
		String str =  " implements ResponseWrapperInterface{@Override public void preseInterface(HttpServletResponse response){response.setHeader(\"roubsite_version\",RoubSiteLicense.getVersion());response.setHeader(\"roubsite_license\",RoubSiteLicense.getLicense());response.addCookie(new Cookie(\"roubsite_version\",RoubSiteLicense.getVersion()));response.addCookie(new Cookie(\"roubsite_license\",RoubSiteLicense.getLicense()));}}";
		System.out.println("压缩前：" + str);
		System.out.println("压缩前字节长度：" + str.getBytes().length);
		byte[] gByte = gZip(str);
		System.out.println("压缩后字节长度：" + gByte.length);
		String base64GStr = base64Encoder(gByte);
		System.out.println("压缩加密：" + base64GStr);
		System.out.println("压缩加密后字节长度：" + base64GStr.getBytes().length);
		byte[] gStrGByte = base64Decoder(base64GStr);
		String decompString = unGZip(gStrGByte);
		System.out.println("解缩解密：" + decompString);

	}
}
