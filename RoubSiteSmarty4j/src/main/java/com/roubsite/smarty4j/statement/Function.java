package com.roubsite.smarty4j.statement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.Operator;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.TemplateReader;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.util.SimpleStack;

/**
 * 基本函数节点，表示一个完整的操作。
 * 
 * 如果函数执行中需要改变context中的某些值，请在scan方法中设置该变量不缓存，代码如下：
 * 
 * <code>
 *  public void scan(Template tpl, VariableManager vm) {
 *   super.scan(tpl);
 *   tpl.preventCacheVariable(variableName);
 * }
 * </code>
 * 
 * 如果无法确定改变context中属性名称，请在scan中关闭缓存，代码如下：
 * 
 * <code>
 *  public void scan(Template tpl, VariableManager vm) {
 *   super.scan(tpl);
 *   tpl.preventCacheVariable(preventAllCache);
 * }
 * </code>
 * 
 * @see com.roubsite.smarty4j.statement.Definition
 * @see com.roubsite.smarty4j.statement.function.$else
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public abstract class Function extends Parameter {

	/** 函数名 */
	private String name;
	private Set<String> options = new HashSet<String>();

	/** 父节点 */
	private Block parent;

	/**
	 * 获取函数名称。
	 * 
	 * @return 函数名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 函数初始化。
	 * 
	 * @param name
	 *          函数名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取父节点。
	 * 
	 * @return 父节点
	 */
	public Block getParent() {
		return parent;
	}

	/**
	 * 设置父节点，有些函数对父节点有特殊的要求，重新实现这个方法， 例如elseif只能是if下的子节点。
	 * 
	 * @param parent
	 *          父节点
	 * @return 如果需要引擎自动关联父子节点的关系返回true。某些情况下， 子结点需要关联父节点信息，但不希望它自己本身被父节点引用，
	 *         请返回false，父节点需要引用手动调用addStatement方法
	 * @throws ParseException
	 *           父节点不合法
	 */
	public boolean setParent(Block parent) throws ParseException {
		ParentType parentType = (ParentType) getClass().getAnnotation(ParentType.class);

		if (parentType != null) {
			String name = parentType.name();
			if (!name.equals(parent.getName())) {
				throw new ParseException(String.format(MessageFormat.MUST_BE_USED_INSIDE_OF, "\""
				    + getClass().getSimpleName().substring(1) + "\"", "\"" + name + "\""));
			}
		}

		this.parent = parent;
		return true;
	}

	public boolean contain(String option) {
		return options.contains(option);
	}
	
	/**
	 * 从指定的节点开始，向父节点遍历查找指定的类。
	 * 
	 * @param now
	 *          开始查找的节点
	 * @param clazz
	 *          希望父节点匹配的类对象
	 * @return 如果某一级父节点属于指定的类，则返回这个父节点，否则返回null
	 */
	public Function find(Function now, Class<?> clazz) {
		for (; now != null; now = now.getParent()) {
			if (clazz.isInstance(now)) {
				return now;
			}
		}
		return null;
	}

	/**
	 * 将词法分析的结果进行语法分析处理并形成函数节点，如果有特殊的语法，例如if函数， 需要重载这个方法。
	 * 
	 * @param tpl
	 *          模板
	 * @param tokens
	 *          词法分析结果
	 * @param wordSize
	 *          词法分析结果数量
	 * @throws ParseException
	 *           参数不合法
	 */
	public void syntax(Analyzer analyzer, SimpleStack tokens) throws ParseException {
		Definition[] parameters = getDefinitions();
		if (parameters != null) {
			Map<String, Expression> fields = new HashMap<String, Expression>();

			int index = 1;
			int size = tokens.size();
			for (; index < size;) {
				Object token = tokens.get(index);
				if (index + 1 == size || Operator.SET != tokens.get(index + 1)) {
					if (token instanceof String) {
						fields.put(parameters[index - 1].getName(), Expression.forString((String) token));
					} else if (token instanceof Expression) {
						fields.put(parameters[index - 1].getName(), (Expression) token);
					} else {
						break;
					}
				} else {
					break;
				}
				index++;
			}

			for (; size > 1; size--) {
				Object token = tokens.get(size - 1);
				if (!(token instanceof String)) {
					break;
				}
				Object prevToken = tokens.get(size - 2);
				if (prevToken instanceof Operator && prevToken != Operator.RGROUP) {
					break;
				}
				options.add((String) token);
			}

			for (; index + 2 < size;) {
				Object name = tokens.get(index);
				if ((name instanceof String) && Operator.SET == tokens.get(index + 1)) {
					Expression value = null;
					Object token = tokens.get(index + 2);
					if (token instanceof String) {
						value = Expression.forString((String) token);
						index += 3;
					} else {
						for (int i = index + 2; i < size; i++) {
							if (Operator.SET == tokens.get(i)) {
								value = Operator.merge(tokens, index + 2, i - 1);
								index = i - 1;
								break;
							}
						}
						if (value == null) {
							value = Operator.merge(tokens, index + 2, size);
							index = size;
						}
					}
					fields.put((String) name, value);
				} else {
					throw new ParseException(String.format(MessageFormat.NOT_CORRECT, "The parameter"));
				}
			}
			if (index != size) {
				throw new ParseException(String.format(MessageFormat.NOT_CORRECT, "The parameter"));
			}
			createParameters(parameters, fields);
		}
	}

	/**
	 * 函数参数的赋值处理，需要对函数参数进行第二次处理的， 或者函数参数有一些特别的语法规则的，需要重载这个方法。
	 * 
	 * @param parameters
	 *          函数的缺省参数信息
	 * @param fields
	 *          当前的参数列表
	 * @throws ParseException
	 *           参数错误将产生这个异常
	 */
	public void createParameters(Definition[] definitions, Map<String, Expression> fields)
	    throws ParseException {
		if (definitions != null) {
			int len = definitions.length;
			Expression[] parameters = new Expression[len];
			for (int i = 0; i < len; i++) {
				Definition definition = definitions[i];
				String name = definition.getName();
				parameters[i] = definition.getExpression(fields.get(name), "\"" + name + "\"");
			}
			PARAMETERS = parameters;
		}
	}

	/**
	 * 对文本输入流进行解析，如果区块函数内部有特殊的解析规则，例如literal， 单行函数也可能需要操作输入流，例如macro，请重载这个函数。
	 * 在函数的初始化完全完成后，才被调用这个方法解析输入流。
	 * 
	 * @param analyzer
	 *          语法分析器
	 * @param reader
	 *          文本输入对象
	 * @param vm
	 *          xxx
	 */
	public void analyzeContent(Analyzer analyzer, TemplateReader reader) throws ParseException {
	}
}