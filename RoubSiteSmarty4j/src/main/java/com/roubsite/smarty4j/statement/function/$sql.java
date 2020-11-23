package com.roubsite.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.statement.Block;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;

public class $sql extends Block {

	private static final Definition[] definitions = { Definition.forFunction("dataSource", Type.STRING),
			Definition.forFunction("sql", Type.STRING), Definition.forFunction("var", Type.STRING) };

	@Override
	public void addStatement(Node child) throws ParseException {
		super.addStatement(child);
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		String dataSource = PARAMETERS[0].toString();
		String sql = PARAMETERS[1].toString();
		String varName = PARAMETERS[2].toString();
		mv.visitLdcInsn(dataSource);
		mv.visitLdcInsn(sql);
		mv.visitMethodInsn(INVOKESTATIC, "com/roubsite/smarty4j/util/DBUtil", "execQuery",
				"(Ljava/lang/String;Ljava/lang/String;)Ljava/util/List;");
		this.writeVariable(mv, local, vm, varName, null);
		mv.visitEnd();
		super.parse(mv, local, vm);
	}
}
