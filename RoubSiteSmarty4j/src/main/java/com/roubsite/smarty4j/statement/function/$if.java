package com.roubsite.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFEQ;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.Operator;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.statement.Block;
import com.roubsite.smarty4j.util.SimpleStack;

/**
 * The tag statements in Smarty have much the same flexibility as PHP if statements, with a few
 * added features for the template engine. Every {if} must be paired with a matching {/if}. {else}
 * and {elseif} are also permitted.
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $if extends Block {

	/**
	 * 单个的if/elseif条件分支结构块类
	 */
	private class Branch {

		/** 分支结构块中的条件表达式 */
		private Expression check;

		/** 分支结构块中的区块语句 */
		private Block block = new Block();

		/**
		 * 分支结构块构造函数
		 * 
		 * @param check
		 *          结构块条件, 如果check为NULL, 表示else块
		 * @param block
		 *          区块语句
		 */
		private Branch(Expression check) {
			this.check = check;
		}
	}

	/** if/elseif条件分支结构块 */
	private List<Branch> branchs = new ArrayList<Branch>();

	/** else结构块 */
	private Block blkElse;

	/** 分支结构块 */
	private Block now;

	/**
	 * 增加一个分支结构块
	 * 
	 * @param check
	 *          分支结构块的逻辑表达式
	 * @throws ParseException
	 *           设置父对象异常
	 */
	private void addBranch(Expression check) throws ParseException {
		Branch branch = new Branch(check);
		branch.block.setParent(this);
		now = branch.block;
		branchs.add(branch);
	}

	@Override
	public void addStatement(Node statement) throws ParseException {
		if (statement instanceof $elseif) {
			if (blkElse != null) {
				throw new ParseException(String.format(MessageFormat.NOT_CORRECT, "\"elseif\""));
			}
			addBranch((($elseif) statement).getCheckExpression());
		} else if (statement instanceof $else) {
			if (blkElse != null) {
				throw new ParseException(String.format(MessageFormat.IS_ALREADY_DEFINED, "\"else\""));
			}
			blkElse = new Block();
			blkElse.setParent(this);
			now = blkElse;
		} else {
			now.addStatement(statement);
		}
	}

	@Override
	public void syntax(Analyzer analyzer, SimpleStack tokens) throws ParseException {
		if (tokens.size() == 1) {
			throw new ParseException(String.format(MessageFormat.NOT_CORRECT, "Parameter format"));
		}
		addBranch(Operator.merge(tokens, 1, tokens.size(), Operator.FLOAT | Operator.BOOLEAN));
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {

		Iterator<Branch> i = branchs.iterator();
		Branch statement;

		Label end = new Label();
		while (i.hasNext()) {
			statement = i.next();
			Label next = new Label();
			Label block = new Label();
			statement.check.parseCheck(mv, local, vm, block, next);
			mv.visitJumpInsn(IFEQ, next);
			mv.visitLabel(block);
			statement.block.parse(mv, local, vm);
			mv.visitJumpInsn(GOTO, end);
			mv.visitLabel(next);
		}

		if (blkElse != null) {
			blkElse.parse(mv, local, vm);
		}

		mv.visitLabel(end);
	}
}