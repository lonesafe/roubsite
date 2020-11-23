package com.roubsite.smarty4j.statement.function;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Operator;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.statement.Function;
import com.roubsite.smarty4j.statement.ParentType;
import com.roubsite.smarty4j.util.SimpleStack;

/**
 * @see com.roubsite.smarty4j.statement.function.$if
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
@ParentType(name = "if")
public class $elseif extends Function {

	/** 条件表达式 */
	private Expression check;

	/**
	 * 获取条件表达式
	 * 
	 * @return 条件表达式
	 */
	public Expression getCheckExpression() {
		return check;
	}

	@Override
	public void syntax(Analyzer analyzer, SimpleStack tokens) throws ParseException {
		if (tokens.size() == 1) {
			throw new ParseException(String.format(MessageFormat.NOT_CORRECT, "参数格式"));
		}
		check = Operator.merge(tokens, 1, tokens.size(), Operator.FLOAT | Operator.BOOLEAN);
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
	}
}
