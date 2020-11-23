package com.roubsite.smarty4j.statement;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.Template;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;

/**
 * 包含参数定义的节点虚基类。
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public abstract class Parameter extends Node {

	private static final Map<String, String> allDesc = new HashMap<String, String>();

	protected Expression[] PARAMETERS;

	public Parameter() {
		String name = this.getClass().getName();
		if (!allDesc.containsKey(name)) {
			StringBuilder sb = new StringBuilder();
			Definition[] definitions = getDefinitions();
			if ((definitions != null) && (definitions.length > 0)) {
				for (int i = 0; i < definitions.length; i++) {
					sb.append(definitions[i].getType());
				}
			}
			allDesc.put(name, sb.toString());
		}
	}

	/**
	 * 获取全部的参数特征定义。
	 * 
	 * @return 参数特征描述数组
	 */
	public Definition[] getDefinitions() {
		return null;
	}

	public String getDesc() {
		String desc = allDesc.get(this.getClass().getName());
		if (desc == null) {
			return "[Ljava/lang/Object;";
		}
		return desc;
	}

	/**
	 * 参数代码转换，根据节点的信息将参数转换成数组放入JVM语句栈中。
	 * 
	 * @param mv
	 *            ASM方法访问对象
	 * @param local
	 *            ASM方法内部的语句栈局部变量起始位置
	 * @param vm
	 *            变量管理器
	 */
	public void parseAllParameters(MethodVisitorProxy mv, int local, VariableManager vm) {
		int len = PARAMETERS != null ? PARAMETERS.length : 0;

		if (allDesc.get(this.getClass().getName()) != null) {
			if (len > 0) {
				for (int i = 0; i < len; i++) {
					if (PARAMETERS[i] == null) {
						mv.visitLdcInsn(null);
					} else {
						PARAMETERS[i].parse(mv, local, vm);
					}
				}
			}
		} else {
			if (len == 0) {
				// 没有参数
				mv.visitLdcInsn(null);
			} else {
				mv.visitLdcInsn(PARAMETERS.length);
				mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");

				for (int i = 0; i < len; i++) {
					if (PARAMETERS[i] == null) {
						continue;
					}
					mv.visitInsn(DUP);
					mv.visitLdcInsn(i);
					PARAMETERS[i].parseObject(mv, local, vm);
					mv.visitInsn(AASTORE);
				}
			}
		}
	}

	protected Method getMethod(String name) {
		for (Method method : this.getClass().getMethods()) {
			if (method.getName().equals(name)) {
				return method;
			}
		}
		throw new RuntimeException(String.format(MessageFormat.IS_NOT_FOUND, name));
	}

	/**
	 * 将函数对象放入语句栈中，提供给LineFunction与BlockFunction使用。
	 * 
	 * @see com.roubsite.smarty4j.statement.LineFunction
	 * @see com.roubsite.smarty4j.statement.BlockFunction
	 * 
	 * @param mv
	 *            ASM方法操作者
	 * @param local
	 *            ASM语句栈的局部变量起始位置
	 * @param index
	 *            函数在Template中保存的位置
	 */
	protected String parseNode(MethodVisitorProxy mv, int local, int index) {
		String name = this.getClass().getName().replace('.', '/');
		mv.visitVarInsn(ALOAD, TEMPLATE);
		mv.visitLdcInsn(index);
		mv.visitMethodInsn(INVOKEVIRTUAL, Template.NAME, "getNode", "(I)Ljava/lang/Object;");
		mv.visitTypeInsn(CHECKCAST, name);
		return name;
	}
}
