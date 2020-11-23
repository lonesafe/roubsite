package com.roubsite.smarty4j;

/**
 * Thrown when the smarty statement has syntax error.
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class ParseException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a {@code ParseException} with the specified detail message.
	 * 
	 * @param message
	 *          the detail message.
	 */
	public ParseException(String message) {
		super(message);
	}
}