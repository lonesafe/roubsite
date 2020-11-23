package com.roubsite.smarty4j.expression;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

public class VoidExpression extends Expression {

	public static final VoidExpression VALUE = new VoidExpression();  
	
	private VoidExpression() {
	}
	
	@Override
	public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue,
	    Label lblFalse) {
	}

	@Override
	public void parseInteger(MethodVisitorProxy mv, int local, VariableManager vm) {
	}

	@Override
	public void parseDouble(MethodVisitorProxy mv, int local, VariableManager vm) {
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
