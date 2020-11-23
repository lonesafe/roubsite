package com.roubsite.smarty4j.statement;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;

/**
 * 循环区块函数节点，被用于支持break与continue等指令。
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public abstract class LoopFunction extends Block {

	protected Label lblContinue = new Label();
	protected Label lblBreak = new Label();

	/**
	 * 获取循环开始位置。
	 * 
	 * @return 循环的开始位置标签
	 */
	public Label getContinueLabel() {
		return lblContinue;
	}

	/**
	 * 获取循环结束位置。
	 * 
	 * @return 循环的结束位置标签
	 */
	public Label getBreakLabel() {
		return lblBreak;
	}

	/**
	 * 恢复循环体的状态。
	 * 
	 * @param mv
	 *          ASM方法访问对象
	 * @param vm
	 *          变量管理器
	 */
	public abstract void restore(MethodVisitorProxy mv, VariableManager vm);
}
