package com.roubsite.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP2;
import static org.objectweb.asm.Opcodes.DUP2_X2;
import static org.objectweb.asm.Opcodes.DUP_X1;
import static org.objectweb.asm.Opcodes.DUP_X2;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IADD;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFLE;
import static org.objectweb.asm.Opcodes.IFLT;
import static org.objectweb.asm.Opcodes.IF_ICMPLT;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.IMUL;
import static org.objectweb.asm.Opcodes.INEG;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.POP2;
import static org.objectweb.asm.Opcodes.SWAP;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.Context;
import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.Operator;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.TemplateReader;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.VariableExpression;
import com.roubsite.smarty4j.expression.number.ConstInteger;
import com.roubsite.smarty4j.statement.Block;
import com.roubsite.smarty4j.statement.LoopFunction;
import com.roubsite.smarty4j.util.SimpleStack;

/**
 * The tag is used to create simple loops.<br>
 * 
 * The following different formarts are supported:
 *
 * <code>
 * {for $var=$start to $end} simple loop with step size of 1.
 * {for $var=$start to $end step $step} loop with individual step size.
 * </code>
 * 
 * {forelse} is executed when the loop is not iterated.
 * 
 * <table border="1">
 * <colgroup> <col align="center" class="param"> <col align="center" class="position"> <col
 * align="center" class="type"> <col align="center" class="required"> <col align="center"
 * class="default"> <col class="desc"> </colgroup> <thead>
 * <tr>
 * <th align="center">Attribute Name</th>
 * <th align="center">Shorthand</th>
 * <th align="center">Type</th>
 * <th align="center">Required</th>
 * <th align="center">Default</th>
 * <th>Description</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td align="center">max</td>
 * <td align="center">n/a</td>
 * <td align="center">integer</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>Limit the number of iterations</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $for extends LoopFunction {

	private String name;
	private Expression start;
	private Expression to;
	private Expression step;
	private Expression max;
	private Block blkElse;

	@Override
	public void restore(MethodVisitorProxy mv, VariableManager vm) {
		mv.visitInsn(POP2);
		if (name != null) {
			if (!vm.hasCached()) {
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
				    "(Ljava/lang/String;Ljava/lang/Object;)V");
			}
		}
	}

	@Override
	public void addStatement(Node child) throws ParseException {
		if (child instanceof $forelse) {
			if (blkElse != null) {
				throw new ParseException(String.format(MessageFormat.IS_ALREADY_DEFINED, "\"forelse\""));
			} else {
				blkElse = new Block();
				blkElse.setParent(this.getParent());
			}
		} else if (blkElse != null) {
			blkElse.addStatement(child);
		} else {
			super.addStatement(child);
		}
	}

	@Override
	public void syntax(Analyzer analyzer, SimpleStack tokens) throws ParseException {
		int size = tokens.size();
		if (size > 2 && tokens.get(1) instanceof VariableExpression) {
			VariableExpression exp = (VariableExpression) tokens.get(1);
			if (!exp.hasExtended() && tokens.get(2) == Operator.SET) {
				name = exp.getName();
				int i = 3;
				for (int j = 3; j < size; j++) {
					Object token = tokens.get(j);
					if ("to".equals(token)) {
						start = Operator.merge(tokens, i, j, Operator.INTEGER);
						j++;
						i = j;
					} else if ("step".equals(token)) {
						if (start == null) {
							throw new ParseException(String.format(MessageFormat.NOT_CORRECT, "Parameter format"));
						}
						to = Operator.merge(tokens, i, j, Operator.INTEGER);
						j++;
						i = j;
					} else if ("max".equals(token)) {
						if (start == null || j + 2 >= size || tokens.get(j + 1) != Operator.SET) {
							throw new ParseException(String.format(MessageFormat.NOT_CORRECT, "Parameter format"));
						}
						if (to == null) {
							to = Operator.merge(tokens, i, j, Operator.INTEGER);
							step = new ConstInteger(1);
						} else {
							step = Operator.merge(tokens, i, j, Operator.INTEGER);
						}
						max = Operator.merge(tokens, j + 2, size, Operator.INTEGER);
						return;
					}
				}
				if (start != null) {
					if (to == null) {
						to = Operator.merge(tokens, i, size, Operator.INTEGER);
						step = new ConstInteger(1);
					} else if (step == null) {
						step = Operator.merge(tokens, i, size, Operator.INTEGER);
					}
					return;
				}
			}
		}
		throw new ParseException(String.format(MessageFormat.NOT_CORRECT, "Parameter format"));
	}

	@Override
	public void analyzeContent(Analyzer analyzer, TemplateReader reader) {
		VariableManager vm = analyzer.getVariableManager();
		VariableManager cvm = new VariableManager(analyzer.getTemplate().getEngine());
		analyzer.setVariableManager(cvm);
		super.analyzeContent(analyzer, reader);
		if (cvm.hasCached()) {
			if (cvm.getIndex(name) == VariableManager.NOEXIST) {
				name = null;
			}
		}
		vm.merge(cvm, name);
		analyzer.setVariableManager(vm);
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {

		int CURRNAME = local;
		int STEP = local + 1;
		int NEG_FLAG = local + 2;
		int LOCAL = local + 3;

		Integer oldNameValue = null;

		Label forelse = new Label();
		Label forelse_pop1 = new Label();
		Label forelse_pop2 = new Label();
		Label step_neg = new Label();
		Label no_neg_flag = new Label();
		Label loop_init = new Label();
		Label loop_start = new Label();
		Label loop_end = new Label();

		if (max != null) {
			max.parseInteger(mv, LOCAL, vm);
			mv.visitInsn(DUP);
			mv.visitJumpInsn(IFLE, forelse_pop1);
		}
		step.parseInteger(mv, LOCAL, vm);
		mv.visitInsn(DUP);
		mv.visitJumpInsn(IFEQ, max != null ? forelse_pop2 : forelse_pop1);
		if (max != null) {
			mv.visitInsn(DUP_X1);
			mv.visitInsn(IMUL);
			start.parseInteger(mv, LOCAL, vm);
			mv.visitInsn(DUP_X2);
			mv.visitInsn(IADD);
			mv.visitInsn(SWAP);
		}
		mv.visitInsn(DUP);
		mv.visitJumpInsn(IFLT, step_neg);

		if (max != null) {
			mv.visitVarInsn(ISTORE, STEP);
			mv.visitLdcInsn(-1);
			mv.visitInsn(IADD);
			to.parseInteger(mv, LOCAL, vm);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "min", "(II)I");
			mv.visitInsn(SWAP);
		} else {
			mv.visitVarInsn(ISTORE, STEP);
			to.parseInteger(mv, LOCAL, vm);
			start.parseInteger(mv, LOCAL, vm);
		}
		if (name != null) {
			mv.visitLdcInsn(false);
		}
		mv.visitJumpInsn(GOTO, loop_init);

		mv.visitLabel(step_neg);
		mv.visitInsn(INEG);
		if (max != null) {
			mv.visitVarInsn(ISTORE, STEP);
			mv.visitInsn(INEG);
			mv.visitLdcInsn(-1);
			mv.visitInsn(IADD);
			to.parseInteger(mv, LOCAL, vm);
			mv.visitInsn(INEG);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "min", "(II)I");
			mv.visitInsn(SWAP);
			mv.visitInsn(INEG);
		} else {
			mv.visitVarInsn(ISTORE, STEP);
			to.parseInteger(mv, LOCAL, vm);
			mv.visitInsn(INEG);
			start.parseInteger(mv, LOCAL, vm);
			mv.visitInsn(INEG);
		}
		if (name != null) {
			mv.visitLdcInsn(true);
		}

		mv.visitLabel(loop_init);
		if (name != null) {
			mv.visitVarInsn(ISTORE, NEG_FLAG);
		}
		mv.visitInsn(DUP2);
		mv.visitJumpInsn(IF_ICMPLT, forelse_pop2);

		if (name != null) {
			if (vm.hasCached()) {
				oldNameValue = vm.getIndex(name);
				vm.setIndex(name, CURRNAME);
			} else {
				// 保存原始的循环变量名值
				mv.visitVarInsn(ALOAD, CONTEXT);
				mv.visitLdcInsn(name);
				mv.visitInsn(DUP2_X2);
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get",
				    "(Ljava/lang/String;)Ljava/lang/Object;");
				mv.visitInsn(DUP_X2);
				mv.visitInsn(POP);
			}
		}

		mv.visitLabel(loop_start);
		if (name != null) {
			mv.visitInsn(DUP);
			mv.visitVarInsn(ILOAD, NEG_FLAG);
			mv.visitJumpInsn(IFEQ, no_neg_flag);
			mv.visitInsn(INEG);
			mv.visitLabel(no_neg_flag);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
			writeVariable(mv, LOCAL, vm, name, null);
		}

		super.parse(mv, LOCAL, vm);
		mv.visitLabel(lblContinue);
		mv.visitVarInsn(ILOAD, STEP);
		mv.visitInsn(IADD);
		mv.visitInsn(DUP2);
		mv.visitJumpInsn(IF_ICMPLT, lblBreak);
		mv.visitJumpInsn(GOTO, loop_start);

		// 循环结束, 恢复恢复过程中被设置的属性的原始值
		mv.visitLabel(lblBreak);
		restore(mv, vm);
		mv.visitJumpInsn(GOTO, loop_end);

		// 循环源集合为空时的处理
		mv.visitLabel(forelse_pop2);
		mv.visitInsn(POP);

		mv.visitLabel(forelse_pop1);
		mv.visitInsn(POP);

		mv.visitLabel(forelse);
		if (blkElse != null) {
			blkElse.parse(mv, LOCAL, vm);
		}

		mv.visitLabel(loop_end);

		if (name != null) {
			if (vm.hasCached()) {
				vm.setIndex(name, oldNameValue);
			}
		}
	}
}