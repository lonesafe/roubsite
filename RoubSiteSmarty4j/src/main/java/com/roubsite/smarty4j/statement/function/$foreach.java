package com.roubsite.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARRAYLENGTH;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP2;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.IF_ICMPEQ;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INSTANCEOF;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.POP2;

import java.util.List;
import java.util.Map;

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
import com.roubsite.smarty4j.expression.NullExpression;
import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.expression.VariableExpression;
import com.roubsite.smarty4j.statement.Block;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.LoopFunction;
import com.roubsite.smarty4j.util.SimpleStack;

/**
 * The tag is used for looping over arrays of data. {foreach} has a simpler and cleaner syntax than
 * the {section} loop, and can also loop over associative arrays.
 * 
 * <code>
 * {foreach $arrayvar as $itemvar}
 * {foreach $arrayvar as $keyvar=>$itemvar}
 * </code>
 * 
 * {foreachelse} is executed when there are no values in the array variable.<br>
 * {foreach} properties are @index, @iteration, @first, @last, @total.<br>
 *
 * This foreach syntax does not accept any named attributes. This syntax is new to Smarty 3, however
 * the Smarty 2.x syntax {foreach from=$myarray key="mykey" item="myitem"} is still supported.
 *
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $foreach extends LoopFunction {

	/**
	 * foreach信息体
	 */
	public static class Bean {

		public boolean first = true;
		public boolean last;
		public int index;
		public int total;
		public int iteration;

		/**
		 * 设置信息体
		 * 
		 * @param index
		 *          当前索引号
		 */
		public void set(int index) {
			if (index == total - 1) {
				last = true;
			} else {
				last = false;
			}
			if (index != 0) {
				first = false;
			}

			this.index = index;
			iteration = index + 1;
		}
	}

	/** ASM名称 */
	public static final String NAME = $foreach.class.getName().replace('.', '/');

	/** 参数定义 */
	private static final Definition[] definitions = {
	    Definition.forFunction("from", Type.OBJECT),
	    Definition.forFunction("item", Type.STRING),
	    Definition.forFunction("key", Type.STRING, NullExpression.VALUE),
	    Definition.forFunction("name", Type.STRING, NullExpression.VALUE) };

	/**
	 * 初始化foreach信息体
	 * 
	 * @param total
	 *          全部的元素数量
	 * @param ctx
	 *          数据容器
	 * @param name
	 *          foreach名称
	 * @return foreach信息体
	 */
	public static Bean init(int total, Context ctx, String name) {
		Bean bean = new Bean();
		bean.total = total;

		if (name != null) {
			ctx.getForeach().put(name, bean);
		}
		return bean;
	}

	/**
	 * 获取一个循环体数据源包含的对象数组，如果数据源是Map，将取回关键字对应的数组， 如果无法将数据源转换成等价的对象数组，数据源将直接被返回
	 * 
	 * @param o
	 *          需要循环的源对象
	 * @return 源对象数组
	 */
	public static Object[] getLooper(Object o) {
		if (o instanceof List) {
			return ((List<?>) o).toArray();
		} else if (o instanceof Object[]) {
			return (Object[]) o;
		} else if (o instanceof Map) {
			return ((Map<?, ?>) o).entrySet().toArray();
		} else {
			return new Object[] { o };
		}
	}

	/** 循环体为空时对应的区块 */
	private Block blkElse;
	private int version;
	private String propName;

	public String getItemName() {
		if (version == 3) {
			return PARAMETERS[1].toString();
		}
		return null;
	}

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
	public void addStatement(Node child) throws ParseException {
		if (child instanceof $foreachelse) {
			if (blkElse != null) {
				throw new ParseException(String.format(MessageFormat.IS_ALREADY_DEFINED, "\"foreachelse\""));
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
		if (size > 3 && tokens.get(1) instanceof VariableExpression) {
			if ("as".equals(tokens.get(2)) && tokens.get(3) instanceof VariableExpression) {
				VariableExpression exp = (VariableExpression) tokens.get(3);
				if (!exp.hasExtended()) {
					if (size == 4) {
						PARAMETERS = new Expression[4];
						PARAMETERS[0] = (VariableExpression) tokens.get(1);
						PARAMETERS[1] = new StringExpression(exp.getName());
						PARAMETERS[2] = NullExpression.VALUE;
						PARAMETERS[3] = NullExpression.VALUE;
						version = 3;
						return;
					} else if (size == 7 && tokens.get(4) == Operator.SET && tokens.get(5) == Operator.GT
					    && tokens.get(6) instanceof VariableExpression) {
						VariableExpression exp2 = (VariableExpression) tokens.get(6);
						if (!exp2.hasExtended()) {
							PARAMETERS = new Expression[4];
							PARAMETERS[0] = (VariableExpression) tokens.get(1);
							PARAMETERS[1] = new StringExpression(exp2.getName());
							PARAMETERS[2] = new StringExpression(exp.getName());
							PARAMETERS[3] = NullExpression.VALUE;
							version = 3;
							return;
						}
					}
				}
			}
		}
		version = 2;
		super.syntax(analyzer, tokens);
	}

	@Override
	public void analyzeContent(Analyzer analyzer, TemplateReader reader) {
		String item = PARAMETERS[1].toString();
		String key = PARAMETERS[2] != NullExpression.VALUE ? PARAMETERS[2].toString() : null;
		propName = item + "@";
		VariableManager vm = analyzer.getVariableManager();
		VariableManager cvm = new VariableManager(analyzer.getTemplate().getEngine());
		analyzer.setVariableManager(cvm);
		if (version == 3) {
			analyzer.bindProperty(item, Bean.class);
		}
		super.analyzeContent(analyzer, reader);
		if (cvm.hasCached()) {
			if (cvm.getIndex(item) == VariableManager.NOEXIST) {
				PARAMETERS[1] = NullExpression.VALUE;
			}
			if (cvm.getIndex(key) == VariableManager.NOEXIST) {
				PARAMETERS[2] = NullExpression.VALUE;
			}
		}
		vm.merge(cvm, item, key);
		analyzer.setVariableManager(vm);
		if (version == 3) {
			analyzer.bindProperty(item, null);
		}
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {

		int CURRITEM = local;
		int CURRKEY = local + 1;
		int BEAN = local + 2;
		int FROM = local + 3;
		int INDEX = local + 4;
		int LOCAL = local + 5;

		Integer oldItem = null;
		Integer oldKey = null;

		Expression item = PARAMETERS[1];
		Expression key = PARAMETERS[2];

		Label isnull = new Label();
		Label isnotmap = new Label();
		Label loopinit = new Label();
		Label setstart = new Label();
		Label setend = new Label();
		Label setlist = new Label();
		Label loopend = new Label();
		Label end = new Label();

		PARAMETERS[0].parseObject(mv, LOCAL, vm);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ASTORE, FROM);
		mv.visitJumpInsn(IFNULL, isnull);

		mv.visitVarInsn(ALOAD, FROM);
		mv.visitMethodInsn(INVOKESTATIC, NAME, "getLooper", "(Ljava/lang/Object;)[Ljava/lang/Object;");
		mv.visitInsn(DUP);
		mv.visitVarInsn(ASTORE, BEAN);
		mv.visitInsn(ARRAYLENGTH);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ISTORE, INDEX);
		mv.visitJumpInsn(IFEQ, isnull);

		boolean hasProp = version == 3 && vm.getIndex(propName) != VariableManager.NOEXIST;
		if (hasProp) {
			vm.setIndex(propName, BEAN);
		}
		if (vm.hasCached()) {
			if (item != NullExpression.VALUE) {
				oldItem = vm.getIndex(item.toString());
				vm.setIndex(item.toString(), CURRITEM);
			}
			if (key != NullExpression.VALUE) {
				oldKey = vm.getIndex(key.toString());
				vm.setIndex(key.toString(), CURRKEY);
			}
		} else {
			// 保存原始的循环变量名对应的值
			if (item != NullExpression.VALUE) {
				mv.visitVarInsn(ALOAD, CONTEXT);
				item.parse(mv, LOCAL, vm);
				mv.visitInsn(DUP2);
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get",
				    "(Ljava/lang/String;)Ljava/lang/Object;");
			}
			if (key != NullExpression.VALUE) {
				mv.visitVarInsn(ALOAD, CONTEXT);
				key.parse(mv, LOCAL, vm);
				mv.visitInsn(DUP2);
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get",
				    "(Ljava/lang/String;)Ljava/lang/Object;");
			}
		}

		mv.visitVarInsn(ALOAD, FROM);
		mv.visitTypeInsn(INSTANCEOF, "java/util/Map");
		mv.visitJumpInsn(IFEQ, isnotmap);
		mv.visitVarInsn(ALOAD, FROM);
		mv.visitTypeInsn(CHECKCAST, "java/util/Map");
		mv.visitJumpInsn(GOTO, loopinit);

		mv.visitLabel(isnotmap);
		mv.visitLdcInsn(null);

		mv.visitLabel(loopinit);
		mv.visitVarInsn(ASTORE, FROM);
		mv.visitVarInsn(ALOAD, BEAN);
		mv.visitVarInsn(ILOAD, INDEX);
		mv.visitLdcInsn(0);
		mv.visitVarInsn(ISTORE, INDEX);

		if (PARAMETERS[3] != NullExpression.VALUE || hasProp) {
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, CONTEXT);
			PARAMETERS[3].parse(mv, LOCAL, vm);
			mv.visitMethodInsn(INVOKESTATIC, NAME, "init",
					"(IL" + Context.NAME + ";Ljava/lang/String;)L" + NAME + "$Bean;");
			mv.visitVarInsn(ASTORE, BEAN);
		}

		mv.visitLabel(setstart);
		// 此时堆栈中最近的两个数据分别是LOOPER与TOTAL
		if (item != NullExpression.VALUE || key != NullExpression.VALUE) {
			mv.visitInsn(DUP2);
		} else {
			mv.visitInsn(DUP);
		}
		mv.visitVarInsn(ILOAD, INDEX);
		mv.visitJumpInsn(IF_ICMPEQ, loopend);

		if (PARAMETERS[3] != NullExpression.VALUE || hasProp) {
			mv.visitVarInsn(ALOAD, BEAN);
			mv.visitVarInsn(ILOAD, INDEX);
			mv.visitMethodInsn(INVOKEVIRTUAL, NAME + "$Bean", "set", "(I)V");
		}

		if (item != NullExpression.VALUE || key != NullExpression.VALUE) {
			mv.visitVarInsn(ILOAD, INDEX);
			mv.visitInsn(AALOAD);
		}

		mv.visitVarInsn(ALOAD, FROM);
		mv.visitJumpInsn(IFNULL, setlist);

		// 循环源集合是Map型时所执行的处理
		if (key != NullExpression.VALUE) {
			if (item != NullExpression.VALUE) {
				mv.visitInsn(DUP);
			}
			mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map$Entry", "getKey", "()Ljava/lang/Object;");
			writeVariable(mv, LOCAL, vm, key.toString(), null);
		}
		if (item != NullExpression.VALUE) {
			mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map$Entry", "getValue", "()Ljava/lang/Object;");
		}
		mv.visitJumpInsn(GOTO, setend);

		// 循环源集合是List型时所执行的处理
		mv.visitLabel(setlist);
		if (key != NullExpression.VALUE) {
			writeVariable(mv, LOCAL, vm, key.toString(), new VariableLoader() {
				public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
					mv.visitVarInsn(ILOAD, local - 1);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				}
			});
		}

		mv.visitLabel(setend);
		if (item != NullExpression.VALUE) {
			writeVariable(mv, LOCAL, vm, item.toString(), null);
		}
		super.parse(mv, LOCAL, vm);
		mv.visitLabel(lblContinue);
		mv.visitIincInsn(INDEX, 1);
		mv.visitJumpInsn(GOTO, setstart);

		mv.visitLabel(loopend);
		if (item != NullExpression.VALUE) {
			mv.visitInsn(POP);
		}
		// 循环结束, 恢复过程中被设置的属性的原始值
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
				vm.setIndex(item.toString(), oldItem);
			}
			if (key != NullExpression.VALUE) {
				vm.setIndex(key.toString(), oldKey);
			}
		}
	}
}