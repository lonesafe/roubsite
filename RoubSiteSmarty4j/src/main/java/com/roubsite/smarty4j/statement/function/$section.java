package com.roubsite.smarty4j.statement.function;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

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
import com.roubsite.smarty4j.expression.number.ConstInteger;
import com.roubsite.smarty4j.statement.Block;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.LoopFunction;

/**
 * The tag is for looping over sequentially indexed arrays of data, unlike {foreach} which is used
 * to loop over a single associative array. Every {section} tag must be paired with a closing
 * {/section} tag.<br>
 * 
 * {sectionelse} is executed when there are no values in the loop variable.<br>
 * A {section} also has its own variables that handle {section} properties. These properties are
 * accessible as: {$smarty.section.name.property} where “name” is the attribute name.<br>
 * {section} properties are index, index_prev, index_next, iteration, first, last, rownum, loop,
 * show, total.<br>
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
 * <td>The name of the section</td>
 * </tr>
 * <tr>
 * <td align="center">loop</td>
 * <td align="center">mixed</td>
 * <td align="center">Yes</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>Value to determine the number of loop iterations</td>
 * </tr>
 * <tr>
 * <td align="center">start</td>
 * <td align="center">integer</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>0</em></span></td>
 * <td>The index position that the section will begin looping. If the value is negative, the start
 * position is calculated from the end of the array. For example, if there are seven values in the
 * loop array and start is -2, the start index is 5. Invalid values (values outside of the length of
 * the loop array) are automatically truncated to the closest valid value.</td>
 * </tr>
 * <tr>
 * <td align="center">step</td>
 * <td align="center">integer</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>1</em></span></td>
 * <td>The step value that will be used to traverse the loop array. For example, step=2 will loop on
 * index 0,2,4, etc. If step is negative, it will step through the array backwards.</td>
 * </tr>
 * <tr>
 * <td align="center">max</td>
 * <td align="center">integer</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>Sets the maximum number of times the section will loop.</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $section extends LoopFunction {

	public static class Bean {

		public boolean first = true;
		public boolean last;
		public int index;
		public int total;
		public int iteration;
		public int index_prev;
		public int index_next;

		private int step;
		private int max;

		public int next() {
			if (index_next == -1) {
				return -1;
			}

			if (iteration == 0) {
				index_prev = -1;
			} else {
				index_prev = index;
				first = false;
			}
			iteration++;

			index = index_next;
			int nextIndex = index + step;
			if (iteration < max && nextIndex >= 0 && nextIndex < total) {
				index_next = nextIndex;
				last = false;
			} else {
				index_next = -1;
				last = true;
			}

			return index;
		}
	}

	/** ASM名称 */
	public static final String NAME = $section.class.getName().replace('.', '/');

	/** 参数定义 */
	private static final Definition[] definitions = {
	    Definition.forFunction("loop", Type.OBJECT),
	    Definition.forFunction("name", Type.STRING),
	    Definition.forFunction("start", Type.INTOBJ, ConstInteger.ZERO),
	    Definition.forFunction("step", Type.INTOBJ, new ConstInteger(1)),
	    Definition.forFunction("max", Type.INTOBJ, new ConstInteger(Integer.MAX_VALUE)) };

	public static Bean init(Object o, int start, int step, int max, Context ctx, String name) {
		int total;
		if (o instanceof List) {
			total = ((List<?>) o).size();
		} else if (o instanceof Object[]) {
			total = ((Object[]) o).length;
		} else if (o instanceof Integer) {
			total = (Integer) o;
		} else {
			return null;
		}
		if (start < 0) {
			start = total + start;
		}
		if (step == 0 || max <= 0 || start < 0 || start >= total) {
			return null;
		}

		Bean bean = new Bean();
		bean.total = total;
		bean.index_next = start;
		bean.step = step;
		bean.max = max;
		ctx.getSection().put(name, bean);
		return bean;
	}

	/** 循环体为空时对应的区块 */
	private String name;
	private Block blkElse;

	@Override
	public void restore(MethodVisitorProxy mv, VariableManager vm) {
		mv.visitInsn(POP);
		if (PARAMETERS[1] != NullExpression.VALUE) {
			if (!vm.hasCached()) {
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
				    "(Ljava/lang/String;Ljava/lang/Object;)V");
			}
		}
	}

	@Override
	public void addStatement(Node statement) throws ParseException {
		if (statement instanceof $sectionelse) {
			if (blkElse != null) {
				throw new ParseException(String.format(MessageFormat.IS_ALREADY_DEFINED, "\"sectionelse\""));
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
		String name = PARAMETERS[1].toString();
		this.name = name;
		VariableManager vm = analyzer.getVariableManager();
		VariableManager cvm = new VariableManager(analyzer.getTemplate().getEngine());
		analyzer.setVariableManager(cvm);
		super.analyzeContent(analyzer, reader);
		if (cvm.hasCached()) {
			if (cvm.getIndex(name) == VariableManager.NOEXIST) {
				PARAMETERS[1] = NullExpression.VALUE;
			}
		}
		vm.merge(cvm, name);
		analyzer.setVariableManager(vm);
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {

		int CURRNAME = local;
		int INDEX = local + 1;
		int LOCAL = local + 2;

		Integer oldNameValue = null;

		Expression name = PARAMETERS[1];

		Label sectionelse_pop1 = new Label();
		Label loop_start = new Label();
		Label loop_end = new Label();

		PARAMETERS[0].parseObject(mv, LOCAL, vm);
		PARAMETERS[2].parseInteger(mv, LOCAL, vm);
		PARAMETERS[3].parseInteger(mv, LOCAL, vm);
		PARAMETERS[4].parseInteger(mv, LOCAL, vm);
		mv.visitVarInsn(ALOAD, CONTEXT);
		mv.visitLdcInsn(this.name);
		mv.visitMethodInsn(INVOKESTATIC, NAME, "init", "(Ljava/lang/Object;IIIL" + Context.NAME
		    + ";Ljava/lang/String;)L" + NAME + "$Bean;");
		mv.visitInsn(DUP);
		mv.visitJumpInsn(IFNULL, sectionelse_pop1);

		if (name != NullExpression.VALUE) {
			if (vm.hasCached()) {
				oldNameValue = vm.getIndex(name.toString());
				vm.setIndex(name.toString(), CURRNAME);
			} else {
				// 保存原始的循环变量名值
				mv.visitVarInsn(ALOAD, CONTEXT);
				name.parse(mv, LOCAL, vm);
				mv.visitInsn(DUP2_X1);
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get",
				    "(Ljava/lang/String;)Ljava/lang/Object;");
				mv.visitInsn(SWAP);
			}
		}

		mv.visitLabel(loop_start);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKEVIRTUAL, NAME + "$Bean", "next", "()I");
		mv.visitInsn(DUP);
		mv.visitVarInsn(ISTORE, INDEX);
		mv.visitJumpInsn(IFLT, lblBreak);

		if (name != NullExpression.VALUE) {
			writeVariable(mv, LOCAL, vm, name.toString(), new VariableLoader() {
				public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
					mv.visitVarInsn(ILOAD, local - 1);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				}
			});
		}

		super.parse(mv, LOCAL, vm);
		mv.visitLabel(lblContinue);
		mv.visitJumpInsn(GOTO, loop_start);

		// 循环结束, 恢复恢复过程中被设置的属性的原始值
		mv.visitLabel(lblBreak);
		restore(mv, vm);
		mv.visitJumpInsn(GOTO, loop_end);

		// 循环源集合为空时的处理
		mv.visitLabel(sectionelse_pop1);
		mv.visitInsn(POP);
		if (blkElse != null) {
			blkElse.parse(mv, LOCAL, vm);
		}

		mv.visitLabel(loop_end);

		if (name != NullExpression.VALUE) {
			if (vm.hasCached()) {
				vm.setIndex(name.toString(), oldNameValue);
			}
		}
	}
}