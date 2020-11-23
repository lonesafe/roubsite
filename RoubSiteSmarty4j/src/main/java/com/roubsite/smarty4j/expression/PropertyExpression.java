package com.roubsite.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Field;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

/**
 * 对象转换节点, 将表达式转换成对象表达式
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class PropertyExpression extends ObjectExpression {

	private String name;
	private String key;
	private Class<?> clazz;

	public PropertyExpression(Analyzer analyzer, String name, String key, Class<?> clazz) {
		this.name = name;
		this.key = key;
		this.clazz = clazz;
		analyzer.getVariableManager().addUsedVariable(name);
	}

	@Override
	public void parseSelf(MethodVisitorProxy mv, int local, VariableManager vm) {
		try {
			mv.visitVarInsn(ALOAD, vm.getIndex(name));

			Field field = clazz.getDeclaredField(key);
			Class<?> type = field.getType();
			if (type == int.class) {
				mv.visitFieldInsn(GETFIELD, clazz.getName().replace('.', '/'), key, "I");
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
			} else if (type == long.class) {
				mv.visitFieldInsn(GETFIELD, clazz.getName().replace('.', '/'), key, "J");
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
			} else if (type == short.class) {
				mv.visitFieldInsn(GETFIELD, clazz.getName().replace('.', '/'), key, "S");
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
			} else if (type == byte.class) {
				mv.visitFieldInsn(GETFIELD, clazz.getName().replace('.', '/'), key, "B");
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
			} else if (type == char.class) {
				mv.visitFieldInsn(GETFIELD, clazz.getName().replace('.', '/'), key, "C");
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf",
				    "(C)Ljava/lang/Character;");
			} else if (type == boolean.class) {
				mv.visitFieldInsn(GETFIELD, clazz.getName().replace('.', '/'), key, "Z");
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
			} else if (type == float.class) {
				mv.visitFieldInsn(GETFIELD, clazz.getName().replace('.', '/'), key, "F");
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
			} else if (type == double.class) {
				mv.visitFieldInsn(GETFIELD, clazz.getName().replace('.', '/'), key, "D");
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
			} else {
				mv.visitFieldInsn(GETFIELD, clazz.getName().replace('.', '/'), name, "()L"
				    + type.getName().replace('.', '/') + ";");
			}
			return;
		} catch (Exception e) {
		}
		mv.visitLdcInsn(null);
	}
}
