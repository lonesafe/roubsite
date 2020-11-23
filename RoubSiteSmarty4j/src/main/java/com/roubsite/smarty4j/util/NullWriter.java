package com.roubsite.smarty4j.util;

import java.io.Writer;

/**
 * Acting on the block function does not want the output of intermediate results.
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class NullWriter extends Writer {

	@Override
	public void close() {
	}

	@Override
	public void flush() {
	}

	@Override
	public void write(char cbuf[], int off, int len) {
	}
}
