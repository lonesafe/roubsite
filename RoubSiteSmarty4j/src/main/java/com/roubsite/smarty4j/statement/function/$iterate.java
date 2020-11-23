package com.roubsite.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.Context;
import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.TemplateReader;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.NullExpression;
import com.roubsite.smarty4j.statement.Block;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.LoopFunction;

/**
 * 迭代函数。
 * 
 * <pre>
 * item--循环内的变量名称
 * from--循环体
 * index--循环索引变量名称
 * </pre>
 * 
 * <b>Syntax:</b>
 * 
 * <pre>
 * {iterate item="String" from=[Object] index="String"}
 * ...
 * {iterateelse}
 * ...
 * {/iterate}
 * </pre>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $iterate extends LoopFunction {

	/** ASM名称 */
	public static final String NAME = $iterate.class.getName().replace('.', '/');

	/** 参数定义 */
	private static final Definition[] definitions = {
	    Definition.forFunction("from", Type.OBJECT),
	    Definition.forFunction("item", Type.STRING),
	    Definition.forFunction("index", Type.STRING, NullExpression.VALUE) };

	/**
	 * 获取一个循环体源对象包含的对象数组, 如果源对象是Map, 将取回关键字对应的数组, 如果无法将源对象转换成等价的对象数组, 源对象将直接被返回
	 * 
	 * @param o
	 *          需要循环的源对象
	 * @return 源对象数组
	 */
	public static Object[] getLooper(Object o) {
		if (o == null) {
			return null;
		}
		Object[] ret;
		if (o instanceof List) {
			ret = ((List<?>) o).toArray();
		} else if (o instanceof Object[]) {
			ret = (Object[]) o;
		} else if (o instanceof Map) {
			ret = ((Map<?, ?>) o).values().toArray();
		} else {
			ret = new Object[] { o };
		}
		if (ret.length == 0) {
			return null;
		}
		return ret;
	}

	/** 循环体为空时对应的区块 */
	private Block blkElse;

	@Override
	public void restore(MethodVisitorProxy mv, VariableManager vm) {
		mv.visitInsn(POP2);
		if (!vm.hasCached()) {
			if (PARAMETERS[2] != NullExpression.VALUE) {
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
				    "(Ljava/lang/String;Ljava/lang/Object;)V");
			}
			if (PARAMETERS[1] != NullExpression.VALUE) {
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
				    "(Ljava/lang/String;Ljava/lang/Object;)V");
			}
		}
	}

	@Override
	public void addStatement(Node statement) throws ParseException {
		if (statement instanceof $sectionelse) {
			if (blkElse != null) {
				throw new ParseException(String.format(MessageFormat.IS_ALREADY_DEFINED, "\"iterateelse\""));
			} else {
				blkElse = new Block();
				blkElse.setParent(this.getParent());
			}
		} else if (blkElse != null) {
			blkElse.addStatement(statement);
		} else {
			super.addStatement(statement);
		}
	}

	@Override
	public void analyzeContent(Analyzer analyzer, TemplateReader reader) {
		String item = PARAMETERS[1] != NullExpression.VALUE ? PARAMETERS[1].toString() : null;
		String index = PARAMETERS[2] != NullExpression.VALUE ? PARAMETERS[2].toString() : null;
		VariableManager vm = analyzer.getVariableManager();
		VariableManager cvm = new VariableManager(analyzer.getTemplate().getEngine());
		analyzer.setVariableManager(cvm);
		super.analyzeContent(analyzer, reader);
		if (cvm.hasCached()) {
			if (cvm.getIndex(item) == VariableManager.NOEXIST) {
				PARAMETERS[1] = NullExpression.VALUE;
			}
			if (cvm.getIndex(index) == VariableManager.NOEXIST) {
				PARAMETERS[2] = NullExpression.VALUE;
			}
		}
		vm.merge(cvm, item, index);
		analyzer.setVariableManager(vm);
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {

		final int CURRITEM = local;
		final int CURRINDEX = local + 1;
		final int TOTAL = local + 2;
		final int LOCAL = local + 3;

		Integer oldItemValue = null;
		Integer oldIndexValue = null;

		Expression item = PARAMETERS[1];
		Expression index = PARAMETERS[2];

		Label isnull = new Label();
		Label loopstart = new Label();
		Label end = new Label();

		PARAMETERS[0].parseObject(mv, LOCAL, vm);
		mv.visitMethodInsn(INVOKESTATIC, NAME, "getLooper", "(Ljava/lang/Object;)[Ljava/lang/Object;");
		mv.visitInsn(DUP);
		mv.visitVarInsn(ASTORE, TOTAL);
		mv.visitJumpInsn(IFNULL, isnull);

		if (item != NullExpression.VALUE) {
			if (vm.hasCached()) {
				oldItemValue = vm.getIndex(item.toString());
				vm.setIndex(item.toString(), CURRITEM);
			} else {
				// 保存原始的循环变量名值
				mv.visitVarInsn(ALOAD, CONTEXT);
				item.parse(mv, LOCAL, vm);
				mv.visitInsn(DUP2);
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get",
				    "(Ljava/lang/String;)Ljava/lang/Object;");
			}
		}
		if (index != NullExpression.VALUE) {
			if (vm.hasCached()) {
				oldIndexValue = vm.getIndex(index.toString());
				vm.setIndex(index.toString(), CURRINDEX);
			} else {
				// 保存原始的循环索引名值
				mv.visitVarInsn(ALOAD, CONTEXT);
				index.parse(mv, LOCAL, vm);
				mv.visitInsn(DUP2);
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get",
				    "(Ljava/lang/String;)Ljava/lang/Object;");
			}
		}

		// 生成用于循环的数组
		mv.visitVarInsn(ALOAD, TOTAL);
		mv.visitInsn(DUP);
		mv.visitInsn(ARRAYLENGTH);
		mv.visitVarInsn(ISTORE, TOTAL);
		mv.visitLdcInsn(0);

		mv.visitLabel(loopstart);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ILOAD, TOTAL);
		mv.visitJumpInsn(IF_ICMPEQ, lblBreak);

		if (item != NullExpression.VALUE) {
			mv.visitInsn(DUP2);
			mv.visitInsn(AALOAD);
			writeVariable(mv, LOCAL, vm, item.toString(), null);
		}
		if (index != NullExpression.VALUE) {
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
			writeVariable(mv, LOCAL, vm, index.toString(), null);
		}
		super.parse(mv, LOCAL, vm);
		mv.visitLabel(lblContinue);
		mv.visitLdcInsn(1);
		mv.visitInsn(IADD);
		mv.visitJumpInsn(GOTO, loopstart);

		// 循环结束, 恢复恢复过程中被设置的属性的原始值
		mv.visitLabel(lblBreak);
		restore(mv, vm);
		mv.visitJumpInsn(GOTO, end);

		// 循环源集合为空时的处理
		mv.visitLabel(isnull);
		if (blkElse != null) {
			blkElse.parse(mv, LOCAL, vm);
		}

		mv.visitLabel(end);

		if (vm.hasCached()) {
			if (item != NullExpression.VALUE) {
				vm.setIndex(item.toString(), oldItemValue);
			}
			if (index != NullExpression.VALUE) {
				vm.setIndex(index.toString(), oldIndexValue);
			}
		}
	}
}