package com.roubsite.smarty4j;

import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.BASTORE;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.D2I;
import static org.objectweb.asm.Opcodes.DCMPL;
import static org.objectweb.asm.Opcodes.DCONST_0;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.I2D;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.ICONST_3;
import static org.objectweb.asm.Opcodes.ICONST_4;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.ICONST_M1;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.NEWARRAY;
import static org.objectweb.asm.Opcodes.SIPUSH;
import static org.objectweb.asm.Opcodes.T_BYTE;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.roubsite.smarty4j.expression.ObjectExpression;

/**
 * A visitor proxy to visit a Java method.
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class MethodVisitorProxy extends MethodVisitor {

	private static enum Type {
		NONE, B2O, I2O, D2O
	}

	private MethodVisitor mv;
	private Type last = Type.NONE;

	/**
	 * Constructs a visitor proxy.
	 * 
	 * @param mv
	 *          a visitor to visit a Java method
	 */
	public MethodVisitorProxy(MethodVisitor mv) {
		super(Opcodes.ASM4);
		this.mv = mv;
	}

	/**
	 * Visits a Short-Circuit jump instruction, it is extended jump instruction.
	 * 
	 * @param opcode
	 *          a jump instruction is an instruction that may jump to another instruction, this opcode
	 *          is either IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT,
	 *          IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, GOTO, JSR, IFNULL or IFNONNULL
	 * @param lblTrue
	 *          beginning of the handler block for condition is true, if it is {@code null}, push
	 *          <tt>true</tt>(ICONST_1) to JVM stack.
	 * @param lblFalse
	 *          beginning of the handler block for condition is false, if it is {@code null}, push
	 *          <tt>false</tt>(ICONST_0) to JVM stack.
	 */
	public void visitSCJumpInsn(int opcode, Label lblTrue, Label lblFalse) {
		boolean noDefTrue = lblTrue == null;
		boolean noDefFalse = lblFalse == null;

		if (noDefTrue) {
			lblTrue = new Label();
		}
		mv.visitJumpInsn(opcode, lblTrue);

		if (noDefFalse) {
			lblFalse = new Label();
			mv.visitLdcInsn(false);
		}
		mv.visitJumpInsn(GOTO, lblFalse);

		if (noDefTrue) {
			mv.visitLabel(lblTrue);
			mv.visitLdcInsn(true);
		}

		if (noDefFalse) {
			mv.visitLabel(lblFalse);
		}
	}

	private void visitType() {
		switch (last) {
		case NONE:
			break;
		case B2O:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
			break;
		case I2O:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
			break;
		case D2O:
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
		}
		last = Type.NONE;
	}
	
	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		visitType();
		return mv.visitAnnotationDefault();
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		visitType();
		return mv.visitAnnotation(desc, visible);
	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		visitType();
		return mv.visitParameterAnnotation(parameter, desc, visible);
	}

	@Override
	public void visitAttribute(Attribute attr) {
		visitType();
		mv.visitAttribute(attr);
	}

	@Override
	public void visitCode() {
		visitType();
		mv.visitCode();
	}

	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		visitType();
		mv.visitFrame(type, nLocal, local, nStack, stack);
	}

	@Override
	public void visitInsn(int opcode) {
		visitType();
		mv.visitInsn(opcode);
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		visitType();
		mv.visitIntInsn(opcode, operand);
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		visitType();
		mv.visitVarInsn(opcode, var);
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		visitType();
		mv.visitTypeInsn(opcode, type);
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		visitType();
		mv.visitFieldInsn(opcode, owner, name, desc);
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		if (opcode == INVOKESTATIC) {
			if (owner.equals(ObjectExpression.NAME)) {
				if (name.equals("o2b")) {
					switch (last) {
					case NONE:
						break;
					case B2O:
					case I2O:
						last = Type.NONE;
						return;
					case D2O:
						last = Type.NONE;
						mv.visitInsn(DCONST_0);
						mv.visitInsn(DCMPL);
						visitSCJumpInsn(IFNE, null, null);
						return;
					}
				} else if (name.equals("o2i")) {
					switch (last) {
					case NONE:
						break;
					case B2O:
					case I2O:
						last = Type.NONE;
						return;
					case D2O:
						last = Type.NONE;
						mv.visitInsn(D2I);
						return;
					}
				} else if (name.equals("o2d")) {
					switch (last) {
					case NONE:
						break;
					case B2O:
					case I2O:
						last = Type.NONE;
						mv.visitInsn(I2D);
						return;
					case D2O:
						last = Type.NONE;
						return;
					}
				}
			} else if (owner.equals("java/lang/Boolean") && name.equals("valueOf")
			    && desc.equals("(Z)Ljava/lang/Boolean;")) {
				last = Type.B2O;
				return;
			} else if (owner.equals("java/lang/Integer")
			    && name.equals("valueOf") && desc.equals("(I)Ljava/lang/Integer;")) {
				last = Type.I2O;
				return;
			} else if (opcode == INVOKESTATIC && owner.equals("java/lang/Double") && name.equals("valueOf")
			    && desc.equals("(D)Ljava/lang/Double;")) {
				last = Type.D2O;
				return;
			}
		}
		visitType();
		mv.visitMethodInsn(opcode, owner, name, desc);
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		visitType();
		mv.visitJumpInsn(opcode, label);
	}

	@Override
	public void visitLabel(Label label) {
		visitType();
		mv.visitLabel(label);
	}

	@Override
	public void visitLdcInsn(Object cst) {
		visitType();
		if (cst == null) {
			mv.visitInsn(ACONST_NULL);
		} else if (cst instanceof Boolean) {
			if ((Boolean) cst) {
				mv.visitInsn(ICONST_1);
			} else {
				mv.visitInsn(ICONST_0);
			}
		} else if (cst instanceof Integer) {
			int value = ((Integer) cst).intValue();
			switch (value) {
			case -1:
				mv.visitInsn(ICONST_M1);
				break;
			case 0:
				mv.visitInsn(ICONST_0);
				break;
			case 1:
				mv.visitInsn(ICONST_1);
				break;
			case 2:
				mv.visitInsn(ICONST_2);
				break;
			case 3:
				mv.visitInsn(ICONST_3);
				break;
			case 4:
				mv.visitInsn(ICONST_4);
				break;
			case 5:
				mv.visitInsn(ICONST_5);
				break;
			default:
				if (value <= Byte.MAX_VALUE && value >= Byte.MIN_VALUE) {
					mv.visitIntInsn(BIPUSH, value);
				} else if (value <= Short.MAX_VALUE && value >= Short.MIN_VALUE) {
					mv.visitIntInsn(SIPUSH, value);
				} else {
					mv.visitLdcInsn(value);
				}
			}
		} else if (cst instanceof byte[]) {
			byte[] value = (byte[]) cst;
			mv.visitLdcInsn(value.length);
			mv.visitIntInsn(NEWARRAY, T_BYTE);
			for (int i = 0; i < value.length; i++) {
				mv.visitInsn(DUP);
				mv.visitLdcInsn(i);
				mv.visitLdcInsn(value[i]);
				mv.visitInsn(BASTORE);
			}
		} else if (cst instanceof Class) {
			mv.visitLdcInsn(org.objectweb.asm.Type.getType((Class<?>) cst));
		} else {
			mv.visitLdcInsn(cst);
		}
	}

	@Override
	public void visitIincInsn(int var, int increment) {
		visitType();
		mv.visitIincInsn(var, increment);
	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		visitType();
		mv.visitTableSwitchInsn(min, max, dflt, labels);
	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		visitType();
		mv.visitLookupSwitchInsn(dflt, keys, labels);
	}

	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		visitType();
		mv.visitMultiANewArrayInsn(desc, dims);
	}

	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		visitType();
		mv.visitTryCatchBlock(start, end, handler, type);
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start,
	    Label end, int index) {
		visitType();
		mv.visitLocalVariable(name, desc, signature, start, end, index);
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		visitType();
		mv.visitLineNumber(line, start);
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		visitType();
		mv.visitMaxs(maxStack, maxLocals);
	}

	@Override
	public void visitEnd() {
		visitType();
		mv.visitEnd();
	}
}
