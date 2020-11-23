package com.roubsite.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.VariableManager;

/**
 * 对象表达式节点, 向JVM语句栈内放入对象
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public abstract class ObjectExpression extends Expression {

	/** ASM名称 */
	public static final String NAME = ObjectExpression.class.getName().replace('.', '/');

	/** 变量的扩展访问节点列表 */
	List<Node> extendeds;

	public static double weak(Object o) {
		if (o == null || o.equals("")) {
			return 0.0d;
		}
		String s = o.toString();
		if (isNumeric(s) != 0) {
			return Double.parseDouble(s);
		} else {
			return Double.NaN;
		}
	}

	private static int isNumeric(String s) {
		int len = s.length();
		int ret;
		char c = s.charAt(0);
		if (c == '.') {
			ret = 1;
		} else if (!Character.isDigit(c) && c != '-') {
			return 0;
		} else {
			ret = -1;
		}

		for (int i = 1; i < len; i++) {
			c = s.charAt(i);
			if (c == '.') {
				if (ret > 0) {
					return 0;
				} else {
					ret = i + 1;
				}
			} else if (!Character.isDigit(c)) {
				return 0;
			}
		}
		return ret;
	}

	/**
	 * 对象转换成逻辑型数据, 对于NULL对象, 空字符串, 以及数值型的0, 均对应false, 否则对应true
	 * 
	 * @param o
	 *          源对象
	 * @return 转换的结果, 0表示<tt>false</tt>, 1表示 <tt>true</tt>
	 */
	public static boolean o2b(Object o) {
		if (o == null || o.equals("")) {
			return false;
		} else if (o instanceof Boolean) {
			return (Boolean) o;
		} else if (o instanceof Double) {
			return ((Double) o).doubleValue() != 0.0;
		} else if (o instanceof Number) {
			return ((Number) o).intValue() != 0;
		}
		return true;
	}

	/**
	 * 对象转换成整型数值, 对于数值类型的对象返回对应的整数值, 对于布尔对象用1/0表示 <tt>true</tt>/<tt>false</tt>, 其它对象将调用类型转换,
	 * 如果无法转换对象的信息成为一个整数将返回0值
	 * 
	 * @param o
	 *          需要转换的对象
	 * @return 对象表示的整数
	 */
	public static int o2i(Object o) {
		if (o == null || o.equals("")) {
			return 0;
		}
		if (o instanceof Number) {
			return ((Number) o).intValue();
		}
		if (o instanceof Boolean) {
			return ((Boolean) o) ? 1 : 0;
		}
		String s = o.toString();
		int ret = isNumeric(s);
		switch (ret) {
		case 0:
		case 1:
			return 0;
		case -1:
			return Integer.parseInt(s);
		default:
			return Integer.parseInt(s.substring(0, ret - 1));
		}
	}

	/**
	 * 对象转换成整型数值, 对于数值类型的对象返回对应的整数值, 对于布尔对象用1/0表示 <tt>true</tt>/<tt>false</tt>, 其它对象将调用类型转换,
	 * 如果无法转换对象的信息成为一个整数将返回NaN值
	 * 
	 * @param o
	 *          需要转换的对象
	 * @return 对象表示的浮点数
	 */
	public static double o2d(Object o) {
		if (o == null || o.equals("")) {
			return Double.NaN;
		}
		if (o instanceof Number) {
			return ((Number) o).doubleValue();
		}
		if (o instanceof Boolean) {
			return ((Boolean) o) ? 1 : 0;
		}
		String s = o.toString();
		if (isNumeric(s) == 0) {
			return Double.NaN;
		} else {
			return Double.parseDouble(s);
		}
	}

	/**
	 * 弱类型转换, 将对象转换为字符串, 其中<tt>null</tt>被转换成空字符串
	 * 
	 * @param o
	 *          源对象
	 * @return 源对象对应的字符串
	 */
	public static String o2s(Object o) {
		return o == null ? "" : o.toString();
	}

	public boolean hasExtended() {
		return extendeds != null;
	}
	
	/**
	 * 添加一个扩展引用节点, 变量可以要求进行列表或映射型的扩展, 实现源数据容器中复杂的对象描述
	 * 
	 * @param extended
	 *          变量访问扩展节点
	 */
	public void add(Node extended) {
		if (extendeds == null) {
			extendeds = new ArrayList<Node>();
		}

		extendeds.add(extended);
	}

	/**
	 * 输出自身
	 * 
	 * @param mv
	 *          ASM方法访问对象
	 * @param local
	 *          ASM方法内部的语句栈局部变量起始位置
	 * @param vm
	 *          变量管理器
	 */
	public abstract void parseSelf(MethodVisitorProxy mv, int local, VariableManager vm);

	@Override
	public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue,
	    Label lblFalse) {
		parseObject(mv, local, vm);
		mv.visitMethodInsn(INVOKESTATIC, NAME, "o2b", "(Ljava/lang/Object;)Z");
	}

	@Override
	public void parseInteger(MethodVisitorProxy mv, int local, VariableManager vm) {
		parseObject(mv, local, vm);
		mv.visitMethodInsn(INVOKESTATIC, NAME, "o2i", "(Ljava/lang/Object;)I");
	}

	@Override
	public void parseDouble(MethodVisitorProxy mv, int local, VariableManager vm) {
		parseObject(mv, local, vm);
		mv.visitMethodInsn(INVOKESTATIC, NAME, "o2d", "(Ljava/lang/Object;)D");
	}

	@Override
	public void parseString(MethodVisitorProxy mv, int local, VariableManager vm) {
		parseObject(mv, local, vm);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf",
		    "(Ljava/lang/Object;)Ljava/lang/String;");
	}

	@Override
	public void parseObject(MethodVisitorProxy mv, int local, VariableManager vm) {
		parseSelf(mv, local, vm);
		if (extendeds != null) {
			for (Node extended : extendeds) {
				extended.parse(mv, local, vm);
			}
		}
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		parseObject(mv, local, vm);
	}
}
