package com.roubsite.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DCMPL;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.IF_ACMPEQ;
import static org.objectweb.asm.Opcodes.IF_ICMPEQ;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.ObjectExpression;
import com.roubsite.smarty4j.expression.number.DoubleExpression;
import com.roubsite.smarty4j.expression.number.IntegerExpression;


/**
 * 全等于布尔表达式节点, 检测两个表达式的结果是否完全相等, 不使用弱类型规则
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class AEQCheck extends CheckExpression {

	private Expression exp1;
	private Expression exp2;

	/**
	 * 创建全等于布尔表达式节点
	 * 
	 * @param exp1 表达式1
	 * @param exp2 表达式2
	 */
	public AEQCheck(Expression exp1, Expression exp2) {
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue, Label lblFalse) {
		if ((exp1 instanceof IntegerExpression && exp2 instanceof IntegerExpression)
				|| (exp1 instanceof CheckExpression && exp2 instanceof CheckExpression)) {
			exp1.parse(mv, local, vm);
			exp2.parse(mv, local, vm);
			mv.visitSCJumpInsn(IF_ICMPEQ, lblTrue, lblFalse);
		} else if ((exp1 instanceof DoubleExpression) && (exp2 instanceof DoubleExpression)) {
			exp1.parse(mv, local, vm);
			exp2.parse(mv, local, vm);
			mv.visitInsn(DCMPL);
			mv.visitSCJumpInsn(IFEQ, lblTrue, lblFalse);
		} else {
			boolean exp1IsObject = exp1 instanceof ObjectExpression;
			boolean exp2IsObject = exp2 instanceof ObjectExpression;
			if (exp1IsObject || exp2IsObject) {
				// if (obj1 == obj2) {
				// return true;
				// } else if (obj1 == null) {
				// return false;
				// } else {
				// return obj1.equals(obj2);
				// }
				Label isTrue = lblTrue == null ? new Label() : lblTrue;
				Label isFalse = lblFalse == null ? new Label() : lblFalse;
				Label end = new Label();

				if (exp1IsObject && exp2IsObject) {
					exp1.parse(mv, local, vm);
					exp2.parse(mv, local, vm);
					mv.visitVarInsn(ASTORE, local + 1);
					mv.visitVarInsn(ASTORE, local);

					// 判断两个对象是否地址相同
					mv.visitVarInsn(ALOAD, local);
					mv.visitVarInsn(ALOAD, local + 1);
					mv.visitJumpInsn(IF_ACMPEQ, isTrue);

					// 判断第一个对象是否为NULL
					mv.visitVarInsn(ALOAD, local);
					mv.visitJumpInsn(IFNULL, isFalse);

					// 判断第一个对象是否相等
					mv.visitVarInsn(ALOAD, local);
					mv.visitVarInsn(ALOAD, local + 1);
				} else if (exp1IsObject) {
					exp2.parseObject(mv, local, vm);
					exp1.parse(mv, local, vm);
				} else {
					exp1.parseObject(mv, local, vm);
					exp2.parse(mv, local, vm);
				}
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z");
				mv.visitJumpInsn(GOTO, end);

				// 结果输出
				if (lblTrue == null) {
					mv.visitLabel(isTrue);
					mv.visitLdcInsn(true);
					mv.visitJumpInsn(GOTO, end);
				}
				if (lblFalse == null) {
					mv.visitLabel(isFalse);
					mv.visitLdcInsn(false);
				}

				mv.visitLabel(end);
			} else {
				if (lblFalse == null) {
					mv.visitLdcInsn(false);
				} else {
					mv.visitJumpInsn(GOTO, lblFalse);
				}
			}
		}
	}
}
