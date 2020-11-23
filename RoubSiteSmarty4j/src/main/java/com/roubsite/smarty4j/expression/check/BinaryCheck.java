package com.roubsite.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.ObjectExpression;
import com.roubsite.smarty4j.expression.number.NumberExpression;

/**
 * 二元弱类型布尔表达式节点, 向JVM语句栈内放入整数值表示两个对象的弱类型逻辑操作结果
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class BinaryCheck extends CheckExpression {

	private int opcode;
	private Expression exp1;
	private Expression exp2;

	public BinaryCheck(int opcode, Expression exp1, Expression exp2) {
		this.opcode = opcode;
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	/**
	 * 对数值进行二元逻辑操作
	 * 
	 * @param mv
	 *          ASM方法操作者
	 * @param swap
	 *          true表示交换了两个操作数在栈中的顺序
	 */
	private void checkDouble(MethodVisitorProxy mv, Label lblTrue, Label lblFalse) {
		mv.visitInsn(DCMPL);
		mv.visitSCJumpInsn(opcode, lblTrue, lblFalse);
	}

	/**
	 * 对字符串进行二元逻辑操作
	 * 
	 * @param mv
	 *          ASM方法操作者
	 * @param swap
	 *          true表示交换了两个操作数在栈中的顺序
	 */
	private void checkString(MethodVisitorProxy mv, Label lblTrue, Label lblFalse) {
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "compareTo", "(Ljava/lang/String;)I");
		mv.visitSCJumpInsn(opcode, lblTrue, lblFalse);
	}

	@Override
	public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue,
	    Label lblFalse) {
		// 在处理过程中, 如果对象的内容不是字符串, 需要先针对变量进行弱类型转换,
		// 只有转换不成功的时候才执行字符串比较
		boolean exp1IsNumber = exp1 instanceof NumberExpression;
		boolean exp2IsNumber = exp2 instanceof NumberExpression;
		if (exp1IsNumber && exp2IsNumber) {
			exp1.parseDouble(mv, local, vm);
			exp2.parseDouble(mv, local, vm);
			checkDouble(mv, lblTrue, lblFalse);
		} else if (exp1IsNumber) {
			Label isString = new Label();
			Label end = new Label();

			exp2.parseObject(mv, local, vm);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ASTORE, local);

			mv.visitMethodInsn(INVOKESTATIC, ObjectExpression.NAME, "weak", "(Ljava/lang/Object;)D");
			mv.visitInsn(DUP2);
			mv.visitVarInsn(DSTORE, local + 1);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "isNaN", "(D)Z");
			mv.visitJumpInsn(IFNE, isString);

			exp1.parseDouble(mv, local, vm);
			mv.visitVarInsn(DLOAD, local + 1);
			checkDouble(mv, lblTrue, lblFalse);
			mv.visitJumpInsn(GOTO, end);

			mv.visitLabel(isString);
			exp1.parseString(mv, local, vm);
			mv.visitVarInsn(ALOAD, local);
			mv.visitMethodInsn(INVOKESTATIC, ObjectExpression.NAME, "o2s",
			    "(Ljava/lang/Object;)Ljava/lang/String;");
			checkString(mv, lblTrue, lblFalse);

			mv.visitLabel(end);
		} else if (exp2IsNumber) {
			Label isString = new Label();
			Label end = new Label();

			exp1.parseObject(mv, local, vm);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ASTORE, local);

			mv.visitMethodInsn(INVOKESTATIC, ObjectExpression.NAME, "weak", "(Ljava/lang/Object;)D");
			mv.visitInsn(DUP2);
			mv.visitVarInsn(DSTORE, local + 1);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "isNaN", "(D)Z");
			mv.visitJumpInsn(IFNE, isString);

			mv.visitVarInsn(DLOAD, local + 1);
			exp2.parseDouble(mv, local, vm);
			checkDouble(mv, lblTrue, lblFalse);
			mv.visitJumpInsn(GOTO, end);

			mv.visitLabel(isString);
			mv.visitVarInsn(ALOAD, local);
			mv.visitMethodInsn(INVOKESTATIC, ObjectExpression.NAME, "o2s",
			    "(Ljava/lang/Object;)Ljava/lang/String;");
			exp2.parseString(mv, local, vm);
			checkString(mv, lblTrue, lblFalse);

			mv.visitLabel(end);
		} else {
			Label nonString = new Label();
			Label isString = new Label();
			Label end = new Label();

			exp1.parseObject(mv, local, vm);
			mv.visitVarInsn(ASTORE, local);
			exp2.parseObject(mv, local, vm);
			mv.visitVarInsn(ASTORE, local + 1);

			mv.visitVarInsn(ALOAD, local);
			mv.visitTypeInsn(INSTANCEOF, "java/lang/String");
			mv.visitJumpInsn(IFEQ, nonString);
			mv.visitVarInsn(ALOAD, local + 1);
			mv.visitTypeInsn(INSTANCEOF, "java/lang/String");
			mv.visitJumpInsn(IFNE, isString);

			mv.visitLabel(nonString);
			mv.visitVarInsn(ALOAD, local);
			mv.visitMethodInsn(INVOKESTATIC, ObjectExpression.NAME, "weak", "(Ljava/lang/Object;)D");
			mv.visitInsn(DUP2);
			mv.visitVarInsn(DSTORE, local + 2);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "isNaN", "(D)Z");
			mv.visitJumpInsn(IFNE, isString);

			mv.visitVarInsn(ALOAD, local + 1);
			mv.visitMethodInsn(INVOKESTATIC, ObjectExpression.NAME, "weak", "(Ljava/lang/Object;)D");
			mv.visitInsn(DUP2);
			mv.visitVarInsn(DSTORE, local + 4);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "isNaN", "(D)Z");
			mv.visitJumpInsn(IFNE, isString);

			mv.visitVarInsn(DLOAD, local + 2);
			mv.visitVarInsn(DLOAD, local + 4);
			checkDouble(mv, lblTrue, lblFalse);
			mv.visitJumpInsn(GOTO, end);

			mv.visitLabel(isString);
			mv.visitVarInsn(ALOAD, local);
			mv.visitMethodInsn(INVOKESTATIC, ObjectExpression.NAME, "o2s",
			    "(Ljava/lang/Object;)Ljava/lang/String;");
			mv.visitVarInsn(ALOAD, local + 1);
			mv.visitMethodInsn(INVOKESTATIC, ObjectExpression.NAME, "o2s",
			    "(Ljava/lang/Object;)Ljava/lang/String;");
			checkString(mv, lblTrue, lblFalse);

			mv.visitLabel(end);
		}
	}
}