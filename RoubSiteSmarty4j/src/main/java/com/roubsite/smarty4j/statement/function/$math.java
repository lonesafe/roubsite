package com.roubsite.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DALOAD;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IALOAD;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.INSTANCEOF;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import java.util.Map;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Operator;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.NullExpression;
import com.roubsite.smarty4j.expression.ObjectExpression;
import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.expression.number.ConstDouble;
import com.roubsite.smarty4j.expression.number.ConstInteger;
import com.roubsite.smarty4j.expression.number.DoubleAdapter;
import com.roubsite.smarty4j.expression.number.IntegerAdapter;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Function;
import com.roubsite.smarty4j.util.ExtInteger;
import com.roubsite.smarty4j.util.SimpleStack;

/**
 * {math} allows the template designer to do math equations in the template.
 *
 * <table border="1">
 * <colgroup> <col align="center" class="param">
 * <col align="center" class="type"> <col align="center" class="required">
 * <col align="center" class="default"> <col class="desc"> </colgroup> <thead>
 * <tr>
 * <th align="center">Attribute Name</th>
 * <th align="center">Type</th>
 * <th align="center">Required</th>
 * <th align="center">Default</th>
 * <th>Description</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td align="center">equation</td>
 * <td align="center">string</td>
 * <td align="center">Yes</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>The equation to execute</td>
 * </tr>
 * <tr>
 * <td align="center">format</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>The format of the result (sprintf)</td>
 * </tr>
 * <tr>
 * <td align="center">assign</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>Template variable the output will be assigned to</td>
 * </tr>
 * <tr>
 * <td align="center">[var ...]</td>
 * <td align="center">numeric</td>
 * <td align="center">Yes</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>Equation variable value</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $math extends Function {

	public static class LocalExpression extends Expression {

		private ExtInteger local;
		private int index;

		public LocalExpression(ExtInteger local, int index) {
			this.local = local;
			this.index = index;
		}

		@Override
		public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue, Label lblFalse) {
		}

		@Override
		public void parseInteger(MethodVisitorProxy mv, int local, VariableManager vm) {
			mv.visitVarInsn(ALOAD, this.local.intValue());
			mv.visitLdcInsn(index);
			mv.visitInsn(IALOAD);
		}

		@Override
		public void parseDouble(MethodVisitorProxy mv, int local, VariableManager vm) {
			mv.visitVarInsn(ALOAD, this.local.intValue());
			mv.visitLdcInsn(index);
			mv.visitInsn(DALOAD);
		}

		@Override
		public void parseString(MethodVisitorProxy mv, int local, VariableManager vm) {
		}

		@Override
		public void parseObject(MethodVisitorProxy mv, int local, VariableManager vm) {
		}

		@Override
		public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		}
	}

	public static final String NAME = $math.class.getName().replace('.', '/');

	public static Object translate(Object[] values) {
		int size = values.length;
		for (int i = 0; i < size; i++) {
			Object value = values[i];
			if (value != null && !(value instanceof Integer)) {
				if (value.toString().indexOf('.') >= 0) {
					double[] ret = new double[size];
					for (; --size >= 0;) {
						ret[size] = ObjectExpression.o2d(values[size]);
					}
					return ret;
				}
			}
		}
		int[] ret = new int[size];
		for (; --size >= 0;) {
			ret[size] = ObjectExpression.o2i(values[size]);
		}
		return ret;
	}

	/** 参数定义 */
	private static final Definition[] definitions = { Definition.forFunction("equation", Type.STRING),
			Definition.forFunction("format", Type.STRING, new StringExpression("%d")),
			Definition.forFunction("assign", Type.STRING, NullExpression.VALUE) };

	/** 数学表达式 */
	private Expression iExp;
	private Expression fExp;
	private int size;
	private ExtInteger local = new ExtInteger();

	/**
	 * 词法分析
	 * 
	 * @param line
	 *            需要分析的行
	 * @param fields
	 *            当前的参数列表
	 * @return 词法分析的结果
	 * @throws ParseException
	 *             语法产生错误时将产生异常
	 */
	private SimpleStack analyseMath(String line, Map<String, Expression> fields) throws ParseException {
		// 保存所有的参数与值
		Expression[] expressions = new Expression[fields.size() + 3];
		expressions[0] = PARAMETERS[0];
		expressions[1] = PARAMETERS[1];
		expressions[2] = PARAMETERS[2];
		PARAMETERS = expressions;

		SimpleStack tokens = new SimpleStack();
		int start = 0;
		int len = line.length();
		while (start < len) {
			char c = line.charAt(start);
			switch (c) {
			case ' ':
				start++;
				continue;
			case '(':
				start++;
				tokens.push(Operator.LGROUP);
				continue;
			case ')':
				start++;
				tokens.push(Operator.RGROUP);
				continue;
			case '~':
				start++;
				tokens.push(Operator.BNOT);
				continue;
			case '/':
				start++;
				tokens.push(Operator.DIV);
				continue;
			case '*':
				start++;
				tokens.push(Operator.MUL);
				continue;
			case '%':
				start++;
				tokens.push(Operator.MOD);
				continue;
			case '+':
				start++;
				tokens.push(Operator.ADD);
				continue;
			case '-':
				start++;
				tokens.push(Operator.SUB);
				continue;
			case '&':
				start++;
				tokens.push(Operator.BAND);
				continue;
			case '|':
				start++;
				tokens.push(Operator.BOR);
				continue;
			case '<':
				if (start + 1 < len && line.charAt(start + 1) == '<') {
					if (start + 2 < len && line.charAt(start + 2) == '<') {
						start += 3;
					} else {
						start += 2;
					}
					tokens.push(Operator.SHL);
					continue;
				}
				break;
			case '>':
				if (start + 1 < len && line.charAt(start + 1) == '>') {
					if (start + 2 < len && line.charAt(start + 2) == '>') {
						start += 3;
						tokens.push(Operator.SAR);
					} else {
						start += 2;
						tokens.push(Operator.SHR);
					}
					continue;
				}
				break;
			case '^':
				start++;
				tokens.push(Operator.BXOR);
				continue;
			default:
				if (Character.isDigit(c)) {
					boolean isFloat = false;
					// 处理数值常量
					int end = start + 1;
					while (end < len) {
						char d = line.charAt(end);
						if (Character.isJavaIdentifierStart(d)) {
							throw new ParseException(String.format(MessageFormat.CANNOT_BE_RESOLVED_TO,
									"\"" + line.substring(start, end + 1) + "\"", "a constant"));
						} else if (d == '.') {
							if (isFloat) {
								throw new ParseException(String.format(MessageFormat.CANNOT_BE_RESOLVED_TO,
										"\"" + line.substring(start, end + 1) + "\"", "a constant"));
							}
							isFloat = true;
						} else if (!Character.isDigit(d)) {
							break;
						}
						end++;
					}

					if (isFloat) {
						tokens.push(new ConstDouble(Double.parseDouble(line.substring(start, end))));
					} else {
						tokens.push(new ConstInteger(Integer.parseInt(line.substring(start, end))));
					}
					start = end;
					continue;
				} else if (Character.isLetter(c)) {
					// 识别变量名
					int end = start + 1;
					while (end < len) {
						if (!Character.isJavaIdentifierPart(line.charAt(end))) {
							break;
						}
						end++;
					}

					String name = line.substring(start, end);
					if (!fields.containsKey(name)) {
						throw new ParseException(String.format(MessageFormat.IS_NOT_FOUND, "\"" + name + "\""));
					}
					Expression exp = fields.get(name);
					if (!(exp instanceof LocalExpression)) {
						PARAMETERS[size + 3] = exp;
						exp = new LocalExpression(local, size);
						size++;
						fields.put(name, exp);
					}
					tokens.push(exp);
					start = end;
					continue;
				}
			}
			throw new ParseException(String.format(MessageFormat.SYNTAX_ERROR_ON_TOKEN, line.substring(0, 1)));
		}
		return tokens;
	}

	@Override
	public void createParameters(Definition[] parameters, Map<String, Expression> fields) throws ParseException {
		super.createParameters(parameters, fields);
		fields.remove("equation");
		fields.remove("format");
		fields.remove("assign");

		SimpleStack tokens = analyseMath(PARAMETERS[0].toString(), fields);
		iExp = new IntegerAdapter(Operator.merge(tokens, 0, tokens.size(), Operator.INTEGER | Operator.OBJECT));
		fExp = new DoubleAdapter(Operator.merge(tokens, 0, tokens.size(), Operator.FLOAT | Operator.OBJECT));
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		Label noint = new Label();
		Label end = new Label();
		this.local.set(local);

		if (PARAMETERS[2] == NullExpression.VALUE) {
			mv.visitVarInsn(ALOAD, WRITER);
		}

		mv.visitLdcInsn(size);
		mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
		for (int i = 0; i < size; i++) {
			mv.visitInsn(DUP);
			mv.visitLdcInsn(i);
			PARAMETERS[i + 3].parseObject(mv, local + 1, vm);
			mv.visitInsn(AASTORE);
		}

		mv.visitMethodInsn(INVOKESTATIC, NAME, "translate", "([Ljava/lang/Object;)Ljava/lang/Object;");
		mv.visitInsn(DUP);
		mv.visitTypeInsn(INSTANCEOF, "[D");
		mv.visitJumpInsn(IFNE, noint);

		mv.visitTypeInsn(CHECKCAST, "[I");
		mv.visitVarInsn(ASTORE, local);
		iExp.parseObject(mv, local + 1, vm);
		mv.visitJumpInsn(GOTO, end);

		mv.visitLabel(noint);
		mv.visitTypeInsn(CHECKCAST, "[D");
		mv.visitVarInsn(ASTORE, local);
		fExp.parseObject(mv, local + 1, vm);

		mv.visitLabel(end);

		if (PARAMETERS[2] == NullExpression.VALUE) {
			if (PARAMETERS[1] != NullExpression.VALUE) {
				mv.visitVarInsn(ASTORE, local);
				PARAMETERS[1].parseString(mv, local + 1, vm);
				mv.visitLdcInsn(1);
				mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
				mv.visitInsn(DUP);
				mv.visitLdcInsn(0);
				mv.visitVarInsn(ALOAD, local);
				mv.visitInsn(AASTORE);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "format",
						"(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;");
			} else {
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
			}
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "write", "(Ljava/lang/String;)V");
		} else {
			writeVariable(mv, local, vm, PARAMETERS[2].toString(), null);
		}
	}
}