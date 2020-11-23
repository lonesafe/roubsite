package com.roubsite.smarty4j.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/**
 * 没有缓存的OutputStreamWriter类，在模板中因为大量的IO操作是在String与array of byte之间切换，缓存没有实际意义。
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class SimpleOutputStreamWriter extends Writer {

	private SimpleEncoder encoder;
	private OutputStream out;
	private WritableByteChannel ch;

	public static SimpleOutputStreamWriter forOutputStream(OutputStream out, Charset charset) {
		return new SimpleOutputStreamWriter(out, SimpleEncoder.forCharset(charset));
	}

	public static SimpleOutputStreamWriter forWritableByteChannel(WritableByteChannel ch, Charset charset) {
		return new SimpleOutputStreamWriter(ch, SimpleEncoder.forCharset(charset));
	}

	private SimpleOutputStreamWriter(OutputStream out, SimpleEncoder enc) {
		this.out = out;
		this.encoder = enc;
	}

	private SimpleOutputStreamWriter(WritableByteChannel ch, SimpleEncoder enc) {
		this.ch = ch;
		this.encoder = enc;
	}

	@Override
	public void write(char cbuf[], int off, int len) throws IOException {
		CharBuffer cb = CharBuffer.wrap(cbuf, off, len);
		ByteBuffer bb = ByteBuffer.allocate(len * 4);
		encoder.encode(cb, bb);
		bb.flip();
		int rem = bb.limit();
		if (ch != null) {
			if (ch.write(bb) != rem) {
				assert false : rem;
			}
		} else {
			out.write(bb.array(), 0, rem);
		}
	}

	@Override
	public void flush() throws IOException {
		if (out != null) {
			out.flush();
		}
	}

	@Override
	public void close() throws IOException {
		if (ch != null) {
			ch.close();
		} else {
			out.close();
		}
	}
}