package com.roubsite.utils;

import java.io.Serializable;
import java.net.InetAddress;

public class UUIDGenerator {
	private String sep = "";
	private int length = 32;
	private static final int IP;

	static {
		int ipadd;
		try {
			ipadd = BytesHelper.toInt(InetAddress.getLocalHost().getAddress());
		} catch (Exception e) {
			ipadd = 0;
		}
		IP = ipadd;
	}

	private static short counter = 0;
	private static final int JVM = (int) (System.currentTimeMillis() >>> 8);
	private static int mycount = 0;

	public int getLength() {
		return this.length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getNextSeqId() {
		setLength(32);
		return 36 + format(getIP()) + this.sep + format(getJVM()) + this.sep + format(getHiTime()) + this.sep
				+ format(getLoTime()) + this.sep + format(getCount());
	}

	public Serializable getNextSeqId(int length) {
		setLength(length);
		mycount += 1;
		StringBuffer sb = new StringBuffer(36);
		sb.append(format(getIP())).append(this.sep).append(format(getJVM())).append(this.sep)
				.append(format(getHiTime())).append(this.sep).append(format(getLoTime())).append(this.sep)
				.append(format(getCount())).toString();

		return processLength(sb);
	}

	protected int getJVM() {
		return JVM;
	}

	protected short getCount() {
		synchronized (UUIDGenerator.class) {
			if (counter < 0)
				counter = 0;
			return counter++;
		}
	}

	protected int getIP() {
		return IP;
	}

	protected short getHiTime() {
		return (short) (int) (System.currentTimeMillis() >>> 32);
	}

	protected int getLoTime() {
		return (int) System.currentTimeMillis();
	}

	protected String format(int intval) {
		String formatted = Integer.toHexString(intval);
		StringBuffer buf = new StringBuffer("00000000");
		buf.replace(8 - formatted.length(), 8, formatted);
		return buf.toString();
	}

	protected String format(short shortval) {
		String formatted = Integer.toHexString(shortval);
		StringBuffer buf = new StringBuffer("0000");
		buf.replace(4 - formatted.length(), 4, formatted);
		return buf.toString();
	}

	private String processLength(StringBuffer sb) {
		if (getLength() != 32) {
			int offset = 32 - getLength();
			if (offset > 0) {
				sb.delete(0, offset);
			} else {
				sb.insert(0, this.sep);
				sb.insert(0, random(offset));
			}
		}
		return sb.toString();
	}

	private String random(int offset) {
		long curr = System.currentTimeMillis();
		String formatted = Long.toHexString(mycount + curr);
		StringBuffer buf = new StringBuffer();
		for (int i = offset; i < 0; i++) {
			buf.append("0");
		}
		if (-offset < 8) {
			buf.replace(0, -offset, formatted.substring(formatted.length() + offset));
		} else if (-offset > 16) {
			buf.replace(0, 8, formatted.substring(formatted.length() - 8));
			formatted = Long.toHexString(curr + mycount + getCount());
			buf.replace(8, 16, formatted.substring(formatted.length() - 8));
		} else {
			buf.replace(0, 8, formatted.substring(formatted.length() - 8));
			formatted = Long.toHexString(curr + mycount + getCount() + curr / 10000L);

			buf.replace(8, -offset, formatted.substring(formatted.length() + offset + 8));
		}

		return buf.toString();
	}

	public UUIDGenerator() {
	}
}