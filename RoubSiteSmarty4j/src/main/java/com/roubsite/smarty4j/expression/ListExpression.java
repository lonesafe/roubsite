package com.roubsite.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import java.util.List;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

public class ListExpression extends ObjectExpression {

	private List<Expression> value;
	
	public ListExpression(List<Expression> value) {
		this.value = value;
	}

	@Override
	public void parseSelf(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitTypeInsn(NEW, "java/util/ArrayList");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
		for (Expression entry : value) {
			mv.visitInsn(DUP);
			entry.parseObject(mv, local, vm);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");
			mv.visitInsn(POP);
		}
	}
}
