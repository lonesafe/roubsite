package com.roubsite.smarty4j;

import static org.objectweb.asm.Opcodes.*;

/**
 * The syntax tree node, which is an atomic operation.
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public abstract class Node {

	/**
	 * A parse function, which write a variable to JVM stack.
	 */
	protected static interface VariableLoader {

		/**
		 * Parse variable to JVM stack.
		 * 
		 * @param mv
		 *          a visitor to visit a Java method
		 * @param local
		 *          the number of local variables for the method
		 * @param vm
		 *          the manager of variable ioctl
		 */
		void parse(MethodVisitorProxy mv, int local, VariableManager vm);
	}

	/** The internal name of the method's owner class */
	protected static final String NAME = Node.class.getName().replace('.', '/');

	/** The template object, with mv.visitVarInsn(ALOAD, TEMPLATE) to get it */
	protected static final int TEMPLATE = 0;

	/** The context, with mv.visitVarInsn(ALOAD, CONTEXT) to get it */
	protected static final int CONTEXT = 1;

	/** The writer, with mv.visitVarInsn(ALOAD, WRITER) to get it */
	protected static final int WRITER = 2;

	protected static final int ENGINE = 3;
	
	/** The number of local variables for the {@link com.roubsite.smarty4j.Parser#merge} method */
	protected static final int LOCAL_START = 4;

	/**
	 * Parse the node to bytecode.
	 * 
	 * @param mv
	 *          a visitor to visit a Java method
	 * @param local
	 *          the number of local variables for the method
	 * @param vm
	 *          the manager of variable ioctl
	 */
	public abstract void parse(MethodVisitorProxy mv, int local, VariableManager vm);

	/**
	 * Write a variable to context.
	 * 
	 * @param mv
	 *          a visitor to visit a Java method
	 * @param local
	 *          the number of local variables for the method
	 * @param vm
	 *          the manager of variable ioctl
	 * @param name
	 *          the variable name
	 * @param loader
	 *          the {@code VariableLoader} used to load variable to JVM stack, a {@code null} value
	 *          indicates that the variable already in the JVM stack
	 */
	protected void writeVariable(MethodVisitorProxy mv, int local, VariableManager vm, String name,
	    VariableLoader loader) {
		if (vm.hasCached()) {
			boolean rewrite = vm.requiredRewrite();
			int index = vm.getIndex(name);
			if (index == VariableManager.NOEXIST) {
				if (!rewrite) {
					if (loader == null) {
						mv.visitInsn(POP);
					}
					// template does not read variables, ignoring
					return;
				}
			} else if (index != VariableManager.NOCACHE) {
				// use cached
				if (loader != null) {
					loader.parse(mv, local, vm);
				}
				mv.visitVarInsn(ASTORE, index);
				if (rewrite) {
					mv.visitVarInsn(ALOAD, CONTEXT);
					mv.visitLdcInsn(name);
					mv.visitVarInsn(ALOAD, index);
					mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
					    "(Ljava/lang/String;Ljava/lang/Object;)V");
				}
				return;
			}
		}
		if (loader == null) {
			mv.visitVarInsn(ASTORE, local);
		}
		mv.visitVarInsn(ALOAD, CONTEXT);
		mv.visitLdcInsn(name);
		if (loader != null) {
			loader.parse(mv, local, vm);
		} else {
			mv.visitVarInsn(ALOAD, local);
		}
		mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
		    "(Ljava/lang/String;Ljava/lang/Object;)V");
	}
}