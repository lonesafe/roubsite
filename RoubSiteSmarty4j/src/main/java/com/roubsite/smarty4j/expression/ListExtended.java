package com.roubsite.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Array;
import java.util.List;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.number.ConstInteger;

/**
 * 变量List型变量扩展节点, 将对象当成列表型结构, 根据提供的序号来访问对应的值. 当对象为NULL时返回NULL, 当对象为 <tt>java.util.List</tt>
 * 时调用get()方法返回, 当对象为数组时返回对象中指定位置的值, 当对象为字符串时返回字符串中的指定字符, 如果以上条件均不满足直接返回对象
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class ListExtended extends Node {

	/** ASM名称 */
	public static final String NAME = ListExtended.class.getName().replace('.', '/');

	/**
	 * 从列表中获取指定序号的对象
	 * 
	 * @param list
	 *          "List"对象
	 * @param index
	 *          序号
	 * @return 指定序号的对象
	 */
	public static Object getValue(Object list, int index) {
		if (list != null) {
			if (list instanceof List) {
				List<?> l = (List<?>) list;
				if (index < l.size()) {
					return l.get(index);
				}
			} else if (list.getClass().isArray()) {
				if (index < Array.getLength(list)) {
					return Array.get(list, index);
				}
			} else if (list instanceof String) {
				String s = (String) list;
				if (index < s.length()) {
					return ((String) list).charAt(index);
				}
			} else {
				return list;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static void setValue(Object value, Object list, int index) {
		if (list instanceof List) {
			List<Object> l = (List<Object>) list;
			if (index < l.size()) {
				l.set(index, value);
			}
		}
	}

	/** 序号表达式 */
	Expression index;

	/**
	 * 创建List型变量扩展节点
	 * 
	 * @param index
	 *          索引表达式
	 */
	public ListExtended(Expression index) {
		this.index = index;
	}

	/**
	 * 创建List型变量扩展节点
	 * 
	 * @param index
	 *          索引数值
	 */
	public ListExtended(int index) {
		this(new ConstInteger(index));
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		index.parseInteger(mv, local, vm);
		mv.visitMethodInsn(INVOKESTATIC, NAME, "getValue", "(Ljava/lang/Object;I)Ljava/lang/Object;");
	}
}