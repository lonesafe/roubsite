package com.roubsite.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.GOTO;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.TemplateReader;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.number.ConstInteger;
import com.roubsite.smarty4j.statement.Function;
import com.roubsite.smarty4j.statement.LoopFunction;
import com.roubsite.smarty4j.util.SimpleStack;

/**
 * The tag aborts the iteration of the array
 * 
 * @see com.ruixus.smarty4j.statement.ILoop
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $break extends Function {

	/** 需要跳出的层级数量 */
	private int nLoop;

	/** 全部循环体出口标签 */
	private Label lblBreak;

	/** 需要依次跳出的循环体，不包括最终跳出的最外层循环体 */
	private LoopFunction[] blocks;

	@Override
	public void syntax(Analyzer analyzer, SimpleStack tokens) throws ParseException {
		switch (tokens.size()) {
		case 1:
			nLoop = 1;
			return;
		case 2:
			Object value = tokens.get(1);
			if (value instanceof ConstInteger) {
				nLoop = ((ConstInteger) value).getValue();
				if (nLoop > 0) {
					return;
				}
			}
		}
		throw new ParseException(String.format(MessageFormat.NOT_CORRECT, "参数格式"));
	}

	@Override
	public void analyzeContent(Analyzer analyzer, TemplateReader reader) {
		blocks = new LoopFunction[nLoop - 1];
		LoopFunction now = (LoopFunction) find(getParent(), LoopFunction.class);
		int i = 1;
		while (true) {
			if (now == null) {
				reader.addMessage(String.format(MessageFormat.MUST_BE_USED_INSIDE_OF, "\"break\"",
				    nLoop == 1 ? "a" : "the " + nLoop + "th") + " loop");
				return;
			}
			if (i == nLoop) {
				break;
			}
			blocks[i - 1] = now;
			now = (LoopFunction) find(now.getParent(), LoopFunction.class);
			i++;
		}
		lblBreak = now.getBreakLabel();
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		for (LoopFunction block : blocks) {
			block.restore(mv, vm);
		}
		mv.visitJumpInsn(GOTO, lblBreak);
	}
}