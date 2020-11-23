package com.roubsite.smarty4j.statement;

import static org.objectweb.asm.Opcodes.*;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.Template;
import com.roubsite.smarty4j.TemplateWriter;
import com.roubsite.smarty4j.VariableManager;

/**
 * 文本输出语句，简单的将模板文件中的文本进行输出。
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class TextStatement extends Node {

	/** 需要输出的文本 */
	private String text;

	/** 字节流缓存索引号 */
	private int index;
	
	private boolean big;

	public TextStatement(Analyzer analyzer, String text) {
		if (text.length() > 0) {
			this.text = text;
			this.index = analyzer.getTextIndex(text);
			if (analyzer.getTemplate().getTextString(index) != null) {
				big = true;
			}
		}
	}

	/**
	 * 获取需要输出的文本。
	 * 
	 * @return 需要输出的文本
	 */
	public String getText() {
		return text == null ? "" : text;
	}
	
	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		if (text != null) {
			mv.visitVarInsn(ALOAD, WRITER);
			if (big) {
				mv.visitVarInsn(ALOAD, TEMPLATE);
				mv.visitLdcInsn(index);
				mv.visitMethodInsn(INVOKEVIRTUAL, Template.NAME, "getTextString", "(I)Ljava/lang/String;");
			} else {
				mv.visitLdcInsn(text);
			}
			mv.visitVarInsn(ALOAD, TEMPLATE);
			mv.visitLdcInsn(index);
			mv.visitMethodInsn(INVOKEVIRTUAL, Template.NAME, "getTextBytes", "(I)[B");
			mv.visitMethodInsn(INVOKEVIRTUAL, TemplateWriter.NAME, "write", "(Ljava/lang/String;[B)V");
		}
	}
}