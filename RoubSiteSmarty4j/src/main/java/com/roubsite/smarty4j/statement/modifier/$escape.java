package com.roubsite.smarty4j.statement.modifier;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.Template;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.expression.check.TrueCheck;
import com.roubsite.smarty4j.expression.number.ConstInteger;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Modifier;
import com.roubsite.smarty4j.util.SimpleEncoder;

/**
 * escape is used to encode or escape a variable to html, url, single quotes, hex, hexentity,
 * javascript and mail. By default its html.
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
 * <code class="literal">html</code>, <code class="literal">htmlall</code>,
 * <code class="literal">url</code>, <code class="literal">urlpathinfo</code>,
 * <code class="literal">quotes</code>, <code class="literal">hex</code>,
 * <code class="literal">hexentity</code>, <code class="literal">javascript</code>,
 * <code class="literal">mail</code></td>
 * <td align="center"><code class="literal">html</code></td>
 * <td>This is the escape format to use.</td>
 * </tr>
 * <tr>
 * <td align="center">2</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center">
 * <code class="literal">ISO-8859-1</code>, <code class="literal">UTF-8</code>, and any character
 * set supported by <a class="ulink" href="http://php.net/htmlentities" target="_top">
 * <code class="varname">htmlentities()</code></a></td>
 * <td align="center"><code class="literal">UTF-8</code></td>
 * <td>The character set encoding passed to htmlentities() et. al.</td>
 * </tr>
 * <tr>
 * <td align="center">3</td>
 * <td align="center">boolean</td>
 * <td align="center">No</td>
 * <td align="center"><code class="constant">FALSE</code></td>
 * <td align="center"><code class="constant">TRUE</code></td>
 * <td>Double encode entites from &amp;amp; to &amp;amp;amp; (applys to
 * <code class="literal">html</code> and <code class="literal">htmlall</code> only)</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "'Stiff Opposition Expected to Casketless Funeral Plan'");<br>
 * context.set("EmailAddress", "smarty@example.com");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle}<br>
 * {$articleTitle|escape}<br>
 * {$articleTitle|escape:'html'}<br>
 * {$articleTitle|escape:'htmlall'}<br>
 * &lt;a href="?title={$articleTitle|escape:'url'}"&gt;click here&lt;/a&gt;<br>
 * {$articleTitle|escape:'quotes'}<br>
 * &lt;a href="mailto:{$EmailAddress|escape:'hex'}"&gt;{$EmailAddress|escape:'hexentity'}&lt;/a&gt;<br>
 * {$EmailAddress|escape:'mail'}<br>
 * <br>
 * <b>Output:</b><br>
 * 'Stiff Opposition Expected to Casketless Funeral Plan'<br>
 * &amp;#039;Stiff Opposition Expected to Casketless Funeral Plan&amp;#039;<br>
 * &amp;#039;Stiff Opposition Expected to Casketless Funeral Plan&amp;#039;<br>
 * &amp;#039;Stiff&amp;nbsp;Opposition&amp;nbsp;Expected&amp;nbsp;to&amp;nbsp;Casketless&amp;nbsp;Funeral&amp;nbsp;Plan&amp;#039;<br>
 * &lt;a href="?title=%27Stiff%20Opposition%20Expected%20to%20Casketless%20Funeral%20Plan%27"&gt;click here&lt;/a&gt;<br>
 * \'Stiff Opposition Expected to Casketless Funeral Plan\'<br>
 * &lt;a href="mailto:%73%6D%61%72%74%79%40%65%78%61%6D%70%6C%65%2E%63%6F%6D"&gt;&amp;#x73;&amp;#x6D;&amp;#x61;&amp;#x72;&amp;#x74;&amp;#x79;&amp;#x40;&amp;#x65;&amp;#x78;&amp;#x61;&amp;#x6D;&amp;#x70;&amp;#x6C;&amp;#x65;&amp;#x2E;&amp;#x63;&amp;#x6F;&amp;#x6D;&lt;/a&gt;<br>
 * smarty [AT] example [DOT] com<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $escape extends Modifier {

	private static final int HTML = 0;
	private static final int HTMLALL = 1;
	private static final int URL = 2;
	private static final int URLPATHINFO = 3;
	private static final int QUOTES = 4;
	private static final int HEX = 5;
	private static final int HEXENTITY = 6;
	private static final int JAVASCRIPT = 7;
	private static final int MAIL = 8;

	private static final Definition[] definitions = {
	    Definition.forModifier(Type.STRING, new StringExpression("html"), "I"),
	    Definition.forModifier(Type.STRING, new StringExpression("UTF-8")),
	    Definition.forModifier(Type.BOOLEAN, TrueCheck.VALUE) };

	private static final Pattern p = Pattern.compile("^&#[0-9]+;$");
	private static final char[] hex = {
	    '0',
	    '1',
	    '2',
	    '3',
	    '4',
	    '5',
	    '6',
	    '7',
	    '8',
	    '9',
	    'A',
	    'B',
	    'C',
	    'D',
	    'E',
	    'F' };

	public static Object execute(Object obj, int type, String charsetName, boolean doubleEncode) {
		switch (type) {
		case HTML:
			return escapeHtml(obj.toString(), false, doubleEncode);
		case HTMLALL:
			return escapeHtml(obj.toString(), true, doubleEncode);
		case URL:
			return escapeUrl(obj.toString(), true, charsetName);
		case URLPATHINFO:
			return escapeUrl(obj.toString(), false, charsetName);
		case QUOTES:
			return escapeQuotes(obj.toString());
		case HEX:
			return escapeHex(obj.toString(), charsetName);
		case HEXENTITY:
			return escapeHexEntity(obj.toString());
		case JAVASCRIPT:
			return escapeJavascript(obj.toString());
		case MAIL:
			return escapeMail(obj.toString());
		}
		return null;
	}

	private static String escapeHtml(String s, boolean all, boolean doubleEncode) {
		int len = s.length();
		StringBuilder buf = new StringBuilder(256);

		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '"':
				buf.append("&quot;");
				continue;
			case '\'':
				buf.append("&#39;");
				continue;
			case '<':
				buf.append("&lt;");
				continue;
			case '>':
				buf.append("&gt;");
				continue;
			case '&':
				if (!doubleEncode) {
					int index = s.indexOf(';', i + 1);
					if (index > 0) {
						String str = s.substring(i, index + 1);
						if (HTMLEscape.toChar(str) > 0 || p.matcher(str).find()) {
							buf.append(str);
							i = index;
							continue;
						}
					}
				}
				buf.append("&amp;");
				continue;
			case ' ':
				if (all) {
					buf.append("&nbsp;");
					continue;
				}
				break;
			default:
				if (all) {
					if (Character.isISOControl(c) || c >= 128) {
						buf.append("&#");
						buf.append(Integer.toString(c));
						buf.append(';');
						continue;
					}
				}
				break;
			}
			buf.append(c);
		}

		return buf.toString();
	}

	private static String escapeUrl(String s, boolean escapePath, String charsetName) {
		Charset cs = Charset.forName(charsetName);
		int len = s.length();
		StringBuilder buf = new StringBuilder(256);

		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c == '_' || c == '-' || c == '.' || (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')
			    || (c >= 'A' && c <= 'Z') || (!escapePath && c == '/')) {
				buf.append(c);
			} else {
				int j = i + 1;
				for (; j < len; j++) {
					c = s.charAt(j);
					if (c == '_' || c == '-' || c == '.' || (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')
					    || (c >= 'A' && c <= 'Z') || (!escapePath && c == '/')) {
						break;
					}
				}
				implEscapeHex(buf, s.substring(i, j), cs);
				i = j - 1;
			}
		}

		return buf.toString();
	}

	private static String escapeQuotes(String s) {
		int len = s.length();
		StringBuilder buf = new StringBuilder(256);

		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c == '\\' || c == '\'' || c == '"') {
				buf.append('\\');
			}
			buf.append(c);
		}

		return buf.toString();
	}

	private static String escapeHex(String s, String charsetName) {
		StringBuilder buf = new StringBuilder(256);
		implEscapeHex(buf, s, Charset.forName(charsetName));
		return buf.toString();
	}

	private static String escapeHexEntity(String s) {
		int len = s.length();
		StringBuilder buf = new StringBuilder(256);

		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c < 256) {
				buf.append("&#x");
				buf.append(hex[c / 16]);
				buf.append(hex[c % 16]);
				buf.append(';');
			} else {
				buf.append("&#u");
				if (c >= 256 * 16) {
					buf.append(hex[c >> 12]);
				}
				buf.append(hex[c >> 8]);
				buf.append(hex[c / 16]);
				buf.append(hex[c % 16]);
				buf.append(';');
			}
		}

		return buf.toString();
	}

	private static String escapeJavascript(String s) {
		int len = s.length();
		StringBuilder buf = new StringBuilder(256);
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\r':
				buf.append("\\r");
				continue;
			case '\n':
				buf.append("\\n");
				continue;
			case '<':
				if (i + 1 < len && s.charAt(i + 1) == '/') {
					buf.append("<\\/");
					i++;
					continue;
				}
				break;
			case '\\':
			case '\'':
			case '"':
				buf.append('\\');
			}
			buf.append(c);
		}

		return buf.toString();
	}

	private static String escapeMail(String s) {
		int len = s.length();
		StringBuilder buf = new StringBuilder(256);

		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c == '@') {
				buf.append(" [AT] ");
			} else if (c == '.') {
				buf.append(" [DOT] ");
			} else {
				buf.append(c);
			}
		}

		return buf.toString();
	}

	private static void implEscapeHex(StringBuilder buf, String s, Charset cs) {
		ByteBuffer bb = ByteBuffer.allocate(s.length() * 4);
		SimpleEncoder.forCharset(cs).encode(CharBuffer.wrap(s.toCharArray()), bb);
		byte[] array = bb.array();
		bb.flip();
		int limit = bb.limit();
		for (int i = 0; i < limit; i++) {
			int v = array[i] & 0xFF;
			buf.append('%');
			buf.append(hex[v / 16]);
			buf.append(hex[v % 16]);
		}
	}

	@Override
	public void createParameters(Template tpl, List<Expression> values) throws ParseException {
		super.createParameters(tpl, values);
		String type = PARAMETERS[0].toString();
		if (type.equals("html")) {
			PARAMETERS[0] = new ConstInteger(HTML);
		} else if (type.equals("htmlall")) {
			PARAMETERS[0] = new ConstInteger(HTMLALL);
		} else if (type.equals("url")) {
			PARAMETERS[0] = new ConstInteger(URL);
		} else if (type.equals("urlpathinfo")) {
			PARAMETERS[0] = new ConstInteger(URLPATHINFO);
		} else if (type.equals("quotes")) {
			PARAMETERS[0] = new ConstInteger(QUOTES);
		} else if (type.equals("hex")) {
			PARAMETERS[0] = new ConstInteger(HEX);
		} else if (type.equals("hexentity")) {
			PARAMETERS[0] = new ConstInteger(HEXENTITY);
		} else if (type.equals("javascript")) {
			PARAMETERS[0] = new ConstInteger(JAVASCRIPT);
		} else if (type.equals("mail")) {
			PARAMETERS[0] = new ConstInteger(MAIL);
		} else {
			throw new ParseException(String.format(MessageFormat.CANNOT_BE_RESOLVED_TO, "The parameter",
			    "either html, htmlall, url, urlpathinfo, quotes, hex, hexentity, javascript or mail"));
		}
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}