package com.roubsite.smarty4j.statement;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP2;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.SWAP;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.SafeContext;
import com.roubsite.smarty4j.Template;
import com.roubsite.smarty4j.TemplateReader;
import com.roubsite.smarty4j.TemplateWriter;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.util.NullWriter;

/**
 * 自定义区块函数，区块函数指的是函数内部包含其它函数或文本，所以必须拥有结束标签的函数，
 * 区块函数在编译过程中将会在解析内部函数或文本之前调用start方法，
 * 在解析完内部函数或文本之后将调用end方法，在模板分析过程中，系统首先初始化函数节点， 然后解析函数的参数，然后设置函数的父函数，最后解析函数的内部数据。
 * 如果不希望进行jvm字节码开发，开发人员应该继承自这个类来实现自己的区块函数扩展节点。
 * 如果需要向Context中写入数据，请参见Function的函数说明。
 * 
 * @see com.roubsite.smarty4j.statement.Function
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public abstract class BlockFunction extends Block {

	/** 空输出 */
	protected static final TemplateWriter NULL = new TemplateWriter(new NullWriter());

	private Template tpl;

	@Override
	public void analyzeContent(Analyzer analyzer, TemplateReader reader) {
		super.analyzeContent(analyzer, reader);
		this.tpl = analyzer.getTemplate();
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		Method method = getMethod("start");
		String returnType;
		boolean hasEnd;

		if (method.getReturnType() != void.class) {
			returnType = "L" + TemplateWriter.NAME + ";";
			mv.visitVarInsn(ALOAD, WRITER);
		} else {
			returnType = "V";
		}

		try {
			if (returnType.length() > 1) {
				this.getClass().getMethod("end", SafeContext.class, TemplateWriter.class, TemplateWriter.class);
			} else {
				this.getClass().getMethod("end", SafeContext.class, TemplateWriter.class);
			}
			hasEnd = true;
		} catch (Exception ex) {
			hasEnd = false;
		}

		int invokeType = Modifier.isStatic(method.getModifiers()) ? INVOKESTATIC : INVOKEVIRTUAL;
		String className;
		if (invokeType == INVOKESTATIC) {
			className = this.getClass().getName().replace('.', '/');
			mv.visitVarInsn(ALOAD, CONTEXT);
			if (hasEnd) {
				mv.visitInsn(DUP);
			}
		} else {
			className = parseNode(mv, local, tpl.addNode(this));
			mv.visitVarInsn(ALOAD, CONTEXT);
			if (hasEnd) {
				mv.visitInsn(DUP2);
			}
		}

		mv.visitVarInsn(ALOAD, WRITER);
		parseAllParameters(mv, local, vm);
		mv.visitMethodInsn(invokeType, className, "start",
				"(L" + SafeContext.NAME + ";L" + TemplateWriter.NAME + ';' + getDesc() + ")" + returnType);

		if (returnType.length() > 1) {
			if (hasEnd) {
				mv.visitVarInsn(ALOAD, WRITER);
				mv.visitInsn(SWAP);
			}
			mv.visitVarInsn(ASTORE, WRITER);
		}

		super.parse(mv, local, vm);

		if (hasEnd) {
			mv.visitVarInsn(ALOAD, WRITER);
			if (returnType.length() > 1) {
				mv.visitMethodInsn(invokeType, className, "end",
						"(L" + SafeContext.NAME + ";L" + TemplateWriter.NAME + ";L" + TemplateWriter.NAME + ";)V");
			} else {
				mv.visitMethodInsn(invokeType, className, "end",
						"(L" + SafeContext.NAME + ";L" + TemplateWriter.NAME + ";)V");
			}
		}

		if (returnType.length() > 1) {
			mv.visitVarInsn(ASTORE, WRITER);
		}
	}
}