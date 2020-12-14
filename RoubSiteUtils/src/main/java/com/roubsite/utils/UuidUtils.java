package com.roubsite.utils;

public class UuidUtils {
	public static String getUuid() {
		UUIDGenerator uid = new UUIDGenerator();
		return StringUtils.md5(uid.getNextSeqId(32).toString());
	}
}
