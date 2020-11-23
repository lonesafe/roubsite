package com.roubsite.smarty4j.statement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.Engine;
import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.Operator;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.Template;
import com.roubsite.smarty4j.TemplateReader;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.expression.VariableExpression;
import com.roubsite.smarty4j.statement.function.$call;
import com.roubsite.smarty4j.util.SimpleStack;

/**
 * 区块函数语句节点虚基类，区块函数指的是函数内部包含其它函数或文本，需要拥有结束标签的函数， 在模板分析过程中，系统首先调用函数的初始化方法，然后解析函数的参数，
 * 然后设置函数的父函数，最后解析函数的内部数据。 如果需要向Context中写入数据，请参见Function的函数说明。
 * 
 * @see com.roubsite.smarty4j.statement.Function
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class Block extends Function {

	/** ASM名称 */
	public static final String NAME = Block.class.getName().replace('.', '/');

	private static final Pattern commentStart = Pattern.compile("\\s*\\*");

	private static final Pattern commentEnd = Pattern.compile("\\*\\s*");

	/** 函数区块中的全部子语句 */
	protected List<Node> children;

	/**
	 * 向区块语句内部增加子语句。
	 * 
	 * @param child
	 *          需要增加到区块中的语句
	 * @throws ParseException
	 *           区块中不允许增加这种类型的语句
	 */
	public void addStatement(Node child) throws ParseException {
		if (children == null) {
			children = new ArrayList<Node>();
		}
		children.add(child);
	}

	@Override
	public void analyzeContent(Analyzer analyzer, TemplateReader reader) {
		Template tpl = analyzer.getTemplate();
		Engine engine = tpl.getEngine();
		String left = engine.getLeftDelimiter();
		String right = engine.getRightDelimiter();

		StringBuilder text = new StringBuilder(64);

		boolean debug = engine.isDebug();
		boolean isComment = false;
		while (true) {
			String line;

			try {
				line = reader.readLine();
			} catch (IOException e) {
				// 出现异常的概率极低
				throw new RuntimeException(e.getMessage());
			}

			if (line == null) {
				if (isComment) {
					reader.addMessage(String.format(MessageFormat.NOT_CORRECT, "The multi-line comment"));
				} else {
					// 文档已经结束, 如果不是文档函数本身, 则块状函数没有正常结束
					String name = getName();
					if (name == null) {
						try {
							addStatement(new TextStatement(analyzer, text.toString()));
						} catch (ParseException e) {
							if (engine.isDebug()) {
								e.printStackTrace();
							}
							reader.addMessage(e);
						}
					} else {
						reader.addMessage(String.format(MessageFormat.IS_REQUIRED, "The end label \"/" + name
						    + "\""));
					}
				}
				return;
			}

			try {
				// 对smarty标签进行词法分析
				if (isComment) {
					Matcher m = commentEnd.matcher(line);
					while (m.find()) {
						if (line.substring(m.end()).startsWith(right)) {
							isComment = false;
							reader.unread(line.substring(m.end() + right.length()));
							break;
						}
					}
					continue;
				}

				SimpleStack tokens = analyzer.lexical(line);
				if (tokens != null) {
					int size = tokens.size() - 2;

					int leftStart = (Integer) tokens.get(size);
					int rightEnd = (Integer) tokens.get(size + 1);

					text.append(line.substring(0, leftStart));
					// 将当前标签至上一个标签之间的内容设置成文本节点
					addStatement(new TextStatement(analyzer, text.toString()));
					reader.move(leftStart);
					text.setLength(0);
					reader.unread(line.substring(rightEnd));
					if (size > 0) {
						Object token = tokens.get(0);
						// 区块函数的结束标签
						if (Operator.DIV == token) {
							// 结束标签内不能有参数, 必须是{/[NAME]}的方式,
							// 并且结束标签必须与当前块函数相同
							String name = getName();
							if ((size == 2) && tokens.get(1).equals(name)) {
								return;
							}
							reader.addMessage(String.format(MessageFormat.NOT_CORRECT, "The end label \"/"
							    + tokens.get(1) + "\""));
						} else {
							// 结束标签没有实质上的处理, 不标记行号,
							// 只要不是结束标签就要标记行号
							if (debug && reader.isNewline()) {
								addStatement(new DebugStatement(reader.getLineNumber()));
							}
							// smarty的注释语法是两头均为'*'号
							if (Operator.MUL == token) {
								if (size == 1 || Operator.MUL != tokens.get(size - 1)) {
									reader.addMessage(String.format(MessageFormat.SYNTAX_ERROR_ON_TOKEN, "*"));
								}
							} else if (token instanceof String) {
								String word = (String) token;
								// 首个词是字符串, 是函数开始语句
								Function function = (Function) engine.createNode(word, true);
								if (function == null) {
									if (tpl.getFunction(word) != null) {
										function = new $call();
										tokens.set(0, "call");
										tokens.setSize(size);
										tokens.push("name");
										tokens.push(Operator.SET);
										tokens.push(new StringExpression(word));
										word = "call";
									} else {
										throw new ParseException(String.format(MessageFormat.IS_NOT_FOUND, "The function(" + word
										    + ")"));
									}
								} else {
									tokens.setSize(size);
								}
								function.setName(word);
								function.syntax(analyzer, tokens);
								if (function.setParent(this)) {
									addStatement(function);
								}
								function.analyzeContent(analyzer, reader);
							} else if (token instanceof VariableExpression && size > 2
							    && tokens.get(1) == Operator.SET) {
								addStatement(new SetStatement((VariableExpression) token, Operator.merge(tokens, 2,
								    size)));
							} else {
								try {
									addStatement(new PrintStatement(Operator.merge(tokens, 0, size)));
								} catch (ParseException e) {
									reader.addMessage(String.format(MessageFormat.CANNOT_BE_RESOLVED_TO,
									    "The statement", "a legal statement"));
								}
							}
						}
					} else {
						reader.addMessage(String.format(MessageFormat.CANNOT_BE_RESOLVED_TO, "The statement",
						    "a legal statement"));
					}
				} else {
					Matcher m = commentStart.matcher(line);
					while (m.find()) {
						if (line.substring(0, m.start()).endsWith(left)) {
							isComment = true;
							text.append(line.substring(0, m.start() - left.length()));
						}
					}
					if (!isComment) {
						text.append(line);
					}
				}
			} catch (ParseException e) {
				if (engine.isDebug()) {
					e.printStackTrace();
				}
				reader.addMessage(e);
			} catch (Exception e) {
				if (engine.isDebug()) {
					e.printStackTrace();
				}
				reader.addMessage(e.getMessage());
			}
		}
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		if (children != null) {
			for (Node child : children) {
				child.parse(mv, local, vm);
			}
		}
	}
}