package com.roubsite.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import com.roubsite.smarty4j.Engine;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.statement.Function;

/**
 * The tag is used for escaping template left-delimiters.
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $ldelim extends Function {

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitVarInsn(ALOAD, WRITER);
		mv.visitVarInsn(ALOAD, ENGINE);
		mv.visitMethodInsn(INVOKEVIRTUAL, Engine.NAME, "getLeftDelimiter", "()Ljava/lang/String;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "write", "(Ljava/lang/String;)V");
	}
}