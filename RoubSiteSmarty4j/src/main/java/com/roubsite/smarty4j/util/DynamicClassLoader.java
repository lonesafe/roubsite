package com.roubsite.smarty4j.util;

/**
 * The class loader is an object that is responsible for loading the ASM dynamically generated
 * class.
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class DynamicClassLoader extends ClassLoader {

	private static ClassLoader systemLoader = DynamicClassLoader.class.getClassLoader();

	/**
	 * Converts an array of bytes into an instance of class <tt>Class</tt>.
	 * 
	 * @param code
	 *          the bytes that make up the class data
	 * @return the <tt>Class</tt> object created from the data
	 */
	public static Class<?> defineClass(byte[] code) {
		return defineClass("anonymous", code);
	}

	/**
	 * Converts an array of bytes into an instance of class <tt>Class</tt>.
	 * 
	 * @param name
	 *          the expected binary name of the class
	 * @param code
	 *          the bytes that make up the class data
	 * @return the <tt>Class</tt> object created from the data
	 */
	public synchronized static Class<?> defineClass(String name, byte[] code) {
		DynamicClassLoader loader = new DynamicClassLoader();
		return loader.defineClass(name, code, 0, code.length);
	}

	private DynamicClassLoader() {
		super(systemLoader);
	}
}