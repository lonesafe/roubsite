package com.roubsite.smarty4j;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.roubsite.smarty4j.ParseMessage.Level;
import com.roubsite.smarty4j.util.SimpleStack;

/**
 * 模板的文本输入对象，与java.io.LineNumberReader不同，它读取单行时，
 * 会把换行的控制字符如\n,\r等同样输出，为了方便模板的解析，支持单行的回滚， 与java.io.PushbackReader一样能够将曾经取出的行，
 * 使用回滚的方式将数据重新放回缓冲区中。
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class TemplateReader extends Reader {
	Logger log = Logger.getLogger(TemplateReader.class);
	/** 文本输入流数据来源 */
	private Reader in;

	/** 文本缓冲区 */
	private char cb[] = new char[8192];

	/** 文本缓冲区全部的数据量 */
	private int nChars;

	/** 文本缓冲区有效的数据起始位置 */
	private int nextChar;

	/** 当前的行号 */
	private int lineNumber;

	/** 换行标志 */
	private boolean newline;

	/** 当前处理的字符串相对于整个文档的开始位置 */
	private int start;

	/** 当前处理的字符串相对于整个文档的结束位置 */
	private int end;

	/** 当前文档最严重的信息等级 */
	private Level level = Level.NORMAL;

	/** 信息列表 */
	private List<ParseMessage> messages = new ArrayList<ParseMessage>();

	/** 被回滚的所有行 */
	private SimpleStack lines = new SimpleStack();

	/** 插入的高优先级文本输入对象 */
	private TemplateReader priorIn;

	/** 插入的高优先级文本输入对象名称，用于提示信息显示 */
	private String priorInName;

	/**
	 * 建立缓冲文本输入对象。
	 * 
	 * @param in 文本输入对象
	 */
	public TemplateReader(Reader in) {
		this.in = in;
	}

	/**
	 * 获取文档当前行号。
	 * 
	 * @return 当前行号
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * 如果当前读入的行是一个新的行返回<tt>true</tt>。
	 * 
	 * @return <tt>true</tt>表示当前读入的行是一个新的行; <tt>false</tt>表示当前读入的行是回滚产生的行
	 */
	public boolean isNewline() {
		return newline;
	}

	/**
	 * 插入一个高优先级的输入对象。
	 * 
	 * @param name 输入对象名称
	 * @param in   文本输入对象
	 */
	public void insertReader(String name, TemplateReader reader) {
		if (priorIn != null) {
			// 优先处理被插入的高优先级输入对象
			priorIn.insertReader(name, reader);
		} else {
			priorInName = name;
			priorIn = reader;
		}
	}

	/**
	 * 设置新语句区间相对于上个语句区间的信息。
	 * 
	 * @param offset 新语句区间相对于文本行的偏移量
	 */
	public void move(int offset) {
		if (priorIn != null) {
			// 优先处理被插入的高优先级输入对象
			priorIn.move(offset);
		} else {
			start += offset;
		}
	}

	/**
	 * 回滚一行，将未处理完的一行重新放入缓存，下次readLine时，将被优先获取。
	 * 
	 * @param line 回滚的文本行
	 */
	public void unread(String line) {
		if (priorIn != null) {
			// 优先处理被插入的高优先级输入对象
			priorIn.unread(line);
		} else {
			int length = line.length();
			if (length > 0) {
				end -= length;
				lines.push(line);
			}
		}
	}

	/**
	 * 检测模板解析状态。
	 * 
	 * @param name 模板名称
	 * @throws TemplateException 如果解析过程发生错误
	 */
	public void checkStatus(String name) throws TemplateException {
		if (level != Level.NORMAL) {
			throw new TemplateException("Syntax error on " + name, messages);
		}
	}

	/**
	 * 增加解析中产生的信息。
	 * 
	 * @param e 解析中的语法异常
	 */
	public void addMessage(ParseException e) {
		addMessage(e.getMessage());
	}

	/**
	 * 增加解析中产生的信息。
	 * 
	 * @param text 信息文本
	 */
	public void addMessage(String text) {
		addMessage(lineNumber, text);
	}

	/**
	 * 增加解析中产生的信息。
	 * 
	 * @param lineNumber 行号
	 * @param text       信息文本
	 */
	public void addMessage(int lineNumber, String text) {
		addMessage(Level.ERROR, text, lineNumber, start, end);
	}

	/**
	 * 增加解析中产生的信息。
	 * 
	 * @param level      信息等级
	 * @param lineNumber 行号
	 * @param start      信息标识区间的开始位置
	 * @param end        信息标识区间的结束位置
	 * @param text       信息文本
	 */
	public void addMessage(Level level, String text, int lineNumber, int start, int end) {
		if (level == Level.ERROR && this.level != Level.ERROR) {
			this.level = level;
		} else if (level == Level.WARNNING && this.level == Level.NORMAL) {
			this.level = level;
		}
		messages.add(new ParseMessage(level,
				priorInName == null ? text : text + "(" + priorInName + ":" + priorIn.getLineNumber() + ")", lineNumber,
				start, end));
	}

	/**
	 * 读取一行文本，文本以\n或者文本输入流结束位置作为换行符。
	 * 
	 * @return 文本行，包含行控制符，如果文本输入流结束，则返回NULL
	 * @throws IOException 数据读取产生异常
	 */
	public String readLine() throws IOException {
		String line;
		// 有插入的高优先级输入对象, 先处理高优先级的输入对象
		if (priorIn != null) {
			line = priorIn.readLine();
			if (line != null) {
				return line;
			}
			priorIn.close();
			priorInName = null;
			priorIn = null;
		}
		if (lines.size() > 0) {
			// 先处理被回滚的行
			newline = false;
			line = (String) lines.pop();
		} else {

			if (nextChar >= nChars) {
				fill();
				if (nextChar >= nChars) {
					return null;
				}
			}
			newline = true;
			lineNumber++;
			StringBuilder s = new StringBuilder(256);
			int startChar = nextChar;
			while (true) {
				if (nextChar >= nChars) {
					// 缓冲区字符已经读完, 加载新的内容进行缓冲区
					s.append(cb, startChar, nChars - startChar);
					fill();
					if (nextChar >= nChars) {
						break;
					}
					startChar = nextChar;
				}
				if (cb[nextChar] == '\n') {
					// 读到一行结束, 保存行结束标志并返回
					nextChar++;
					s.append(cb, startChar, nextChar - startChar);
					break;
				}
				nextChar++;
			}
			line = s.toString();
		}
		start = end;
		end += line.length();
		return line;
	}

	@Override
	public int read() throws IOException {
		if (nextChar >= nChars) {
			fill();
			if (nextChar >= nChars) {
				return -1;
			}
		}
		return cb[nextChar++];
	}

	@Override
	public boolean ready() throws IOException {
		if (nextChar >= nChars) {
			fill();
		}
		return nextChar < nChars;
	}

	@Override
	public long skip(long n) throws IOException {
		long r = n;
		while (true) {
			if (nChars - nextChar >= r) {
				nextChar += r;
				break;
			} else {
				r -= nChars - nextChar;
				nextChar = nChars;
				fill();
				if (nextChar >= nChars) {
					break;
				}
			}
		}
		return n - r;
	}

	@Override
	public void close() {
		if (priorIn != null) {
			priorIn.close();
			priorInName = null;
			priorIn = null;
		}
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
			}
			in = null;
		}
	}

	@Override
	public int read(char cbuf[], int off, int len) throws IOException {
		int r = len;
		while (r > 0) {
			if (nextChar >= nChars) {
				fill();
				if (nextChar >= nChars) {
					return -1;
				}
			}
			int n = Math.min(r, nChars - nextChar);
			System.arraycopy(cb, nextChar, cbuf, off, n);
			nextChar += n;
			off += n;
			r -= n;
		}
		return len - r;
	}

	/**
	 * 向文本缓冲区内填充数据。
	 * 
	 * @throws IOException 数据读取产生异常
	 */
	private void fill() throws IOException {
		int len = nChars - nextChar;
		System.arraycopy(cb, nextChar, cb, 0, len);
		int n;
		do {
			n = in.read(cb, len, cb.length - len);
		} while (n == 0);
		if (n > 0) {
			nextChar = 0;
			nChars = len + n;
		}
	}
}
