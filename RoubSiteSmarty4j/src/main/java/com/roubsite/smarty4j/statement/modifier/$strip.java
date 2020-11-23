package com.roubsite.smarty4j.statement.modifier;

import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Modifier;

/**
 * This replaces all spaces, newlines and tabs with a single space, or with the supplied string.
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "Grandmother of\neight makes\t    hole in one.");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle}<br>
 * {$articleTitle|strip}<br>
 * {$articleTitle|strip:'&nbsp;'}<br>
 * <br>
 * <b>Output:</b><br>
 * Grandmother of<br>
 * eight makes&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;hole in one.<br>
 * Grandmother of eight makes hole in one.<br>
 * Grandmother&nbsp;of&nbsp;eight&nbsp;makes&nbsp;hole&nbsp;in&nbsp;one.<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $strip extends Modifier {

	private static final Definition[] definitions = { Definition.forModifier(Type.STROBJ,
	    new StringExpression(" ")) };

	public static Object execute(Object obj, String separator) {
		StringBuilder buf = new StringBuilder(64);

		String s = obj.toString();
		int size = s.length();
		boolean lastIsWhitespace = false;
		for (int i = 0; i < size; i++) {
			char c = s.charAt(i);
			if (Character.isWhitespace(c)) {
				lastIsWhitespace = true;
			} else {
				if (lastIsWhitespace) {
					buf.append(separator);
				}
				buf.append(c);
				lastIsWhitespace = false;
			}
		}

		return buf.toString();
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}