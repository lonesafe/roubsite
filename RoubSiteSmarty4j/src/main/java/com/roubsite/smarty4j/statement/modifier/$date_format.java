package com.roubsite.smarty4j.statement.modifier;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.Template;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.NullExpression;
import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Modifier;

/**
 * This formats a date and time into the given strftime() format. Dates can be passed to Smarty as
 * java.util.Date objects, java.util.Calendar objects or any string made up of month day year,
 * parsable by php's strtotime(). Designers can then use date_format to have complete control of the
 * formatting of the date. If the date passed to date_format is empty and a second parameter is
 * passed, that will be used as the date to format.
 * 
 * <table border="1">
 * <colgroup> <col align="center" class="param"> <col align="center" class="type"> <col
 * align="center" class="required"> <col align="center" class="default"> <col class="desc">
 * </colgroup> <thead>
 * <tr>
 * <th align="center">Parameter Position</th>
 * <th align="center">Type</th>
 * <th align="center">Required</th>
 * <th align="center">Default</th>
 * <th>Description</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td align="center">1</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center">%b %e, %Y</td>
 * <td>This is the format for the outputted date.</td>
 * </tr>
 * <tr>
 * <td align="center">2</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center">n/a</td>
 * <td>This is the default date if the input is empty.</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * Map<String, Object> config = new HashMap<String, Object>();<br>
 * config.put("date", "%I:%M %p");<br>
 * config.put("time", "%H:%M:%S");<br>
 * context.set("config", config);<br>
 * context.set("now", new Date(115, 0, 1, 14, 33, 0));<br>
 * context.set("yesterday", new Date(114, 11, 31, 14, 33, 0));<br>
 * <br>
 * <b>Template:</b><br>
 * {$now|date_format}<br>
 * {$now|date_format:'%D'}<br>
 * {$now|date_format:$config.date}<br>
 * {$yesterday|date_format}<br>
 * {$yesterday|date_format:'%A, %B %e, %Y'}<br>
 * {$yesterday|date_format:$config.time}<br>
 * <br>
 * <b>Output:</b><br>
 * Jan 1, 2015<br>
 * 01/01/15<br>
 * 02:33 PM<br>
 * Dec 31, 2014<br>
 * Wednesday, December 31, 2014<br>
 * 14:33:00<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $date_format extends Modifier {

	private static final Definition[] definitions = {
	    Definition.forModifier(Type.STROBJ, new StringExpression("%b %e, %Y")),
	    Definition.forModifier(Type.OBJECT, NullExpression.VALUE) };

	private static final Map<String, Locale> locales = new HashMap<String, Locale>();

	static {
		locales.put("CANADA", Locale.CANADA);
		locales.put("CANADA_FRENCH", Locale.CANADA_FRENCH);
		locales.put("CHINA", Locale.CHINA);
		locales.put("CHINESE", Locale.CHINESE);
		locales.put("ENGLISH", Locale.ENGLISH);
		locales.put("FRANCE", Locale.FRANCE);
		locales.put("FRENCH", Locale.FRENCH);
		locales.put("GERMAN", Locale.GERMAN);
		locales.put("GERMANY", Locale.GERMANY);
		locales.put("ITALIAN", Locale.ITALIAN);
		locales.put("ITALY", Locale.ITALY);
		locales.put("JAPAN", Locale.JAPAN);
		locales.put("JAPANESE", Locale.JAPANESE);
		locales.put("KOREA", Locale.KOREA);
		locales.put("KOREAN", Locale.KOREAN);
		locales.put("PRC", Locale.PRC);
		locales.put("SIMPLIFIED_CHINESE", Locale.SIMPLIFIED_CHINESE);
		locales.put("TAIWAN", Locale.TAIWAN);
		locales.put("TRADITIONAL_CHINESE", Locale.TRADITIONAL_CHINESE);
		locales.put("UK", Locale.UK);
		locales.put("US", Locale.US);
	}

	private static final SimpleDateFormat simple = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private Locale locale;

	/**
	 * This formats a date and time into the given strftime() format.
	 * 
	 * @param obj
	 * @param format
	 * @param defValue
	 * @return
	 */
	public Object execute(Object obj, String format, Object defValue) {
		Calendar calendar;
		if (obj instanceof Calendar) {
			calendar = (Calendar) obj;
		} else {
			calendar = Calendar.getInstance(locale);
			if (obj instanceof String) {
				String data = StringUtils.rightPad((String) obj, 13, "0");
				obj = strtotime(data);
			} else if (obj instanceof Integer) {
				String data = Integer.toString((int) obj);
				data = StringUtils.rightPad(data, 13, "0");
				obj = strtotime(data);
			} else if (obj instanceof Long) {
				String data = Long.toString((Long) obj);
				data = StringUtils.rightPad(data, 13, "0");
				obj = strtotime(data);
			} else if (!(obj instanceof Date)) {
				obj = null;
			}

			if (obj != null) {
				calendar.setTime((Date) obj);
			} else {
				return defValue;
			}
		}

		DateFormatSymbols symbols = new DateFormatSymbols(locale);
		StringBuilder buf = new StringBuilder(64);

		for (int i = 0, len = format.length(); i < len; i++) {
			char c = format.charAt(i);
			if (c == '%' && ++i < len) {
				switch (format.charAt(i)) {
				case 'a':
					buf.append(symbols.getShortWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)]);
					continue;
				case 'A':
					buf.append(symbols.getWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)]);
					continue;
				case 'b':
				case 'h':
					buf.append(symbols.getShortMonths()[calendar.get(Calendar.MONTH)]);
					continue;
				case 'B':
					buf.append(symbols.getMonths()[calendar.get(Calendar.MONTH)]);
					continue;
				case 'c':
					buf.append(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, locale)
					    .format(calendar.getTime()));
					continue;
				case 'C':
					appendTwoChar(buf, calendar.get(Calendar.YEAR) / 100);
					continue;
				case 'd':
					appendTwoChar(buf, calendar.get(Calendar.DAY_OF_MONTH));
					continue;
				case 'D':
					appendTwoChar(buf, calendar.get(Calendar.MONTH) + 1);
					buf.append('/');
					appendTwoChar(buf, calendar.get(Calendar.DAY_OF_MONTH));
					buf.append('/');
					appendTwoChar(buf, calendar.get(Calendar.YEAR) % 100);
					continue;
				case 'e':
					buf.append(calendar.get(Calendar.DAY_OF_MONTH));
					continue;
				case 'F':
					appendTwoChar(buf, calendar.get(Calendar.YEAR) % 100);
					buf.append('-');
					appendTwoChar(buf, calendar.get(Calendar.MONTH) + 1);
					buf.append('-');
					appendTwoChar(buf, calendar.get(Calendar.DAY_OF_MONTH));
					continue;
				case 'g':
					calendar.add(Calendar.DATE, 4 - ((calendar.get(Calendar.DAY_OF_WEEK) + 6) % 7 + 1));
					appendTwoChar(buf, calendar.get(Calendar.YEAR) % 100);
				case 'G':
					calendar.add(Calendar.DATE, 4 - ((calendar.get(Calendar.DAY_OF_WEEK) + 6) % 7 + 1));
					appendTwoChar(buf, calendar.get(Calendar.YEAR));
				case 'H':
					appendTwoChar(buf, calendar.get(Calendar.HOUR_OF_DAY));
					continue;
				case 'I':
					appendTwoChar(buf, calendar.get(Calendar.HOUR));
					continue;
				case 'j': {
					int day = calendar.get(Calendar.DAY_OF_YEAR);
					buf.append((char) ((day / 100) + '0'));
					appendTwoChar(buf, day % 100);
					continue;
				}
				case 'k':
					buf.append(calendar.get(Calendar.HOUR_OF_DAY));
					continue;
				case 'l':
					buf.append(calendar.get(Calendar.HOUR));
					continue;
				case 'm':
					appendTwoChar(buf, calendar.get(Calendar.MONTH) + 1);
					continue;
				case 'M':
					appendTwoChar(buf, calendar.get(Calendar.MINUTE));
					continue;
				case 'n':
					buf.append('\n');
					continue;
				case 'p':
					buf.append(symbols.getAmPmStrings()[calendar.get(Calendar.AM_PM)]);
					continue;
				case 'r':
					buf.append((calendar.get(Calendar.HOUR) + 11) % 12 + 1);
					continue;
				case 'R':
					buf.append((calendar.get(Calendar.HOUR_OF_DAY) + 23) % 24 + 1);
					continue;
				case 'S':
					appendTwoChar(buf, calendar.get(Calendar.SECOND));
					continue;
				case 't':
					buf.append('\t');
					continue;
				case 'T':
					appendTwoChar(buf, calendar.get(Calendar.HOUR_OF_DAY));
					buf.append(':');
					appendTwoChar(buf, calendar.get(Calendar.MINUTE));
					buf.append(':');
					appendTwoChar(buf, calendar.get(Calendar.SECOND));
					continue;
				case 'u':
					buf.append((calendar.get(Calendar.DAY_OF_WEEK) + 6) % 7 + 1);
					continue;
				case 'U':
					buf.append(calendar.get(Calendar.WEEK_OF_YEAR));
					continue;
				case 'V': {
					calendar.add(Calendar.DATE, 4 - ((calendar.get(Calendar.DAY_OF_WEEK) + 6) % 7 + 1));
					int day = calendar.get(Calendar.DAY_OF_YEAR) - 1;
					buf.append(1 + day / 7 + (day % 7 + (7 - calendar.get(Calendar.DAY_OF_WEEK)) % 7) / 7);
					continue;
				}
				case 'w':
					buf.append(calendar.get(Calendar.DAY_OF_WEEK) - 1);
					continue;
				case 'W': {
					int day = calendar.get(Calendar.DAY_OF_YEAR) - 1;
					buf.append(1 + day / 7 + (day % 7 + (7 - calendar.get(Calendar.DAY_OF_WEEK)) % 7) / 7);
					continue;
				}
				case 'x':
					buf.append(DateFormat.getDateInstance(DateFormat.LONG, locale).format(calendar.getTime()));
					continue;
				case 'X':
					buf.append(DateFormat.getTimeInstance(DateFormat.LONG, locale).format(calendar.getTime()));
					continue;
				case 'y':
					appendTwoChar(buf, calendar.get(Calendar.YEAR) % 100);
					continue;
				case 'Y':
					buf.append(calendar.get(Calendar.YEAR));
					continue;
				case 'z':
				case 'Z':
					buf.append(calendar.getTimeZone().getDisplayName(true, TimeZone.SHORT, locale));
					continue;
				case '%':
					buf.append('%');
					continue;
				}
			} else {
				buf.append(c);
			}
		}
		return buf.toString();
	}

	private void appendTwoChar(StringBuilder buf, int value) {
		buf.append((char) (value / 10 + '0'));
		buf.append((char) (value % 10 + '0'));
	}

	private Date strtotime(String s) {
		if (s.equals("now")) {
			return new Date();
		} else {
			try {
				Long l = new Long(s);
				String d = simple.format(l);
				return simple.parse(d);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Override
	public void createParameters(Template tpl, List<Expression> values) throws ParseException {
		super.createParameters(tpl, values);
		if (values.size() > 2) {
			Expression exp = values.get(2);
			if (exp instanceof StringExpression) {
				locale = locales.get(exp.toString());
				if (locale == null) {
					throw new ParseException(String.format(MessageFormat.IS_NOT_FOUND,
					    "The locale(" + exp.toString() + ")"));
				}
				return;
			}
		}
		locale = Locale.getDefault();
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}