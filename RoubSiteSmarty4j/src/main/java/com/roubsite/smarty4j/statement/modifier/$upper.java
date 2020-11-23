package com.roubsite.smarty4j.statement.modifier;

import static org.objectweb.asm.Opcodes.*;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.statement.Modifier;

/**
 * This is used to uppercase a variable.
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "If Strike isn't Settled Quickly it may Last a While.");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle}<br>
 * {$articleTitle|upper}<br>
 * <br>
 * <b>Output:</b><br>
 * If Strike isn't Settled Quickly it may Last a While.<br>
 * IF STRIKE ISN'T SETTLED QUICKLY IT MAY LAST A WHILE.<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $upper extends Modifier {

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toUpperCase", "()Ljava/lang/String;");
	}
}