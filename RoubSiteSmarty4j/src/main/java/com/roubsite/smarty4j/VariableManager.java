package com.roubsite.smarty4j;

import static org.objectweb.asm.Opcodes.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.MethodVisitor;

public class VariableManager {

	public static final int NOEXIST = -1;
	public static final int NOCACHE = 0;
	private static final int CACHE = 1;

	/** All variable information read in the current template, use -1 means a variable banned cache */
	private Map<String, Integer> variables;

	/** In the case of the need for rewrite variables in use cache */
	private boolean rewrite = false;

	public VariableManager(Engine engine) {
		if (engine != null && engine.isCached()) {
			variables = new HashMap<String, Integer>();
		}
	}

	/**
	 * 缓存使用情况查询
	 * 
	 * @return 是/否使用缓存
	 */
	public boolean hasCached() {
		return variables != null;
	}

	/**
	 * 阻止指定的变量进行缓存。
	 * 
	 * @param name
	 *          变量名称
	 */
	public void preventCache(String name) {
		if (variables != null) {
			variables.put(name, NOCACHE);
		}
	}

	/**
	 * 阻止所有的变量进行缓存。
	 */
	public void preventAllCache() {
		variables = null;
	}

	/**
	 * 回写情况查询
	 * 
	 * @return 是/否强制回写
	 */
	public boolean requiredRewrite() {
		return rewrite;
	}

	/**
	 * 设置强制回写
	 */
	public void forcedRewrite() {
		rewrite = true;
	}

	/**
	 * 获取变量的缓存索引编号
	 * 
	 * @param name
	 *          变量名
	 * @return 缓存索引编号
	 */
	public int getIndex(String name) {
		if (variables != null) {
			Integer value = variables.get(name);
			if (value != null) {
				return value;
			}
		}
		return NOEXIST;
	}

	/**
	 * 设置变量的缓存索引编号
	 * 
	 * @param name
	 *          变量名
	 * @param value
	 *          缓存索引编号
	 */
	public void setIndex(String name, int value) {
		variables.put(name, value);
	}

	/**
	 * 添加一次变量的使用记录。
	 * 
	 * @param name
	 *          变量名
	 */
	public void addUsedVariable(String name) {
		if (variables != null) {
			Integer value = variables.get(name);
			if (value == null) {
				variables.put(name, CACHE);
			}
		}
	}

	/**
	 * 合并两个对象管理器
	 * 
	 * @param vm
	 *          对象管理器
	 * @param removeNames
	 *          不需要合并的变量名列表
	 */
	public void merge(VariableManager vm, String... removeNames) {
		if (vm.variables == null) {
			variables = null;
			return;
		}
		rewrite = rewrite || vm.rewrite;
		Map<String, Integer> otherVariables = vm.variables;
		if (removeNames != null) {
			for (String name : removeNames) {
				otherVariables.remove(name);
			}
		}
		for (Entry<String, Integer> entry : otherVariables.entrySet()) {
			String key = entry.getKey();
			if (variables.containsKey(key)) {
				if (variables.get(key) == NOCACHE) {
					return;
				}
			}
			variables.put(key, entry.getValue());
		}
	}

	/**
	 * 变量初始化转换
	 * 
	 * @param mv
	 *          ASM方法访问对象
	 * @param local
	 *          堆栈顶点
	 * @return 总共的变量数量
	 */
	int parse(MethodVisitor mv, int local) {
		if (variables != null) {
			for (Entry<String, Integer> variable : variables.entrySet()) {
				String key = variable.getKey();
				if (variable.getValue() == CACHE) {
					mv.visitInsn(DUP);
					mv.visitLdcInsn(key);
					mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get",
					    "(Ljava/lang/String;)Ljava/lang/Object;");
					mv.visitVarInsn(ASTORE, local);
					variable.setValue(local);
					local++;
				}
			}
			mv.visitInsn(POP);
		}
		return local;
	}
}
