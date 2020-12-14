package com.roubsite.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class FileUtils {
	public static void main(String[] args) {
		new FileUtils().filePutContent("d:/fefe/uu/k.txt", "asdfadsf", false);
	}

	public String fileGetContent(String fileName) {
		String encoding = "UTF-8";
		byte[] filecontent = getContentByte(fileName, encoding);
		try {
			return new String(filecontent, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String fileGetContent(String fileName, String encoding) {
		byte[] filecontent = getContentByte(fileName, encoding);
		try {
			return new String(filecontent, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] fileGetContentByte(String fileName) {
		String encoding = "UTF-8";
		return getContentByte(fileName, encoding);
	}

	public byte[] fileGetContentByte(String fileName, String encoding) {
		return getContentByte(fileName, encoding);
	}

	public boolean filePutContent(String fileName, String data, boolean append) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileWriter fileWritter = new FileWriter(fileName, append);
			fileWritter.write(data);
			fileWritter.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean filePutContent(String fileName, char[] data, boolean append) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileWriter fileWritter = new FileWriter(fileName, append);
			fileWritter.write(data);
			fileWritter.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private byte[] getContentByte(String fileName, String encoding) {
		File file = new File(fileName);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
			return filecontent;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
