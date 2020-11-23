package com.roubsite.smarty4j.statement.function;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.SafeContext;
import com.roubsite.smarty4j.TemplateWriter;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.NullExpression;
import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.expression.check.FalseCheck;
import com.roubsite.smarty4j.expression.check.TrueCheck;
import com.roubsite.smarty4j.expression.number.ConstInteger;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.LineFunction;

/**
 * The tag is used to alternate a set of values. This makes it easy to for example, alternate
 * between two or more colors in a table, or cycle through an array of values.
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
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>default</em></span></td>
 * <td>The name of the cycle</td>
 * </tr>
 * <tr>
 * <td align="center">values</td>
 * <td align="center">mixed</td>
 * <td align="center">Yes</td>
 * <td align="center"><span class="emphasis"><em>N/A</em></span></td>
 * <td>The values to cycle through, either a comma delimited list (see delimiter attribute), or an
 * array of values</td>
 * </tr>
 * <tr>
 * <td align="center">print</td>
 * <td align="center">boolean</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em><code class="constant">TRUE</code></em></span></td>
 * <td>Whether to print the value or not</td>
 * </tr>
 * <tr>
 * <td align="center">advance</td>
 * <td align="center">boolean</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em><code class="constant">TRUE</code></em></span></td>
 * <td>Whether or not to advance to the next value</td>
 * </tr>
 * <tr>
 * <td align="center">delimiter</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>,</em></span></td>
 * <td>The delimiter to use in the values attribute</td>
 * </tr>
 * <tr>
 * <td align="center">assign</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>The template variable the output will be assigned to</td>
 * </tr>
 * <tr>
 * <td align="center">reset</td>
 * <td align="center">boolean</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em><code class="constant">FALSE</code></em></span></td>
 * <td>The cycle will be set to the first value and not advanced</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $cycle extends LineFunction {

	/** 参数定义 */
	private static final Definition[] definitions = {
	    Definition.forFunction("name", Type.STRING, new StringExpression("default")),
	    Definition.forFunction("values", Type.OBJECT, NullExpression.VALUE),
	    Definition.forFunction("print", Type.BOOLEAN, NullExpression.VALUE, "I"),
	    Definition.forFunction("advance", Type.BOOLEAN, TrueCheck.VALUE),
	    Definition.forFunction("delimiter", Type.STRING, new StringExpression(",")),
	    Definition.forFunction("assign", Type.STRING, NullExpression.VALUE),
	    Definition.forFunction("reset", Type.BOOLEAN, FalseCheck.VALUE) };

	private static class Cycle {

		private Object[] objs;
		private int index;
		private boolean print;

		private Object get(boolean isAdvance, boolean reset) {
			if (reset) {
				index = 0;
			}

			Object result = objs[index];

			if (isAdvance) {
				index = (index + 1) % objs.length;
			}

			return result;
		}
	}

	@SuppressWarnings("unchecked")
	public static Object execute(SafeContext ctx, TemplateWriter writer, String name, Object values,
	    int print, boolean advance, String delimiter, String assign, boolean reset)
	    throws IOException {
		Map<String, Object> cycles = (Map<String, Object>) ctx.getProperties("cycle");
		// 获得轮换器对象
		Cycle cycle;
		if (cycles == null) {
			cycles = new HashMap<String, Object>();
			ctx.setProperties("cycle", cycles);
			cycle = null;
		} else {
			cycle = (Cycle) cycles.get(name);
		}
		if (cycle == null) {
			if (values == null) {
				throw new NullPointerException();
			}
			cycle = new Cycle();
			if (print == 2) {
				cycle.print = true;
			}
			cycles.put(name, cycle);
		}

		// 设置轮换器的参数
		Object array = values;
		if (array != null) {
			if (array instanceof Object[]) {
				cycle.objs = (Object[]) array;
			} else if (array instanceof List) {
				cycle.objs = ((List<?>) array).toArray();
			} else if (array instanceof Map) {
				cycle.objs = ((Map<?, ?>) array).values().toArray();
			} else if (array instanceof String) {
				cycle.objs = ((String) array).split(delimiter);
			} else {
				cycle.objs = new Object[] { array };
			}
			cycle.index = 0;
		}
		if (print != 2) {
			cycle.print = print == 1;
		}

		if (assign != null) {
			return cycle.get(advance, reset);
		} else if (cycle.print) {
			writer.write(String.valueOf(cycle.get(advance, reset)));
		} else {
			cycle.get(advance, reset);
		}
		return null;
	}

	@Override
	public void createParameters(Definition[] definitions, Map<String, Expression> fields)
	    throws ParseException {
		super.createParameters(definitions, fields);
		if (PARAMETERS[2] == NullExpression.VALUE) {
			PARAMETERS[2] = new ConstInteger(2);
		}
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}