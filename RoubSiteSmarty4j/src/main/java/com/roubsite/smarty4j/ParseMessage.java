package com.roubsite.smarty4j;

/**
 * The message in parsing.
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class ParseMessage {

	/**
	 * The message level.
	 */
	public static enum Level {
		NORMAL, WARNNING, ERROR
	};

	private Level level;
	private String msg;
	private int lineNo;
	private int start;
	private int end;

	/**
	 * Constructs a message.
	 * 
	 * @param level
	 *          message level
	 * @param msg
	 *          message text
	 * @param lineNo
	 *          the line number when message is created
	 * @param start
	 *          the start position when message is created(relative to the entire document)
	 * @param end
	 *          the end position when message is created(relative to the entire document)
	 */
	public ParseMessage(Level level, String msg, int lineNo, int start, int end) {
		this.level = level;
		this.msg = msg;
		this.lineNo = lineNo;
		this.start = start;
		this.end = end;
	}

	/**
	 * Returns message level.
	 * 
	 * @return message level
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * Returns message text.
	 * 
	 * @return message text
	 */
	public String getMessage() {
		return msg;
	}

	/**
	 * Returns the line number when message is created.
	 * 
	 * @return the line number when message is created
	 */
	public int getLineNumber() {
		return lineNo;
	}

	/**
	 * Returns the start position when message is created(relative to the entire document).
	 * 
	 * @return the start position when message is created
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Returns the end position when message is created(relative to the entire document).
	 * 
	 * @return the end position when message is created
	 */
	public int getEnd() {
		return end;
	}
}