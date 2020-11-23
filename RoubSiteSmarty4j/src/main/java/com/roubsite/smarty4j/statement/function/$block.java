package com.roubsite.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import com.roubsite.smarty4j.Context;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.TemplateWriter;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.statement.Block;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Document;

/**
 * This is used to define a named area of template source for template inheritance.<br>
 * The {block} template source area of a child template will replace the correponding areas in the
 * parent template(s).<br>
 * Optionally {block} areas of child and parent templates can be merged into each other. You can
 * append or prepend the parent {block} content by using the append or prepend option flag with the
 * childs {block} definition. With the {$smarty.block.parent} the {block} content of the parent
 * template can be inserted at any location of the child {block} content. {$smarty.block.child}
 * inserts the {block} content of the child template at any location of the parent {block}.
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
 * <td>The name of the template source block</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * <table border="1">
 * <colgroup> <col align="center" class="param"> <col class="desc"> </colgroup> <thead>
 * <tr>
 * <th align="center">Option Name</th>
 * <th>Description</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td align="center">append</td>
 * <td>The <code class="varname">{block}</code> content will be be appended to the content of the
 * parent template <code class="varname">{block}</code></td>
 * </tr>
 * <tr>
 * <td align="center">prepend</td>
 * <td>The <code class="varname">{block}</code> content will be prepended to the content of the
 * parent template <code class="varname">{block}</code></td>
 * </tr>
 * <tr>
 * <td align="center">hide</td>
 * <td>Ignore the block content if no child block of same name is existing.</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $block extends Block {

	private static final Definition[] definitions = { Definition.forFunction("name", Type.STRING) };

	private $block child;

	@Override
	public boolean setParent(Block parent) throws ParseException {
		if (PARAMETERS != null) {
			Document doc = (Document) find(parent, Document.class);
			String name = PARAMETERS[0].toString();
			$block old = doc.addBlock(name, this);
			if (old != null) {
				old.child = this;
				return false;
			}
		}
		return super.setParent(parent);
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		if (child == null) {
			if (!contain("hide")) {
				super.parse(mv, local, vm);
			}
		} else {
			boolean parentIsText = children.size() == 1;
			boolean childIsText = child.children.size() == 1;

			if (!parentIsText || !childIsText) {
				mv.visitVarInsn(ALOAD, WRITER);

				mv.visitVarInsn(ALOAD, CONTEXT);
				if (childIsText) {
					mv.visitLdcInsn(null);
				} else {
					mv.visitMethodInsn(INVOKESTATIC, TemplateWriter.NAME, "getTemporaryWriter", "()L"
					    + TemplateWriter.NAME + ";");
					mv.visitVarInsn(ASTORE, WRITER);
					super.parse(mv, local, vm);
					mv.visitVarInsn(ALOAD, WRITER);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "toString", "()Ljava/lang/String;");
				}
				if (parentIsText) {
					mv.visitLdcInsn(null);
				} else {
					mv.visitMethodInsn(INVOKESTATIC, TemplateWriter.NAME, "getTemporaryWriter", "()L"
					    + TemplateWriter.NAME + ";");
					mv.visitVarInsn(ASTORE, WRITER);
					child.parse(mv, local, vm);
					mv.visitVarInsn(ALOAD, WRITER);
					mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "toString", "()Ljava/lang/String;");
				}
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "setBlock",
				    "(Ljava/lang/String;Ljava/lang/String;)V");

				mv.visitVarInsn(ASTORE, WRITER);
			}

			if (child.contain("append")) {
				child.parse(mv, local, vm);
				super.parse(mv, local, vm);
			} else if (child.contain("prepend")) {
				super.parse(mv, local, vm);
				child.parse(mv, local, vm);
			} else if (!parentIsText) {
				super.parse(mv, local, vm);
			} else {
				child.parse(mv, local, vm);
			}

			if (!parentIsText || !childIsText) {
				mv.visitVarInsn(ALOAD, CONTEXT);
				mv.visitLdcInsn(null);
				mv.visitLdcInsn(null);
				mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "setBlock",
				    "(Ljava/lang/String;Ljava/lang/String;)V");
			}
		}
	}
}