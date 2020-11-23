package com.roubsite.smarty4j.expression;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFLT;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.POP2;
import static org.objectweb.asm.Opcodes.SWAP;

import org.objectweb.asm.Label;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.Context;
import com.roubsite.smarty4j.Engine;
import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.Template;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.statement.function.$foreach;
import com.roubsite.smarty4j.statement.function.$section;

/**
 * 变量表达式节点, 向JVM语句栈内放入从数据容器中引用的对象
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class VariableExpression extends ObjectExpression {

	/** 变量在数据容器中的名称 */
	private String name;
	private String className;
	private String smarty;

	public VariableExpression(Analyzer analyzer, String name) {
		this.name = name;
		if (!name.equals("smarty")) {
			analyzer.getVariableManager().addUsedVariable(name);
			className = analyzer.getDeclared(name);
		}
	}

	public String getName() {
		return name;
	}

	@Override
	public void add(Node extended) {
		if (extendeds == null && extended instanceof MapExtended) {
			Node key = ((MapExtended) extended).key;
			if (key instanceof StringExpression) {
				if (className != null) {
					((StringExpression) key).setValue(key.toString() + "#" + className);
				} else if (name.equals("smarty")) {
					if (smarty == null) {
						smarty = key.toString();
					} else {
						smarty += "." + key.toString();
					}
					return;
				}
			}
		}
		super.add(extended);
	}

	public void parseSet(MethodVisitorProxy mv, int local, VariableManager vm) {
		if (extendeds != null) {
			parseSelf(mv, local, vm);
			int size = extendeds.size() - 1;
			for (int i = 0; i < size; i++) {
				Node extended = extendeds.get(i);
				if (extended instanceof ModifierExtended) {
					throw new RuntimeException(String.format(MessageFormat.NOT_CORRECT, "The variable name"));
				}
				extended.parse(mv, local, vm);
			}
			Node node = extendeds.get(size);
			if (node instanceof MapExtended) {
				((MapExtended) node).key.parseObject(mv, local, vm);
				mv.visitMethodInsn(INVOKESTATIC, MapExtended.NAME, "setValue",
				    "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;)V");
			} else if (node instanceof ListExtended) {
				((ListExtended) node).index.parseInteger(mv, local, vm);
				mv.visitMethodInsn(INVOKESTATIC, ListExtended.NAME, "setValue",
				    "(Ljava/lang/Object;Ljava/lang/Object;I)V");
			} else {
				mv.visitInsn(POP2);
			}
		} else {
			writeVariable(mv, local, vm, name, null);
		}
	}

	@Override
	public void parseSelf(MethodVisitorProxy mv, int local, VariableManager vm) {
		if (smarty != null) {
			if (smarty.equals("now")) {
				mv.visitTypeInsn(NEW, "java/util/Date");
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESPECIAL, "java/util/Date", "<init>", "()V");
			} else if (smarty.startsWith("capture.")) {
				String[] args = smarty.split("\\.");
				if (args.length != 2) {
					mv.visitLdcInsn(null);
				} else {
					mv.visitVarInsn(ALOAD, CONTEXT);
					mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "getCapture", "()Ljava/util/Map;");
					mv.visitLdcInsn(args[1]);
					mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get",
					    "(Ljava/lang/Object;)Ljava/lang/Object;");
				}
			} else if (smarty.startsWith("config.")) {
				String[] args = smarty.split("\\.");
				if (args.length != 2) {
					mv.visitLdcInsn(null);
				} else {
					mv.visitVarInsn(ALOAD, CONTEXT);
					mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "getConfig", "()Ljava/util/Map;");
					mv.visitLdcInsn(args[1]);
					mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get",
					    "(Ljava/lang/Object;)Ljava/lang/Object;");
				}
			} else if (smarty.startsWith("foreach.")) {
				String[] args = smarty.split("\\.");
				if (args.length == 3) {
					int type = 0;
					if (args[2].equals("first")) {
						type = 1;
					} else if (args[2].equals("last")) {
						type = 2;
					} else if (args[2].equals("index")) {
						type = 3;
					} else if (args[2].equals("total")) {
						type = 4;
					} else if (args[2].equals("iteration")) {
						type = 5;
					}
					if (type > 0) {
						Label isnull = new Label();
						Label end = new Label();
						mv.visitVarInsn(ALOAD, CONTEXT);
						mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "getForeach", "()Ljava/util/Map;");
						mv.visitLdcInsn(args[1]);
						mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get",
						    "(Ljava/lang/Object;)Ljava/lang/Object;");
						mv.visitInsn(DUP);
						mv.visitJumpInsn(IFNULL, isnull);

						mv.visitTypeInsn(CHECKCAST, $foreach.NAME + "$Bean");
						switch (type) {
						case 1:
							mv.visitFieldInsn(GETFIELD, $foreach.NAME + "$Bean", "first", "Z");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf",
							    "(Z)Ljava/lang/Boolean;");
							break;
						case 2:
							mv.visitFieldInsn(GETFIELD, $foreach.NAME + "$Bean", "last", "Z");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf",
							    "(Z)Ljava/lang/Boolean;");
							break;
						case 3:
							mv.visitFieldInsn(GETFIELD, $foreach.NAME + "$Bean", "index", "I");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
							    "(I)Ljava/lang/Integer;");
							break;
						case 4:
							mv.visitFieldInsn(GETFIELD, $foreach.NAME + "$Bean", "total", "I");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
							    "(I)Ljava/lang/Integer;");
							break;
						case 5:
							mv.visitFieldInsn(GETFIELD, $foreach.NAME + "$Bean", "iteration", "I");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
							    "(I)Ljava/lang/Integer;");
						}
						mv.visitJumpInsn(GOTO, end);

						mv.visitLabel(isnull);
						mv.visitInsn(POP);
						mv.visitLdcInsn(null);

						mv.visitLabel(end);
						return;
					}
				}
				mv.visitLdcInsn(null);
			} else if (smarty.startsWith("section.")) {
				String[] args = smarty.split("\\.");
				if (args.length == 3) {
					int type = 0;
					if (args[2].equals("first")) {
						type = 1;
					} else if (args[2].equals("last")) {
						type = 2;
					} else if (args[2].equals("index")) {
						type = 3;
					} else if (args[2].equals("total")) {
						type = 4;
					} else if (args[2].equals("rownum") || args[2].equals("iteration")) {
						type = 5;
					} else if (args[2].equals("index_prev")) {
						type = 6;
					} else if (args[2].equals("index_next")) {
						type = 7;
					}
					if (type > 0) {
						Label isnull = new Label();
						Label end = new Label();
						mv.visitVarInsn(ALOAD, CONTEXT);
						mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "getSection", "()Ljava/util/Map;");
						mv.visitLdcInsn(args[1]);
						mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get",
						    "(Ljava/lang/Object;)Ljava/lang/Object;");
						mv.visitInsn(DUP);
						mv.visitJumpInsn(IFNULL, isnull);

						mv.visitTypeInsn(CHECKCAST, $section.NAME + "$Bean");
						switch (type) {
						case 1:
							mv.visitFieldInsn(GETFIELD, $section.NAME + "$Bean", "first", "Z");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf",
							    "(Z)Ljava/lang/Boolean;");
							break;
						case 2:
							mv.visitFieldInsn(GETFIELD, $section.NAME + "$Bean", "last", "Z");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf",
							    "(Z)Ljava/lang/Boolean;");
							break;
						case 3:
							mv.visitFieldInsn(GETFIELD, $section.NAME + "$Bean", "index", "I");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
							    "(I)Ljava/lang/Integer;");
							break;
						case 4:
							mv.visitFieldInsn(GETFIELD, $section.NAME + "$Bean", "total", "I");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
							    "(I)Ljava/lang/Integer;");
							break;
						case 5:
							mv.visitFieldInsn(GETFIELD, $section.NAME + "$Bean", "iteration", "I");
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
							    "(I)Ljava/lang/Integer;");
							break;
						case 6:
							mv.visitFieldInsn(GETFIELD, $section.NAME + "$Bean", "index_prev", "I");
							mv.visitInsn(DUP);
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
							    "(I)Ljava/lang/Integer;");
							mv.visitInsn(SWAP);
							mv.visitJumpInsn(IFLT, isnull);
							break;
						case 7:
							mv.visitFieldInsn(GETFIELD, $section.NAME + "$Bean", "index_next", "I");
							mv.visitInsn(DUP);
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
							    "(I)Ljava/lang/Integer;");
							mv.visitInsn(SWAP);
							mv.visitJumpInsn(IFLT, isnull);
						}
						mv.visitJumpInsn(GOTO, end);

						mv.visitLabel(isnull);
						mv.visitInsn(POP);
						mv.visitLdcInsn(null);

						mv.visitLabel(end);
						return;
					}
				}
				mv.visitLdcInsn(null);
			} else if (smarty.equals("template")) {
				mv.visitVarInsn(ALOAD, TEMPLATE);
				mv.visitMethodInsn(INVOKEVIRTUAL, Template.NAME, "getName", "()Ljava/lang/String;");
			} else if (smarty.equals("current_dir")) {
				mv.visitVarInsn(ALOAD, TEMPLATE);
				mv.visitLdcInsn("");
				mv.visitMethodInsn(INVOKEVIRTUAL, Template.NAME, "getRelativePath",
				    "(Ljava/lang/String;)Ljava/lang/String;");
			} else if (smarty.equals("version")) {
				mv.visitLdcInsn("Smarty4j 1.1");
			} else if (smarty.equals("block.parent")) {
				mv.visitVarInsn(ALOAD, CONTEXT);
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "getBlockParent", "()Ljava/lang/String;");
			} else if (smarty.equals("block.child")) {
				mv.visitVarInsn(ALOAD, CONTEXT);
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "getBlockChild", "()Ljava/lang/String;");
			} else if (smarty.equals("ldelim")) {
				mv.visitVarInsn(ALOAD, ENGINE);
				mv.visitMethodInsn(INVOKEVIRTUAL, Engine.NAME, "getLeftDelimiter", "()Ljava/lang/String;");
			} else if (smarty.equals("rdelim")) {
				mv.visitVarInsn(ALOAD, ENGINE);
				mv.visitMethodInsn(INVOKEVIRTUAL, Engine.NAME, "getRightDelimiter", "()Ljava/lang/String;");
			} else {
				mv.visitLdcInsn(null);
			}
		} else if (vm.hasCached()) {
			// 如果对象有堆栈中的索引号, 直接从堆栈中取出对象的值
			mv.visitVarInsn(ALOAD, vm.getIndex(name));
		} else {
			// 从数据容器中获取对象的值
			mv.visitVarInsn(ALOAD, CONTEXT);
			mv.visitLdcInsn(name);
			mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get",
			    "(Ljava/lang/String;)Ljava/lang/Object;");
		}
	}
}