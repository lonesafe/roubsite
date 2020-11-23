package com.roubsite.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Method;
import java.util.Map;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.VariableManager;

/**
 * 变量Map型变量扩展节点, 将对象当成映射型结构, 根据提供的名称来访问对应的值, 如果对象为NULL返回NULL, 如果对象是 <tt>java.util.Map</tt>
 * 返回指定关键字的对象, 否则将按JavaBean规范调用对应的getXXX方法, 如果以上条件均不满足直接返回对象
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class MapExtended extends Node {

	/** ASM名称 */
	public static final String NAME = MapExtended.class.getName().replace('.', '/');

	/**
	 * 从映射关系中获取指定的对象
	 * 
	 * @param map
	 *          映射对象
	 * @param key
	 *          关键字
	 * @return 关键字对应的对象
	 */
	public static Object getValue(Object map, String key) {
		if (map == null) {
			return null;
		} else if (map instanceof Map) {
			return ((Map<?, ?>) map).get(key);
		} else if (key == null) {
			return null;
		} else {
			StringBuilder s = new StringBuilder(key.length() + 3);
			s.append("get");
			s.append(key);
			s.setCharAt(3, Character.toUpperCase(key.charAt(0)));
			try {
				return map.getClass().getMethod(s.toString()).invoke(map);
			} catch (Throwable e) {
				return null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void setValue(Object value, Object map, String key) {
		if (map instanceof Map) {
			((Map<String, Object>) map).put(key, value);
		}
	}
	
	/** 关键字表达式 */
	Expression key;

	/**
	 * 建立Map型变量扩展节点
	 * 
	 * @param key
	 *          关键字表达式
	 */
	public MapExtended(Expression key) {
		this.key = key;
	}

	/**
	 * 建立Map型变量扩展节点
	 * 
	 * @param key
	 *          关键字表达式
	 */
	public MapExtended(String key) {
		this(new StringExpression(key));
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		if (key instanceof StringExpression) {
			String[] names = key.toString().split("#");
			if (names.length > 1) {
				String name = names[0];
				name = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
				String type = names[1];
				try {
					Method method = Class.forName(type.replace('/', '.')).getMethod(name);
					String result = method.getReturnType().getName();

					if (!result.equals("void")) {
						mv.visitTypeInsn(CHECKCAST, type);
						if (result.equals("int")) {
							mv.visitMethodInsn(INVOKEVIRTUAL, type, name, "()I");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
							    "(I)Ljava/lang/Integer;");
						} else if (result.equals("long")) {
							mv.visitMethodInsn(INVOKEVIRTUAL, type, name, "()J");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
						} else if (result.equals("short")) {
							mv.visitMethodInsn(INVOKEVIRTUAL, type, name, "()S");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
						} else if (result.equals("byte")) {
							mv.visitMethodInsn(INVOKEVIRTUAL, type, name, "()B");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
						} else if (result.equals("char")) {
							mv.visitMethodInsn(INVOKEVIRTUAL, type, name, "()C");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf",
							    "(C)Ljava/lang/Character;");
						} else if (result.equals("boolean")) {
							mv.visitMethodInsn(INVOKEVIRTUAL, type, name, "()Z");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf",
							    "(Z)Ljava/lang/Boolean;");
						} else if (result.equals("float")) {
							mv.visitMethodInsn(INVOKEVIRTUAL, type, name, "()F");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
						} else if (result.equals("double")) {
							mv.visitMethodInsn(INVOKEVIRTUAL, type, name, "()D");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf",
							    "(D)Ljava/lang/Double;");
						} else {
							mv.visitMethodInsn(INVOKEVIRTUAL, type, name, "()L" + result.replace('.', '/') + ";");
						}
						return;
					}
				} catch (Exception e) {
				}
				mv.visitInsn(POP);
				mv.visitLdcInsn(null);
				return;
			}
		}
		key.parseString(mv, local, vm);
		mv.visitMethodInsn(INVOKESTATIC, NAME, "getValue",
		    "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;");
	}
}