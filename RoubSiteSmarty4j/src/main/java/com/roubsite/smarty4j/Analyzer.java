package com.roubsite.smarty4j;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.ListExpression;
import com.roubsite.smarty4j.expression.ListExtended;
import com.roubsite.smarty4j.expression.MapExpression;
import com.roubsite.smarty4j.expression.MapExtended;
import com.roubsite.smarty4j.expression.MixedStringExpression;
import com.roubsite.smarty4j.expression.ModifierExtended;
import com.roubsite.smarty4j.expression.NullExpression;
import com.roubsite.smarty4j.expression.ObjectAdapter;
import com.roubsite.smarty4j.expression.ObjectExpression;
import com.roubsite.smarty4j.expression.PropertyExpression;
import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.expression.VariableExpression;
import com.roubsite.smarty4j.expression.number.ConstDouble;
import com.roubsite.smarty4j.expression.number.ConstInteger;
import com.roubsite.smarty4j.statement.Modifier;
import com.roubsite.smarty4j.util.SimpleStack;

/**
 * The Smarty's syntax analyser.
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class Analyzer {

	private static final Charset utf8Charset = Charset.forName("UTF-8");

	private Engine engine;
	private Template tpl;
	private VariableManager vm;
	private SimpleStack tokens = new SimpleStack();
	private int pos;
	private int end;
	private Map<String, String> declares;
	private Map<String, Class<?>> props = new HashMap<String, Class<?>>();
	private Map<String, Integer> texts = new HashMap<String, Integer>();

	/**
	 * Constructs an analyzer with a template for delegation.
	 * 
	 * @param tpl the template
	 */
	public Analyzer(Template tpl) {
		engine = tpl.getEngine();
		this.tpl = tpl;
	}

	/**
	 * Returns a template object associated with it.
	 * 
	 * @return a template object associated with it
	 */
	public Template getTemplate() {
		return tpl;
	}

	/**
	 * Returns the manager of variable associated with it.
	 * 
	 * @return the manager of variable associated with it
	 */
	public VariableManager getVariableManager() {
		return vm;
	}

	/**
	 * The manager of variable to be associated with the analyzer.
	 * 
	 * @param vm the manager of variable
	 */
	public void setVariableManager(VariableManager vm) {
		this.vm = vm;
	}

	/**
	 * Returns the classname associated with the specified variable's name.
	 * 
	 * @param varName vriable's name
	 * @return the classname associated with the specified variable's name
	 */
	public String getDeclared(String varName) {
		if (declares != null) {
			return declares.get(varName);
		}
		return null;
	}

	/**
	 * Associates the classname with the specified variable's name.
	 * 
	 * @param varName   variable's name
	 * @param className the classname to be associated with the specified variable's
	 *                  name
	 */
	public void setDeclared(String varName, String className) {
		if (declares == null) {
			declares = new HashMap<String, String>();
		}
		declares.put(varName, className.replace('.', '/'));
	}

	/**
	 * Binding property, make the specified variables can support the var@key
	 * syntax, see {@link com.roubsite.smarty4j.statement.function.$foreach}.
	 * 
	 * @param varName variable's name
	 * @param clazz   variable's class
	 */
	public void bindProperty(String varName, Class<?> clazz) {
		if (clazz != null) {
			props.put(varName, clazz);
		} else {
			props.remove(varName);
		}
	}

	/**
	 * Returns the text's index, provided OutputStream rendering and generated from
	 * the text.
	 * 
	 * @param text text segment in the template
	 * @return the text's index
	 */
	public int getTextIndex(String text) {
		int ret = tpl.bytes.size();
		if (!texts.containsKey(text)) {
			byte[] bytes = text.getBytes(engine.getCharset());
			tpl.bytes.push(bytes);
			if (text.getBytes(utf8Charset).length > 65535 || ret >= 65534) {
				tpl.strs.push(text);
			} else {
				tpl.strs.push(null);
			}
			texts.put(text, ret);
		} else {
			ret = texts.get(text);
		}
		return ret;
	}

	/**
	 * Attempts to match the line against the statement.
	 * 
	 * @param line the string to be matched
	 * @return the list will store all token of the statement, but the last 2
	 *         elements are the start index and the end index of the statement
	 *         match, {@code null} if the line no contains a smarty statement
	 * @throws ParseException If the statement's syntax is invalid
	 */
	public SimpleStack lexical(String line) throws ParseException {
		tokens.setSize(0);

		String ldelim = engine.getLeftDelimiter();
		String rdelim = engine.getRightDelimiter();
		// check left/right delimiter
		int lIndex = line.indexOf(ldelim);
		if (lIndex < 0) {
			return null;
		}
		int rIndex = line.indexOf(rdelim, lIndex);
		if (rIndex < 0) {
			return null;
		}

		int lLen = ldelim.length();
		int rLen = rdelim.length();
		pos = lIndex + lLen;
		end = line.length() - rLen;
		lexical: while (true) {
			if (pos > end) {
				return null;
			}
			if (pos > rIndex) {
				// the right delimiter between two quotes, it is part of string, this will find
				// new right
				// delimiter
				rIndex = line.indexOf(rdelim, pos);
				if (rIndex < 0) {
					return null;
				}
			}
			if (pos == rIndex) {
				break;
			}
			Object value = findValue(line, true, '\0');
			if (!(value instanceof Character)) {
				tokens.push(value);
				continue;
			}
			char c = (Character) value;
			switch (c) {
			case '*':
				if (tokens.size() == 0) {
					// line comment(multi-line comment see BlockStatement#process)
					for (int i = rIndex - 1;; i--) {
						c = line.charAt(i);
						if (!Character.isWhitespace(c)) {
							tokens.push(Operator.MUL);
							tokens.push(line.substring(pos + 1, rIndex));
							if (c == '*' && i != pos) {
								tokens.push(Operator.MUL);
							}
							break lexical;
						}
					}
				}
			case '@':
			case '(':
			case ')':
			case '!':
			case '~':
			case '/':
			case '%':
			case '+':
			case '-':
			case '<':
			case '>':
			case '=':
			case '&':
			case '^':
			case '|':
			case ',':
			case '#':
				tokens.push(findOperator(line, c));
				continue;
			default:
				tokens.push(c);
			}
			pos++;
		}
		tokens.push(lIndex);
		tokens.push(rIndex + rLen);
		return tokens;
	}

	private Object findOperator(String line, char first) {
		int len = 0;
		int sum = first;
		loop: for (pos++; pos < end; pos++) {
			char c = line.charAt(pos);
			switch (c) {
			case '=':
				if ((len > 1 || (first != '!' && first != '=')) && (len > 0 || (first != '<' && first != '>'))) {
					break loop;
				}
				break;
			case '|':
				if (len > 0 || first != '|') {
					break loop;
				}
				break;
			case '&':
				if (len > 0 || first != '&') {
					break loop;
				}
				break;
			case '<':
				if (len > 1 || first != '<') {
					break loop;
				}
				break;
			case '>':
				if (len > 1 || first != '>') {
					break loop;
				}
				break;
			default:
				break loop;
			}
			len++;
			sum += c;
		}
		Operator op = null;
		switch (len) {
		case 0:
			switch (sum) {
			case '(':
				op = Operator.LGROUP;
				break;
			case ')':
				op = Operator.RGROUP;
				break;
			case '@':
				op = Operator.AT;
				break;
			case '~':
				op = Operator.BNOT;
				break;
			case '!':
				op = Operator.NOT;
				break;
			case '/':
				op = Operator.DIV;
				break;
			case '*':
				op = Operator.MUL;
				break;
			case '%':
				op = Operator.MOD;
				break;
			case '+':
				op = Operator.ADD;
				break;
			case '-':
				op = Operator.SUB;
				break;
			case '>':
				op = Operator.GT;
				break;
			case '<':
				op = Operator.LT;
				break;
			case '&':
				op = Operator.BAND;
				break;
			case '^':
				op = Operator.BXOR;
				break;
			case '|':
				op = Operator.BOR;
				break;
			case '=':
				op = Operator.SET;
				break;
			case ',':
				op = Operator.COMMA;
				break;
			case '#':
				op = Operator.CONFIG;
				break;
			}
			break;
		case 1:
			switch (sum) {
			case '<' + '<':
				op = Operator.SHL;
				break;
			case '>' + '>':
				op = Operator.SHR;
				break;
			case '>' + '=':
				op = Operator.GTE;
				break;
			case '<' + '=':
				op = Operator.LTE;
				break;
			case '=' + '=':
				op = Operator.EQ;
				break;
			case '!' + '=':
				op = Operator.NEQ;
				break;
			case '&' + '&':
				op = Operator.AND;
				break;
			case '|' + '|':
				op = Operator.OR;
				break;
			}
			break;
		case 2:
			switch (sum) {
			case '<' + '<' + '<':
				op = Operator.SHL;
				break;
			case '>' + '>' + '>':
				op = Operator.SAR;
				break;
			case '=' + '=' + '=':
				op = Operator.AEQ;
				break;
			case '!' + '=' + '=':
				op = Operator.ANE;
				break;
			}
			break;
		}
		if (op != null) {
			return op;
		} else {
			return first;
		}
	}

	private Expression findNumber(String line) throws ParseException {
		int start = pos;
		boolean isFloat = false;
		loop: for (pos++; pos < end; pos++) {
			switch (line.charAt(pos)) {
			case '.':
				if (isFloat) {
					break loop;
				} else {
					isFloat = true;
				}
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				continue;
			default:
				break loop;
			}
		}

		String token = line.substring(start, pos);
		return isFloat ? new ConstDouble(Double.parseDouble(token)) : new ConstInteger(Integer.parseInt(token));
	}

	private String findIdentifier(String line) {
		int start = pos;
		for (pos++; pos < end; pos++) {
			if (!Character.isJavaIdentifierPart(line.charAt(pos))) {
				break;
			}
		}
		return line.substring(start, pos);
	}

	private Expression findString(String line, char terminator) throws ParseException {
		pos++;
		MixedStringExpression exp = null;
		StringBuilder sb = new StringBuilder();
		int end = line.length();
		while (pos < end) {
			char c = line.charAt(pos);
			switch (c) {
			case '$':
				if (pos + 1 >= end || !Character.isJavaIdentifierStart(line.charAt(pos + 1))) {
					break;
				}
			case '`':
				if (exp == null) {
					exp = new MixedStringExpression();
				}
				exp.add(sb.toString());
				sb.setLength(0);
				if (c == '$') {
					tokens.push(findVariable(line));
				} else {
					tokens.push(findExpression(line, '`'));
				}
				exp.add((Expression) tokens.pop());
				continue;
			case '\\':
				// escape character
				pos++;
				c = line.charAt(pos);
				switch (c) {
				case 'n':
					c = '\n';
					break;
				case 'r':
					c = '\r';
					break;
				case 't':
					c = '\t';
					break;
				case '`':
				case '$':
				case '"':
				case '\'':
				case '\\':
				case '.':
				case '[':
				case ']':
					break;
				default:
					sb.append('\\');
				}
				break;
			default:
				if (c == terminator) {
					pos++;
					if (exp == null) {
						return new StringExpression(sb.toString());
					} else {
						exp.add(sb.toString());
						return exp;
					}
				}
			}
			sb.append(c);
			pos++;
		}
		throw new ParseException(String.format(MessageFormat.SYNTAX_ERROR_ON_TOKEN, Character.toString(terminator)));
	}

	private Object findVariable(String line) throws ParseException {
		pos++;
		if (!Character.isJavaIdentifierStart(line.charAt(pos))) {
			// illegal identifier
			tokens.push('$');
			return '$';
		}
		String s = findIdentifier(line);
		ObjectExpression var;
		if (props.containsKey(s) && pos + 2 < end && line.charAt(pos) == '@'
				&& Character.isJavaIdentifierStart(line.charAt(pos + 1))) {
			pos++;
			var = new PropertyExpression(this, s + "@", findIdentifier(line), props.get(s));
		} else {
			var = new VariableExpression(this, s);
		}
		loop: while (pos < end) {
			char c = line.charAt(pos);
			switch (c) {
			case '\t':
			case '\r':
			case ' ':
				pos++;
				continue;
			case '[': {
				var.add(new ListExtended(findExpression(line, ']')));
				continue;
			}
			case '.':
				c = line.charAt(pos + 1);
				Expression exp;
				if (c == '`') {
					pos++;
					exp = findExpression(line, '`');
				} else if (!Character.isJavaIdentifierStart(c)) {
					break loop;
				} else {
					pos++;
					String word = findIdentifier(line);
					if (word.charAt(0) == '$') {
						exp = new VariableExpression(this, word.substring(1));
					} else if (line.charAt(pos) == '#') {
						// quickly visit javabean's property
						int start = pos;
						for (pos++; pos < end; pos++) {
							c = line.charAt(pos);
							if (!Character.isJavaIdentifierPart(c) && c != '/') {
								break;
							}
						}
						exp = new StringExpression(word + line.substring(start, pos));
					} else {
						exp = new StringExpression(word);
					}
				}
				var.add(new MapExtended(exp));
				continue;
			default:
				break loop;
			}
		}
		return var;
	}

	private Expression findExpression(String line, char terminator) throws ParseException {
		pos++;
		int size = tokens.size();
		while (pos < end) {
			Object value = findValue(line, true, terminator);
			if (!(value instanceof Character)) {
				tokens.push(value);
				continue;
			}
			char c = (Character) value;
			if (c == terminator) {
				Expression exp = Operator.merge(tokens, size, tokens.size(),
						(terminator != '`' ? Operator.INTEGER : Operator.FLOAT) | Operator.OBJECT);
				tokens.setSize(size);
				pos++;
				return exp;
			}
			switch (c) {
			case '@':
			case '(':
			case ')':
			case '!':
			case '~':
			case '/':
			case '*':
			case '%':
			case '+':
			case '-':
			case '<':
			case '>':
			case '=':
			case '&':
			case '^':
			case '|':
			case ',':
			case '#':
				tokens.push(findOperator(line, c));
				continue;
			default:
				tokens.push(c);
			}
			pos++;
		}
		throw new ParseException(String.format(MessageFormat.SYNTAX_ERROR_ON_TOKEN, Character.toString(terminator)));
	}

	private Expression findList(String line) throws ParseException {
		pos++;
		List<Expression> list = new ArrayList<Expression>();
		boolean isValue = false;
		while (pos < end) {
			Object value = findValue(line, false, '\0');
			if (!(value instanceof Character)) {
				if (isValue) {
					throw new ParseException(String.format(MessageFormat.SYNTAX_ERROR_ON_TOKEN, "VALUE"));
				}
				list.add((Expression) value);
				isValue = true;
				continue;
			}

			char c = (Character) value;
			switch (c) {
			case ',':
				if (isValue) {
					isValue = false;
				} else {
					list.add(NullExpression.VALUE);
				}
				break;
			case ']':
				pos++;
				return new ListExpression(list);
			}
			pos++;
		}
		throw new ParseException(String.format(MessageFormat.SYNTAX_ERROR_ON_TOKEN, "]"));
	}

	private Expression findMap(String line) throws ParseException {
		pos++;
		Map<String, Expression> map = new HashMap<String, Expression>();
		String name = null;
		boolean waitValue = false;
		while (pos < end) {
			Object value = findValue(line, false, '\0');
			if (!(value instanceof Character)) {
				if (name == null && !waitValue) {
					if (value instanceof StringExpression) {
						name = value.toString();
					} else {
						throw new ParseException(String.format(MessageFormat.SYNTAX_ERROR_ON_TOKEN, "NAME"));
					}
				} else if (name != null && waitValue) {
					map.put(name, (Expression) value);
					name = null;
				} else {
					throw new ParseException(String.format(MessageFormat.SYNTAX_ERROR_ON_TOKEN, "VALUE"));
				}
				continue;
			}

			char c = (Character) value;
			switch (c) {
			case ':':
				if (name == null || waitValue) {
					throw new ParseException(String.format(MessageFormat.SYNTAX_ERROR_ON_TOKEN, "null"));
				}
				waitValue = true;
				break;
			case ',':
				if (name != null && waitValue) {
					map.put(name, null);
				}
				waitValue = false;
				break;
			case '}':
				pos++;
				return new MapExpression(map);
			}
			pos++;
		}
		throw new ParseException(String.format(MessageFormat.SYNTAX_ERROR_ON_TOKEN, "}"));
	}

	private Object findValue(String line, boolean useid, char ignore) throws ParseException {
		while (pos < end) {
			char c = line.charAt(pos);
			if (c == ignore) {
				return c;
			}
			switch (c) {
			case '\t':
			case '\r':
			case ' ':
				pos++;
				continue;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '.':
				return findNumber(line);
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case 'G':
			case 'H':
			case 'I':
			case 'J':
			case 'K':
			case 'L':
			case 'M':
			case 'N':
			case 'O':
			case 'P':
			case 'Q':
			case 'R':
			case 'S':
			case 'T':
			case 'U':
			case 'V':
			case 'W':
			case 'X':
			case 'Y':
			case 'Z':
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n':
			case 'o':
			case 'p':
			case 'q':
			case 'r':
			case 's':
			case 't':
			case 'u':
			case 'v':
			case 'w':
			case 'x':
			case 'y':
			case 'z':
			case '_': {
				String s = findIdentifier(line);
				Expression ret = Expression.forString(s);
				if (!(ret instanceof StringExpression)) {
					return ret;
				}
				if (useid) {
					Expression exp = mergeModifier(line, s);
					if (exp == null) {
						return s;
					} else {
						return exp;
					}
				} else {
					return ret;
				}
			}
			case '\'':
			case '"':
				return findString(line, c);
			case '$':
				return findVariable(line);
			case '`':
				return findExpression(line, '`');
			case '[':
				return findList(line);
			case '{':
				return findMap(line);
			default:
				return c;
			}
		}
		return null;
	}

	private Expression mergeModifier(String line, String name) throws ParseException {
		int size = tokens.size();
		if (size < 2) {
			return null;
		}
		// boolean ransack = true;
		Object token = tokens.get(--size);
		if (token == Operator.AT) {
			if (size < 2) {
				return null;
			}
			// ransack = false;
			token = tokens.get(--size);
		}
		if (token == Operator.BOR) {
			token = tokens.get(--size);
		} else {
			return null;
		}

		ObjectExpression exp = null;
		if (token == Operator.RGROUP) {
			for (int i = size - 1;; i--) {
				if (i == -1) {
					return null;
				}
				if (tokens.get(i) == Operator.LGROUP) {
					exp = new ObjectAdapter(Operator.merge(tokens, i + 1, size));
					tokens.setSize(i);
					break;
				}
			}
		} else if (token instanceof ObjectExpression) {
			exp = (ObjectExpression) token;
			tokens.setSize(size);
		} else if (token instanceof Expression) {
			exp = new ObjectAdapter((Expression) token);
			tokens.setSize(size);
		} else {
			return null;
		}

		Modifier modifier = (Modifier) engine.createNode(name, false);
		if (modifier == null) {
			throw new ParseException(String.format(MessageFormat.IS_NOT_FOUND, "The modifier(" + name + ")"));
		}
		List<Expression> values = new ArrayList<Expression>();
		exp.add(new ModifierExtended(modifier));

		boolean colon = false;

		while (pos < end) {
			Object value = findValue(line, false, '\0');
			if (!(value instanceof Character)) {
				if (!colon) {
					break;
				}
				values.add((Expression) value);
				colon = false;
				continue;
			}
			if ((Character) value == ':') {
				if (colon) {
					values.add(null);
				} else {
					colon = true;
				}
				pos++;
				continue;
			}
			break;
		}
		modifier.createParameters(tpl, values);
		return exp;
	}
}
