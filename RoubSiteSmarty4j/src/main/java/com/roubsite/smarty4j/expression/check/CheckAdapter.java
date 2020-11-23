package com.roubsite.smarty4j.expression.check;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;

/**
 * 布尔表达式转换节点, 将其它表达式转换成布尔表达式
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class CheckAdapter extends CheckExpression {

  /** 需要转换的表达式 */
  private Expression exp;

  /**
   * 创建布尔表达式转换节点
   * 
   * @param exp
   *          需要转换的表达式
   */
  public CheckAdapter(Expression exp) {
    this.exp = exp;
  }

	@Override
  public void parseCheck(MethodVisitorProxy mv, int local, VariableManager vm, Label lblTrue, Label lblFalse) {
    exp.parseCheck(mv, local, vm, lblTrue, lblFalse);
  }
}
