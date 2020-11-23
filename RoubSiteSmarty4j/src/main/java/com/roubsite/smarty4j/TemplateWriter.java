package com.roubsite.smarty4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import com.roubsite.smarty4j.util.SimpleOutputStreamWriter;
import com.roubsite.smarty4j.util.SimpleStringWriter;

/**
 * , it 能够选择被代理的Writer或OutputStream输出
 * 模板默认的二进制/文本转换输出类，在模板解析时，如果用户传入的是二进制输出流，
 * 将自动建立这个类，这个类可以方便的在二进制/文本输出之间转换。
 * 
 * @see com.roubsite.smarty4j.Template
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class TemplateWriter extends Writer {

	/** ASM名称 */
	public static final String NAME = TemplateWriter.class.getName().replace('.', '/');

	/** 书写器 */
	private Writer writer;

	/** 二进制输出流 */
	private OutputStream out;

	/** nio字节流写通道 */
	private WritableByteChannel ch;

	/**
	 * 获取临时的输出器
	 * 
	 * @return 临时的输出器
	 */
	public static TemplateWriter getTemporaryWriter() {
		return new TemplateWriter(new SimpleStringWriter());
	}

	/**
	 * 创建模板输出器
	 * 
	 * @param ch
	 *          nio字节流写通道
	 * @param encoding
	 *          编码集
	 * @throws IOException
	 *           构造对象时产生IO错误
	 */
	public TemplateWriter(WritableByteChannel ch, Charset charset) throws IOException {
		this.writer = SimpleOutputStreamWriter.forWritableByteChannel(ch, charset);
		this.ch = ch;
	}

	/**
	 * 创建模板输出器
	 * 
	 * @param out
	 *          二进制输出流
	 * @param encoding
	 *          编码集
	 * @throws IOException
	 *           构造对象时产生IO错误
	 */
	public TemplateWriter(OutputStream out, Charset charset) throws IOException {
		this.writer = SimpleOutputStreamWriter.forOutputStream(out, charset);
		this.out = out;
	}

	/**
	 * 创建模板输出器
	 * 
	 * @param writer
	 *          书写器
	 */
	public TemplateWriter(Writer writer) {
		this.writer = writer;
	}

	/**
	 * 输出数据，s与b的值必须等价
	 * 
	 * @param s
	 *          需要输出的文本
	 * @param b
	 *          需要输出的二进制流
	 * @throws IOException
	 */
	public void write(String s, byte[] b) throws IOException {
		if (ch != null) {
			if (ch.write(ByteBuffer.wrap(b)) != b.length) {
				assert false : b.length;
			}
		} else if (out != null) {
			out.write(b);
		} else {
			writer.write(s);
		}
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		writer.write(cbuf, off, len);
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	@Override
	public String toString() {
		return writer.toString();
	}
}
