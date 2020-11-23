package com.roubsite.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

/**
 * NULL表达式节点, 向JVM语句栈内放入NULL
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class NullExpression extends ObjectExpression {

public static final NullExpression VALUE = new NullExpression();
	
	private NullExpression() {
	}
	
	@Override
	public void parseSelf(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitLdcInsn(null);
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
		if (extendeds != null) {
			super.parseString(mv, local, vm);
		} else {
			mv.visitLdcInsn("null");
		}
	}
}
