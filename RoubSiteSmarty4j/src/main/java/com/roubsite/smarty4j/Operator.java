package com.roubsite.smarty4j;

import static org.objectweb.asm.Opcodes.DADD;
import static org.objectweb.asm.Opcodes.DDIV;
import static org.objectweb.asm.Opcodes.DMUL;
import static org.objectweb.asm.Opcodes.DREM;
import static org.objectweb.asm.Opcodes.DSUB;
import static org.objectweb.asm.Opcodes.IADD;
import static org.objectweb.asm.Opcodes.IAND;
import static org.objectweb.asm.Opcodes.IDIV;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFGE;
import static org.objectweb.asm.Opcodes.IFGT;
import static org.objectweb.asm.Opcodes.IFLE;
import static org.objectweb.asm.Opcodes.IFLT;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IMUL;
import static org.objectweb.asm.Opcodes.IOR;
import static org.objectweb.asm.Opcodes.IREM;
import static org.objectweb.asm.Opcodes.ISHL;
import static org.objectweb.asm.Opcodes.ISHR;
import static org.objectweb.asm.Opcodes.ISUB;
import static org.objectweb.asm.Opcodes.IUSHR;
import static org.objectweb.asm.Opcodes.IXOR;

import java.util.HashMap;
import java.util.Map;

import com.roubsite.smarty4j.expression.AndObject;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.MapExtended;
import com.roubsite.smarty4j.expression.NullExpression;
import com.roubsite.smarty4j.expression.OrObject;
import com.roubsite.smarty4j.expression.VariableExpression;
import com.roubsite.smarty4j.expression.check.AEQCheck;
import com.roubsite.smarty4j.expression.check.ANEQCheck;
import com.roubsite.smarty4j.expression.check.AndCheck;
import com.roubsite.smarty4j.expression.check.BinaryCheck;
import com.roubsite.smarty4j.expression.check.FalseCheck;
import com.roubsite.smarty4j.expression.check.NotCheck;
import com.roubsite.smarty4j.expression.check.OrCheck;
import com.roubsite.smarty4j.expression.check.TrueCheck;
import com.roubsite.smarty4j.expression.number.BinaryDouble;
import com.roubsite.smarty4j.expression.number.BinaryInteger;
import com.roubsite.smarty4j.expression.number.ConstDouble;
import com.roubsite.smarty4j.expression.number.ConstInteger;
import com.roubsite.smarty4j.expression.number.DoubleExpression;
import com.roubsite.smarty4j.util.SimpleStack;

/**
 * This class manages operator information, and provide a method for the combined expression.
 *
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class Operator {

	/** merge flag bit */
	public static final int FLOAT = 0;

	/** merge flag bit */
	public static final int INTEGER = 1;

	/** merge flag bit */
	public static final int OBJECT = 0;

	/** merge flag bit */
	public static final int BOOLEAN = 2;

	/** symbol '(' */
	public static final Operator LGROUP = new Operator(0, Type.LGROUP);

	/** symbol ')' */
	public static final Operator RGROUP = new Operator(0, Type.RGROUP);

	/** symbol '@' */
	public static final Operator AT = new Operator(1, Type.AT);

	/** symbol '~' */
	public static final Operator BNOT = new Operator(2, Type.BNOT, 1);

	/** symbol '!' */
	public static final Operator NOT = new Operator(2, Type.NOT, 1);

	/** symbol '-' */
	public static final Operator NEG = new Operator(2, Type.NEG, 1);

	/** symbol '/' */
	public static final Operator DIV = new Operator(3, Type.DIV);

	/** symbol '*' */
	public static final Operator MUL = new Operator(3, Type.MUL);

	/** symbol '%' */
	public static final Operator MOD = new Operator(3, Type.MOD);

	/** symbol '+' */
	public static final Operator ADD = new Operator(4, Type.ADD);

	/** symbol '-' */
	public static final Operator SUB = new Operator(4, Type.SUB);

	/** symbol '&lt;&lt;' */
	public static final Operator SHL = new Operator(5, Type.SHL);

	/** symbol '>>' */
	public static final Operator SHR = new Operator(5, Type.SHR);

	/** symbol '>>>' */
	public static final Operator SAR = new Operator(5, Type.SAR);

	/** symbol '>' */
	public static final Operator GT = new Operator(6, Type.GT);

	/** symbol '>=' */
	public static final Operator GTE = new Operator(6, Type.GTE);

	/** symbol '&lt;' */
	public static final Operator LT = new Operator(6, Type.LT);

	/** symbol '&lt;=' */
	public static final Operator LTE = new Operator(6, Type.LTE);

	/** symbol '==' */
	public static final Operator EQ = new Operator(7, Type.EQ);

	/** symbol '===' */
	public static final Operator AEQ = new Operator(7, Type.AEQ);

	/** symbol '!=' */
	public static final Operator NEQ = new Operator(7, Type.NEQ);

	/** symbol '!==' */
	public static final Operator ANE = new Operator(7, Type.ANE);

	/** symbol '&' */
	public static final Operator BAND = new Operator(8, Type.BAND);

	/** symbol '^' */
	public static final Operator BXOR = new Operator(9, Type.BXOR);

	/** symbol '|' */
	public static final Operator BOR = new Operator(10, Type.BOR);

	/** symbol '&&' */
	public static final Operator AND = new Operator(11, Type.AND);

	/** symbol '||' */
	public static final Operator OR = new Operator(12, Type.OR);

	/** symbol '=' */
	public static final Operator SET = new Operator(15, Type.SET);

	/** symbol ',' */
	public static final Operator COMMA = new Operator(16, Type.COMMA);

	/** symbol '#' */
	public static final Operator CONFIG = new Operator(20, Type.CONFIG);

	private static enum Type {
		LGROUP, RGROUP, AT, BNOT, NOT, NEG, DIV, MUL, MOD, ADD, SUB, SHL, SHR, SAR, GT, GTE, LT, LTE, EQ, AEQ, NEQ, ANE, BAND, BXOR, BOR, AND, OR, ISDIVBY, ISEVENBY, ISODDBY, ISEVEN, ISODD, SET, COMMA, CONFIG
	}

	private static final Map<String, Operator> opmap = new HashMap<String, Operator>();
	// private static final Operator[] special = {
	// new Operator("is div by", 14, Type.ISDIVBY),
	// new Operator("is even by", 14, Type.ISEVENBY),
	// new Operator("is odd by", 14, Type.ISODDBY),
	// new Operator("is even", 14, Type.ISEVEN, 1),
	// new Operator("is not odd", 14, Type.ISEVEN, 1),
	// new Operator("is odd", 14, Type.ISODD, 1),
	// new Operator("is not even", 14, Type.ISODD, 1) };

	static {
		opmap.put("not", NOT);
		opmap.put("div", DIV);
		opmap.put("mod", MOD);
		opmap.put("gt", GT);
		opmap.put("ge", GTE);
		opmap.put("gte", GTE);
		opmap.put("lt", LT);
		opmap.put("le", LTE);
		opmap.put("lte", LTE);
		opmap.put("eq", EQ);
		opmap.put("ne", NEQ);
		opmap.put("neq", NEQ);
		opmap.put("and", AND);
		opmap.put("or", OR);
	}

	private String[] name;
	private int priority;
	private int param;
	private Type type;

	/**
	 * Merge the contents of the specified interval to generate expression.
	 *
	 * @param tokens
	 *          the list will store all token of the statement
	 * @param start
	 *          the start index of expression in tokens
	 * @param end
	 *          the end index of expression in tokens
	 * @return an expression generated according to the specified interval
	 * @throws ParseException
	 *           If the specified interval is not a valid expression syntax
	 */
	public static Expression merge(SimpleStack tokens, int start, int end) throws ParseException {
		for (int i = start; i < end; i++) {
			if (tokens.get(i) instanceof DoubleExpression) {
				return merge(tokens, start, end, FLOAT | OBJECT);
			}
		}
		return merge(tokens, start, end, INTEGER | OBJECT);
	}

	/**
	 * Merge the contents of the specified interval to generate expression.
	 *
	 * @param tokens
	 *          the list will store all token of the statement
	 * @param start
	 *          the start index of expression in tokens
	 * @param end
	 *          the end index of expression in tokens
	 * @param flag
	 *          the flag bit is the way to control and combine expressions and can be INTEGER or
	 *          FLOAT, BOOLEAN or OBJECT
	 * @return an expression generated according to the specified interval
	 * @throws ParseException
	 *           If the specified interval is not a valid expression syntax
	 */
	public static Expression merge(SimpleStack tokens, int start, int end, int flag)
	    throws ParseException {
		// Reverse Polish notation
		SimpleStack ops = new SimpleStack();
		SimpleStack exps = new SimpleStack();
		// first expression flag, change SUB to MINUS
		boolean isFirst = true;
		for (int i = start; i < end;) {
			Object word = tokens.get(i);
			if (isFirst && word == Operator.SUB) {
				ops.push(Operator.NEG);
				isFirst = false;
			} else {
				if (word.equals("true")) {
					exps.push(TrueCheck.VALUE);
					isFirst = false;
				} else if (word.equals("false")) {
					exps.push(FalseCheck.VALUE);
					isFirst = false;
				} else if (word.equals("null")) {
					exps.push(NullExpression.VALUE);
					isFirst = false;
				} else if (word instanceof Expression) {
					exps.push(word);
					isFirst = false;
				} else if (word == Operator.CONFIG && i + 2 < end && tokens.get(i + 1) instanceof String
				    && tokens.get(i + 2) == Operator.CONFIG) {
					VariableExpression exp = new VariableExpression(null, "smarty");
					exp.add(new MapExtended("config"));
					exp.add(new MapExtended((String) tokens.get(i + 1)));
					exps.push(exp);
					i += 2;
				} else if (word == Operator.LGROUP) {
					ops.push(null);
					isFirst = true;
				} else if (word == Operator.RGROUP) {
					while (true) {
						if (ops.size() == 0) {
							throw new ParseException(String.format(MessageFormat.SYNTAX_ERROR_ON_TOKEN, ")"));
						}
						Operator op = (Operator) ops.pop();
						if (op != null) {
							op.merge(exps, flag);
						} else {
							break;
						}
					}
					isFirst = false;
				} else {
					isFirst = true;
					Operator op = null;

					if (word instanceof Operator) {
						op = (Operator) word;
					} else {
						op = opmap.get(word.toString());
						// if (op == null) {
						// outer: for (Operator o : special) {
						// String[] name = o.name;
						// // the operator is composed of a group of words
						// int len = name.length;
						// int index = i + len - 1;
						// if (index < end) {
						// for (; index >= i; index--) {
						// if (!name[index - i].equals(tokens.get(index))) {
						// continue outer;
						// }
						// }
						// op = o;
						// break;
						// }
						// }
						// }
					}
					if (op != null) {
						i += op.name == null ? 1 : op.name.length;
						int priority = op.priority;
						for (int size = ops.size() - 1; size >= 0; size--) {
							Operator tmp = (Operator) ops.pop();
							if ((tmp != null) && (priority >= tmp.priority)) {
								tmp.merge(exps, flag);
							} else {
								ops.setSize(ops.size() + 1);
								break;
							}
						}
						ops.push(op);
						continue;
					}
					throw new ParseException(String.format(MessageFormat.SYNTAX_ERROR_ON_TOKEN,
					    "Invalid Operator"));
				}
			}
			i++;
		}

		for (int size = ops.size() - 1; size >= 0; size--) {
			Operator op = (Operator) ops.get(size);
			if (op == null) {
				throw new ParseException(String.format(MessageFormat.SYNTAX_ERROR_ON_TOKEN, "("));
			}
			op.merge(exps, flag);
		}

		if (exps.size() == 1) {
			return (Expression) exps.pop();
		} else {
			throw new ParseException(String.format(MessageFormat.SYNTAX_ERROR_ON_TOKEN,
			    "Invalid Expression"));
		}
	}

	private Operator(int priority, Type type) {
		this(null, priority, type, 2);
	}

	private Operator(String name, int priority, Type type) {
		this(name, priority, type, 2);
	}

	private Operator(int priority, Type type, int param) {
		this(null, priority, type, param);
	}

	private Operator(String name, int priority, Type type, int param) {
		if (name != null) {
			this.name = name.split(" ");
		}
		this.priority = priority;
		this.type = type;
		this.param = param;
	}

	private void merge(SimpleStack expressions, int flag) throws ParseException {
		boolean isInt = (flag & INTEGER) != 0;
		boolean isBool = (flag & BOOLEAN) != 0;

		if (expressions.size() < param) {
			throw new ParseException("Syntax error on the expression");
		}
		Expression exp;
		Expression exp2;
		if (param == 1) {
			exp = (Expression) expressions.pop();
			exp2 = null;
		} else {
			exp2 = (Expression) expressions.pop();
			exp = (Expression) expressions.pop();
		}
		switch (type) {
		case BNOT:
			expressions.push(new BinaryInteger(IXOR, exp, new ConstInteger(-1)));
			break;
		case NOT:
			expressions.push(new NotCheck(exp));
			break;
		case NEG:
			if (exp instanceof ConstInteger) {
				((ConstInteger) exp).inverse();
				expressions.push(exp);
			} else if (exp instanceof ConstDouble) {
				((ConstDouble) exp).inverse();
				expressions.push(exp);
			} else {
				expressions.push(isInt ? new BinaryInteger(ISUB, ConstInteger.ZERO, exp)
				    : new BinaryDouble(DSUB, ConstDouble.ZERO, exp));
			}
			break;
		case DIV:
			expressions.push(isInt ? new BinaryInteger(IDIV, exp, exp2) : new BinaryDouble(DDIV, exp,
			    exp2));
			break;
		case MUL:
			expressions.push(isInt ? new BinaryInteger(IMUL, exp, exp2) : new BinaryDouble(DMUL, exp,
			    exp2));
			break;
		case MOD:
			expressions.push(isInt ? new BinaryInteger(IREM, exp, exp2) : new BinaryDouble(DREM, exp,
			    exp2));
			break;
		case ADD:
			expressions.push(isInt ? new BinaryInteger(IADD, exp, exp2) : new BinaryDouble(DADD, exp,
			    exp2));
			break;
		case SUB:
			expressions.push(isInt ? new BinaryInteger(ISUB, exp, exp2) : new BinaryDouble(DSUB, exp,
			    exp2));
			break;
		case SHL:
			expressions.push(new BinaryInteger(ISHL, exp, exp2));
			break;
		case SHR:
			expressions.push(new BinaryInteger(ISHR, exp, exp2));
			break;
		case SAR:
			expressions.push(new BinaryInteger(IUSHR, exp, exp2));
			break;
		case GT:
			expressions.push(new BinaryCheck(IFGT, exp, exp2));
			break;
		case GTE:
			expressions.push(new BinaryCheck(IFGE, exp, exp2));
			break;
		case LT:
			expressions.push(new BinaryCheck(IFLT, exp, exp2));
			break;
		case LTE:
			expressions.push(new BinaryCheck(IFLE, exp, exp2));
			break;
		case EQ:
			expressions.push(new BinaryCheck(IFEQ, exp, exp2));
			break;
		case AEQ:
			expressions.push(new AEQCheck(exp, exp2));
			break;
		case NEQ:
			expressions.push(new BinaryCheck(IFNE, exp, exp2));
			break;
		case ANE:
			expressions.push(new ANEQCheck(exp, exp2));
			break;
		case BAND:
			expressions.push(new BinaryInteger(IAND, exp, exp2));
			break;
		case BXOR:
			expressions.push(new BinaryInteger(IXOR, exp, exp2));
			break;
		case BOR:
			expressions.push(new BinaryInteger(IOR, exp, exp2));
			break;
		case AND:
			expressions.push(isBool ? new AndCheck(exp, exp2) : new AndObject(exp, exp2));
			break;
		case OR:
			expressions.push(isBool ? new OrCheck(exp, exp2) : new OrObject(exp, exp2));
			break;
		case ISDIVBY:
			expressions.push(new NotCheck(new BinaryInteger(IREM, exp, exp2)));
			break;
		case ISEVENBY:
			expressions.push(new NotCheck(new BinaryInteger(IREM, new BinaryInteger(IDIV, exp, exp2),
			    new ConstInteger(2))));
			break;
		case ISODDBY:
			expressions.push(new BinaryInteger(IREM, new BinaryInteger(IDIV, exp, exp2),
			    new ConstInteger(2)));
			break;
		case ISEVEN:
			expressions.push(new NotCheck(new BinaryInteger(IREM, exp, new ConstInteger(2))));
			break;
		case ISODD:
			expressions.push(new BinaryInteger(IREM, exp, new ConstInteger(2)));
			break;
		default:
			throw new ParseException(String.format(MessageFormat.SYNTAX_ERROR_ON_TOKEN,
			    "Invalid Operator"));
		}
	}
}
