package com.roubsite.smarty4j.statement.modifier;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.Template;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.expression.number.ConstInteger;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Modifier;

public class $unescape extends Modifier {

	private static final int HTML = 0;
	private static final int HTMLALL = 1;

	private static final Definition[] definitions = {
	    Definition.forModifier(Type.STRING, new StringExpression("html"), "I"),
	    Definition.forModifier(Type.STRING, new StringExpression("UTF-8")) };

	private static final Pattern p = Pattern.compile("&(#([0-9]+)|[a-zA-Z]+);");

	public static Object execute(Object obj, int type, String charsetName) {
		switch (type) {
		case HTML:
			return unescapeHtml(obj.toString());
		case HTMLALL:
			return unescapeHtmlall(obj.toString());
		}
		return null;
	}

	private static String unescapeHtml(String s) {
		return s.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"")
		    .replace("&#39;", "'").replace("&amp;", "&");
	}

	private static String unescapeHtmlall(String s) {
		StringBuilder buf = new StringBuilder();
		int last = 0;
		Matcher m = p.matcher(s);
		while (m.find()) {
			buf.append(s.substring(last, m.start()));
			String str = m.group();
			if (m.group(2) != null) {
				buf.append((char) Integer.parseInt(str));
			} else {
				char c = HTMLEscape.toChar(str);
				if (c > 0) {
					buf.append((char) c);
				} else {
					buf.append(str);
				}
			}
			last = m.end();
		}
		buf.append(s.substring(last));
		return buf.toString();
	}
	
	@Override
	public void createParameters(Template tpl, List<Expression> values) throws ParseException {
		super.createParameters(tpl, values);
		String type = PARAMETERS[0].toString();
		if (type.equals("html")) {
			PARAMETERS[0] = new ConstInteger(HTML);
		} else if (type.equals("htmlall")) {
			PARAMETERS[0] = new ConstInteger(HTMLALL);
		} else if (type.equals("entity")) {
			PARAMETERS[0] = new ConstInteger(HTMLALL);
		} else {
			throw new ParseException(String.format(MessageFormat.CANNOT_BE_RESOLVED_TO, "The parameter",
			    "either html, htmlall or entity"));
		}
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}
