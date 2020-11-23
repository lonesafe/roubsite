package com.roubsite.smarty4j.statement;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.VariableManager;

/**
 * 调试语句，记录当前处理的行号，在解析错误时，JVM将提示出错的行号信息，
 * 是否开启这个功能受模板控制器的调试开关影响。
 * 
 * @see com.roubsite.smarty4j.Engine#setDebug(boolean)
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class DebugStatement extends Node {

	/** 当前的行号 */
	private int lineNumber;

	/**
	 * 创建调试语句
	 * 
	 * @param lineNumber
	 *          当前的行号
	 */
	public DebugStatement(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		Label line = new Label();
		mv.visitLabel(line);
		mv.visitLineNumber(lineNumber, line);
	}
}
