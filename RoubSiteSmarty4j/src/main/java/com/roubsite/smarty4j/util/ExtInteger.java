package com.roubsite.smarty4j.util;

/**
 * The {@code IntNumber} class wraps a value of the primitive type {@code int} or {@code boolean} in
 * an object, and Integer/Boolean different is that it can modify the value of the packaged
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class ExtInteger extends Number {

	private static final long serialVersionUID = 1L;

	private int value;

	/**
	 * Constructs a newly allocated {@code IntNumber} object.
	 */
	public ExtInteger() {
	}

	/**
	 * Constructs a newly allocated {@code IntNumber} object that represents the specified {@code int}
	 * value.
	 *
	 * @param value
	 *          the value to be represented by the {@code IntNumber} object.
	 */
	public ExtInteger(int value) {
		set(value);
	}

	/**
	 * Set the value to the {@code IntNumber} object.
	 * 
	 * @param value
	 *          the value to be represented by the {@code IntNumber} object.
	 */
	public void set(int value) {
		this.value = value;
	}

	@Override
	public double doubleValue() {
		return value;
	}

	@Override
	public float floatValue() {
		return value;
	}

	@Override
	public int intValue() {
		return value;
	}

	@Override
	public long longValue() {
		return value;
	}
}
