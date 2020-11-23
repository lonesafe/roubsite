package com.roubsite.smarty4j.statement;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.VariableExpression;

public class SetStatement extends Node {

	private VariableExpression var;
	private Expression value;

	public SetStatement(VariableExpression var, Expression value) {
		this.var = var;
		this.value = value;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		value.parseObject(mv, local, vm);
		var.parseSet(mv, local, vm);
	}
}
