package com.roubsite.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

/**
 * 假操作布尔表达式节点, 向JVM语句栈内放入0
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class FalseCheck extends CheckExpression {

	public static final FalseCheck VALUE = new FalseCheck(); 
	
	private FalseCheck() {
	}

	@Override
	public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue, Label lblFalse) {
		if (lblFalse == null) {
			mv.visitLdcInsn(false);
		} else {
			mv.visitJumpInsn(GOTO, lblFalse);
		}
	}

	@Override
	public void parseInteger(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitLdcInsn(0);
	}

	@Override
	public void parseDouble(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitInsn(DCONST_0);
	}

	@Override
	public void parseString(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitLdcInsn(Boolean.toString(false));
	}

	@Override
	public void parseObject(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/Boolean", "FALSE", "Ljava/lang/Boolean;");
	}
}