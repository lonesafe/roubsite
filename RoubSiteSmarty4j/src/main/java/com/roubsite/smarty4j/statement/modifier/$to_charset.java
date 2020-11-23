package com.roubsite.smarty4j.statement.modifier;

import static org.objectweb.asm.Opcodes.*;

import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Modifier;

/**
 * This is used to transcode a string from the internal charset to a given charset.
 * 
 * <table border="1">
 * <colgroup> <col align="center" class="param"> <col align="center" class="type"> <col
 * align="center" class="required"> <col align="center" class="possible"> <col align="center"
 * class="default"> <col class="desc"> </colgroup> <thead>
 * <tr>
 * <th align="center">Parameter Position</th>
 * <th align="center">Type</th>
 * <th align="center">Required</th>
 * <th align="center">Possible Values</th>
 * <th align="center">Default</th>
 * <th>Description</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td align="center">1</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center">
 * <code class="literal">ISO-8859-1</code>, <code class="literal">UTF-8</code>, and any character
 * set supported by <a class="ulink" href="http://php.net/mb_convert_encoding" target="_top">
 * <code class="varname">mb_convert_encoding()</code></a></td>
 * <td align="center"><code class="literal">ISO-8859-1</code></td>
 * <td>The charset encoding the value is supposed to be encoded to</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $to_charset extends Modifier {

	private static final Definition[] definitions = { Definition.forModifier(Type.STRING,
	    new StringExpression("ISO-8859-1")) };

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
		PARAMETERS[0].parseString(mv, local, vm);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/lang/String;)[B");
	}
}