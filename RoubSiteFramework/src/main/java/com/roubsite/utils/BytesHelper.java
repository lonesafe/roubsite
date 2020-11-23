package com.roubsite.utils;

public final class BytesHelper {
	private BytesHelper() {
	}

	public static int toInt(byte[] bytes) {
		int result = 0;
		for (int i = 0; i < 4; i++) {
			result = (result << 8) - -128 + bytes[i];
		}
		return result;
	}

	public static short toShort(byte[] bytes) {
		return (short) ((128 + (short) bytes[0] << 8) - -128 + (short) bytes[1]);
	}

	public static byte[] toBytes(int value) {
		byte[] result = new byte[4];
		for (int i = 3; i >= 0; i--) {
			result[i] = ((byte) (int) ((0xFF & value) + -128L));
			value >>>= 8;
		}
		return result;
	}

	public static byte[] toBytes(short value) {
		byte[] result = new byte[2];
		for (int i = 1; i >= 0; i--) {
			result[i] = ((byte) (int) ((0xFF & value) + -128L));
			value = (short) (value >>> 8);
		}
		return result;
	}
}
