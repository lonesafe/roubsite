package com.roubsite.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.Context;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.TemplateReader;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.statement.Block;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;

/**
 * The tag is used to create functions within a template and call them just like a plugin function.
 * Instead of writing a plugin that generates presentational content, keeping it in the template is
 * often a more manageable choice. It also simplifies data traversal, such as deeply nested menus.
 *
 * <table border="1">
 * <colgroup> <col align="center" class="param"> <col align="center" class="type"> <col
 * align="center" class="required"> <col align="center" class="default"> <col class="desc">
 * </colgroup> <thead>
 * <tr>
 * <th align="center">Attribute Name</th>
 * <th align="center">Type</th>
 * <th align="center">Required</th>
 * <th align="center">Default</th>
 * <th>Description</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td align="center">name</td>
 * <td align="center">string</td>
 * <td align="center">Yes</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>The name of the template function</td>
 * </tr>
 * <tr>
 * <td align="center">[var ...]</td>
 * <td align="center">[var type]</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>default variable value to pass local to the template function</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $function extends Block {

	private static class Default extends Node {

		private String name;
		private Expression value;

		private Default(String name, Expression value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
			writeVariable(mv, local, vm, name, new VariableLoader() {
				public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
					Label end = new Label();
					if (vm.hasCached()) {
						mv.visitVarInsn(ALOAD, vm.getIndex(name));
					} else {
						mv.visitVarInsn(ALOAD, CONTEXT);
						mv.visitLdcInsn(name);
						mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get",
						    "(Ljava/lang/String;)Ljava/lang/Object;");
					}
					mv.visitInsn(DUP);
					mv.visitJumpInsn(IFNONNULL, end);
					mv.visitInsn(POP);
					value.parseObject(mv, local, vm);
					mv.visitLabel(end);
				}
			});
		}
	}

	private static final Definition[] definitions = { Definition.forFunction("name", Type.STRING) };

	@Override
	public boolean setParent(Block parent) {
		return false;
	}

	@Override
	public void createParameters(Definition[] parameters, Map<String, Expression> fields)
	    throws ParseException {
		super.createParameters(parameters, fields);
		fields.remove("name");
		for (Entry<String, Expression> entry : fields.entrySet()) {
			addStatement(new Default(entry.getKey(), entry.getValue()));
		}
	}

	@Override
	public void analyzeContent(Analyzer analyzer, TemplateReader reader) {
		VariableManager vm = analyzer.getVariableManager();
		VariableManager cvm = new VariableManager(null);
		analyzer.setVariableManager(cvm);
		super.analyzeContent(analyzer, reader);
		analyzer.setVariableManager(vm);
		analyzer.getTemplate().addFunction(PARAMETERS[0].toString(), this, cvm);
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}