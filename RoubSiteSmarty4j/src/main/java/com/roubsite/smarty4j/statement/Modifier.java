package com.roubsite.smarty4j.statement;

import static org.objectweb.asm.Opcodes.*;

import java.util.List;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.Template;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;

/**
 * 自定义变量调节器节点，它提供了一个高级的调用<br>
 * execute(Object, Object[])<br>
 * 方法，第一个参数是需要被调节的变量，第二个参数是变量调节器的参数组，
 * 如果不希望进行jvm字节码开发，开发人员应该继承自这个类来实现自己的变量调节器扩展节点。
 * 
 * @see com.roubsite.smarty4j.Template#addModifier(IModifier)
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public abstract class Modifier extends Parameter {

	/** ASM名称 */
	public static final String NAME = Modifier.class.getName().replace('.', '/');

	private Template tpl;

	/**
	 * 变量调节器初始化，读取并识别模板文件中变量调节器的参数。
	 * 
	 * @param tpl
	 *            用于保存变量调节器对象的模板
	 * @param ransack
	 *            变量调节器需要递归处理设置为<tt>true</tt>
	 * @param values
	 *            模板引擎解析得到的当前变量调节器的参数值
	 * @throws ParseException
	 *             参数不合法
	 * @see com.roubsite.smarty4j.Template#addNode
	 */
	public void createParameters(Template tpl, List<Expression> values) throws ParseException {
		Definition[] definitions = getDefinitions();
		if (definitions != null) {
			int len = definitions.length;
			Expression[] parameters = new Expression[len];

			for (int i = 0; i < len; i++) {
				parameters[i] = definitions[i].getExpression(i < values.size() ? values.get(i) : null,
						"The " + (i + 1) + "th parameter");
			}

			PARAMETERS = parameters;
		}

		this.tpl = tpl;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		int invokeType = java.lang.reflect.Modifier.isStatic(getMethod("execute").getModifiers()) ? INVOKESTATIC
				: INVOKEVIRTUAL;
		String className;
		if (invokeType == INVOKESTATIC) {
			className = this.getClass().getName().replace('.', '/');
		} else {
			className = parseNode(mv, local, tpl.addNode(this));
			mv.visitInsn(SWAP);
		}

		parseAllParameters(mv, local, vm);
		mv.visitMethodInsn(invokeType, className, "execute", "(Ljava/lang/Object;" + getDesc() + ")Ljava/lang/Object;");
	}
}