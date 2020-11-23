package com.roubsite.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

public class MapExpression extends ObjectExpression {

	private Map<String, Expression> value;
	
	public MapExpression(Map<String, Expression> value) {
		this.value = value;
	}

	public Map<String, Expression> getValue() {
		return value;
	}
	
	@Override
	public void parseSelf(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitTypeInsn(NEW, "java/util/HashMap");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
		for (Map.Entry<String, Expression> entry : value.entrySet()) {
			mv.visitInsn(DUP);
			mv.visitLdcInsn(entry.getKey());
			entry.getValue().parseObject(mv, local, vm);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
			mv.visitInsn(POP);
		}
	}
}
