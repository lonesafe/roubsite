package com.roubsite.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

/**
 * 真操作布尔表达式节点, 向JVM语句栈内放入1
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class TrueCheck extends CheckExpression {

	public static final TrueCheck VALUE = new TrueCheck(); 
	
	private TrueCheck() {
	}

	@Override
	public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue, Label lblFalse) {
		if (lblTrue == null) {
			mv.visitLdcInsn(true);
		} else {
			mv.visitJumpInsn(GOTO, lblTrue);
		}
	}

	@Override
	public void parseInteger(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitLdcInsn(1);
	}

	@Override
	public void parseDouble(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitInsn(DCONST_1);
	}

	@Override
	public void parseString(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitLdcInsn(Boolean.toString(true));
	}

	@Override
	public void parseObject(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitFieldInsn(GETSTATIC, "java/lang/Boolean", "TRUE", "Ljava/lang/Boolean;");
	}
}