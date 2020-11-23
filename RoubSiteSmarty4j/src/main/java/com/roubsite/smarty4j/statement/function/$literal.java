package com.roubsite.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.TemplateReader;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.statement.Function;

/**
 * The tag allow a block of data to be taken literally. This is typically used around Javascript or
 * stylesheet blocks where {curly braces} would interfere with the template delimiter syntax.
 * Anything within {literal}{/literal} tags is not interpreted, but displayed as-is. If you need
 * template tags embedded in a {literal} block, consider using {ldelim}{rdelim} to escape the
 * individual delimiters instead.
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $literal extends Function {

	/** 结束符 */
	private static Pattern p = Pattern.compile("\\{\\s*/\\s*literal\\s*\\}");

	/** 文本缓冲区 */
	private StringBuilder text = new StringBuilder(64);

	@Override
	public void analyzeContent(Analyzer analyzer, TemplateReader reader) {
		while (true) {
			String line;
			try {
				line = reader.readLine();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}

			if (line == null) {
				reader.addMessage(String.format(MessageFormat.IS_REQUIRED, "结束标签 \"/literal\""));
				return;
			}

			Matcher m = p.matcher(line);
			if (m.find()) {
				text.append(line.substring(0, m.start()));
				reader.unread(line.substring(m.end()));
				return;
			}

			text.append(line);
		}
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitVarInsn(ALOAD, WRITER);
		mv.visitLdcInsn(text.toString());
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "write", "(Ljava/lang/String;)V");
	}
}
