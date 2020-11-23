package com.roubsite.smarty4j.statement;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.SafeContext;
import com.roubsite.smarty4j.Template;
import com.roubsite.smarty4j.TemplateReader;
import com.roubsite.smarty4j.TemplateWriter;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.NullExpression;

/**
 * 自定义单行函数节点，单行函数是指不需要结束标签的函数，语句只占用一行， 在模板解析过程中， 将调用execute方法，如果不希望进行jvm字节码开发，
 * 开发人员应该继承自这个类来实现自己的行函数扩展节点。 如果需要向Context中写入数据，请参见Function的函数说明。
 * 
 * @see com.roubsite.smarty4j.statement.Function
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public abstract class LineFunction extends Function {

	private Template tpl;

	/**
	 * 获取赋值变量名
	 * 
	 * @return 赋值变量名
	 */
	public String getAssignName() {
		Definition[] definitions = getDefinitions();
		if (definitions != null) {
			for (int i = 0; i < definitions.length; i++) {
				if ("assign".equals(definitions[i].getName())) {
					return PARAMETERS[i] != NullExpression.VALUE ? PARAMETERS[i].toString() : null;
				}
			}
		}
		return null;
	}

	/**
	 * 返回值解析
	 * 
	 * @param mv
	 *            ASM方法访问对象
	 * @param local
	 *            ASM方法内部的语句栈局部变量起始位置
	 * @param vm
	 *            变量管理器
	 */
	public void parseReturn(MethodVisitorProxy mv, int local, VariableManager vm) {
		String name = getAssignName();
		if (name == null) {
			mv.visitInsn(POP);
		} else {
			writeVariable(mv, local, vm, name, null);
		}
	}

	@Override
	public void analyzeContent(Analyzer analyzer, TemplateReader reader) throws ParseException {
		super.analyzeContent(analyzer, reader);
		this.tpl = analyzer.getTemplate();
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		String name = getAssignName();
		if (name != null) {
			if (!vm.requiredRewrite() && vm.hasCached() && vm.getIndex(name) == VariableManager.NOEXIST) {
				return;
			}
		}

		Method method = getMethod("execute");
		String returnType = method.getReturnType() == void.class ? "V" : "Ljava/lang/Object;";
		int invokeType = Modifier.isStatic(method.getModifiers()) ? INVOKESTATIC : INVOKEVIRTUAL;

		String className;
		if (invokeType == INVOKESTATIC) {
			className = this.getClass().getName().replace('.', '/');
		} else {
			className = parseNode(mv, local, tpl.addNode(this));
		}

		mv.visitVarInsn(ALOAD, CONTEXT);
		mv.visitVarInsn(ALOAD, WRITER);
		parseAllParameters(mv, local, vm);		
		mv.visitMethodInsn(invokeType, className, "execute",
				"(L" + SafeContext.NAME + ";L" + TemplateWriter.NAME + ';' + getDesc() + ")" + returnType);
		if (returnType.length() > 1) {
			parseReturn(mv, local, vm);
		}
	}
}