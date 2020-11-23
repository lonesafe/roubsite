package com.roubsite.smarty4j.expression.number;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

/**
 * 整数表达式节点, 向JVM语句栈内放入一个整数值
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public abstract class IntegerExpression extends NumberExpression {

  @Override
	public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue, Label lblFalse) {
		parseInteger(mv, local, vm);
		mv.visitSCJumpInsn(IFNE, lblTrue, lblFalse);
	}

  @Override
	public void parseDouble(MethodVisitorProxy mv, int local, VariableManager vm) {
  	parseInteger(mv, local, vm);
		mv.visitInsn(I2D);
	}

  @Override
	public void parseString(MethodVisitorProxy mv, int local, VariableManager vm) {
  	parseInteger(mv, local, vm);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "toString", "(I)Ljava/lang/String;");
	}

  @Override
	public void parseObject(MethodVisitorProxy mv, int local, VariableManager vm) {
  	parseInteger(mv, local, vm);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
	}


  @Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
  	parseInteger(mv, local, vm);
  }
}
