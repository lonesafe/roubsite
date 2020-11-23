package com.roubsite.smarty4j.statement;

import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.ListExpression;
import com.roubsite.smarty4j.expression.MapExpression;
import com.roubsite.smarty4j.expression.ObjectAdapter;
import com.roubsite.smarty4j.expression.ObjectExpression;
import com.roubsite.smarty4j.expression.StringAdapter;
import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.expression.check.CheckAdapter;
import com.roubsite.smarty4j.expression.check.CheckExpression;
import com.roubsite.smarty4j.expression.check.FalseCheck;
import com.roubsite.smarty4j.expression.check.TrueCheck;
import com.roubsite.smarty4j.expression.number.ConstDouble;
import com.roubsite.smarty4j.expression.number.ConstInteger;
import com.roubsite.smarty4j.expression.number.DoubleAdapter;
import com.roubsite.smarty4j.expression.number.DoubleExpression;
import com.roubsite.smarty4j.expression.number.IntegerAdapter;
import com.roubsite.smarty4j.expression.number.IntegerExpression;

/**
 * 函数参数特征信息描述类。
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class Definition {

	public static enum Type {
		BOOLEAN, INTEGER, DOUBLE, STRING, LIST, MAP, OBJECT, BOOLOBJ, INTOBJ, DBLOBJ, STROBJ
	};

	private String name;
	private Type type;
	private Expression value;
	private String retType;

	public static Definition forModifier(Type type) {
		return new Definition(null, type, null, null);
	}

	public static Definition forModifier(Type type, Expression defValue) {
		return new Definition(null, type, defValue, null);
	}

	public static Definition forModifier(Type type, Expression defValue, String retType) {
		return new Definition(null, type, defValue, retType);
	}

	public static Definition forFunction(String name, Type type) {
		return new Definition(name, type, null, null);
	}

	public static Definition forFunction(String name, Type type, Expression defValue) {
		return new Definition(name, type, defValue, null);
	}

	public static Definition forFunction(String name, Type type, Expression defValue, String retType) {
		return new Definition(name, type, defValue, retType);
	}

	private Definition(String name, Type type, Expression defValue, String retType) {
		this.name = name;
		this.type = type;
		this.value = defValue;
		this.retType = retType;
	}

	/**
	 * 获取自定义属性。
	 * 
	 * @return 自定义属性对象
	 */
	public String getName() {
		return name;
	}

	/**
	 * 检测传入的参数表达式的合法性，并输出参数最终对应的表达式。
	 * 
	 * @param expression
	 *          参数表达式
	 * @param name
	 *          参数名称
	 * @return 参数表达式
	 * @throws ParseException
	 *           参数验证错误时产生异常
	 */
	Expression getExpression(Expression expression, String name) throws ParseException {
		if (expression == null) {
			if (value == null) {
				throw new ParseException(String.format(MessageFormat.IS_REQUIRED, name));
			} else {
				return value;
			}
		} else {
			switch (type) {
			case BOOLEAN:
				if (expression != TrueCheck.VALUE && expression != FalseCheck.VALUE) {
					throw new ParseException(String.format(MessageFormat.CANNOT_BE_RESOLVED_TO, name,
					    "a boolean"));
				}
				break;
			case INTEGER:
				if (!(expression instanceof ConstInteger)) {
					throw new ParseException(String.format(MessageFormat.CANNOT_BE_RESOLVED_TO, name,
					    "an integer"));
				}
				break;
			case DOUBLE:
				if (!(expression instanceof ConstInteger) && !(expression instanceof ConstDouble)) {
					throw new ParseException(String.format(MessageFormat.CANNOT_BE_RESOLVED_TO, name,
					    "a float"));
				}
				break;
			case STRING:
				if (!(expression instanceof StringExpression)) {
					throw new ParseException(String.format(MessageFormat.CANNOT_BE_RESOLVED_TO, name,
					    "a string"));
				}
				break;
			case LIST:
				if (!(expression instanceof ListExpression)) {
					throw new ParseException(String.format(MessageFormat.CANNOT_BE_RESOLVED_TO, name,
					    "a list"));
				}
				break;
			case MAP:
				if (!(expression instanceof MapExpression)) {
					throw new ParseException(String.format(MessageFormat.CANNOT_BE_RESOLVED_TO, name,
					    "a map"));
				}
				break;
			case OBJECT:
				if (!(expression instanceof ObjectExpression)) {
					expression = new ObjectAdapter(expression);
				}
				break;
			case BOOLOBJ:
				if (!(expression instanceof CheckExpression)) {
					expression = new CheckAdapter(expression);
				}
				break;
			case INTOBJ:
				if (!(expression instanceof IntegerExpression)) {
					expression = new IntegerAdapter(expression);
				}
				break;
			case DBLOBJ:
				if (!(expression instanceof DoubleExpression)) {
					expression = new DoubleAdapter(expression);
				}
				break;
			case STROBJ:
				if (!(expression instanceof StringExpression)) {
					expression = new StringAdapter(expression);
				}
				break;
			}
			return expression;
		}
	}

	String getType() {
		if (retType != null) {
			return retType;
		}
		switch (type) {
		case BOOLEAN:
		case BOOLOBJ:
			return "Z";
		case INTEGER:
		case INTOBJ:
			return "I";
		case DOUBLE:
		case DBLOBJ:
			return "D";
		case STRING:
		case STROBJ:
			return "Ljava/lang/String;";
		case LIST:
			return "Ljava/util/List;";
		case MAP:
			return "Ljava/util/Map;";
		default:
			return "Ljava/lang/Object;";
		}
	}
}
