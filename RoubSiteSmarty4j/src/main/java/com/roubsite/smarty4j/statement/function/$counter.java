package com.roubsite.smarty4j.statement.function;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.roubsite.smarty4j.MessageFormat;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.SafeContext;
import com.roubsite.smarty4j.TemplateWriter;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.NullExpression;
import com.roubsite.smarty4j.expression.ObjectAdapter;
import com.roubsite.smarty4j.expression.StringExpression;
import com.roubsite.smarty4j.expression.number.ConstInteger;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.LineFunction;

/**
 * The tag is used to print out a count. {counter} will remember the count on each iteration. You
 * can adjust the number, the interval and the direction of the count, as well as determine whether
 * or not to print the value. You can run multiple counters concurrently by supplying a unique name
 * for each one.
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
 * <td>The name of the counter</td>
 * </tr>
 * <tr>
 * <td align="center">start</td>
 * <td align="center">number</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>1</em></span></td>
 * <td>The initial number to start counting from</td>
 * </tr>
 * <tr>
 * <td align="center">skip</td>
 * <td align="center">number</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>1</em></span></td>
 * <td>The interval to count by</td>
 * </tr>
 * <tr>
 * <td align="center">direction</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>up</em></span></td>
 * <td>The direction to count (up/down)</td>
 * </tr>
 * <tr>
 * <td align="center">print</td>
 * <td align="center">boolean</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em><code class="constant">TRUE</code></em></span></td>
 * <td>Whether or not to print the value</td>
 * </tr>
 * <tr>
 * <td align="center">assign</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>the template variable the output will be assigned to</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $counter extends LineFunction {

	private static final int DEFAULT = 0;
	private static final int UP = 1;
	private static final int DOWN = 2;
	private static final int KEEP = 3;

	/** 参数定义 */
	private static final Definition[] definitions = {
	    Definition.forFunction("name", Type.STRING, new StringExpression("default")),
	    Definition.forFunction("start", Type.INTOBJ, NullExpression.VALUE, "Ljava/lang/Integer;"),
	    Definition.forFunction("skip", Type.INTOBJ, NullExpression.VALUE, "Ljava/lang/Integer;"),
	    Definition.forFunction("direction", Type.STRING, NullExpression.VALUE, "I"),
	    Definition.forFunction("print", Type.BOOLEAN, NullExpression.VALUE, "I"),
	    Definition.forFunction("assign", Type.STRING, NullExpression.VALUE) };

	/**
	 * 计数器对象，保存计数器的信息
	 */
	private static class Counter {

		/** 初始值 */
		private int start = 1;

		/** 步长 */
		private int skip = 1;

		/** 增长方向 */
		private int direction;

		private boolean print;

		/**
		 * 获取计数器的当前值, 同时计数器自动累加
		 * 
		 * @return 计数器的当前值
		 */
		private int get() {
			int result = start;

			switch (direction) {
			case UP:
				start += skip;
				break;
			case DOWN:
				start -= skip;
				break;
			default:
				break;
			}

			return result;
		}
	}

	@SuppressWarnings("unchecked")
	public static Object execute(SafeContext ctx, TemplateWriter writer, String name, Integer start,
	    Integer skip, int direction, int print, String assign) throws IOException {
		Map<String, Object> counters = (Map<String, Object>) ctx.getProperties("counter");
		// 获得计数器
		Counter counter;
		if (counters == null) {
			counters = new HashMap<String, Object>();
			ctx.setProperties("counter", counters);
			counter = null;
		} else {
			counter = (Counter) counters.get(name);
		}
		if (counter == null) {
			counter = new Counter();
			if (start == null) {
				counter.start = 1;
			}
			if (skip == null) {
				counter.skip = 1;
			}
			if (direction == DEFAULT) {
				counter.direction = UP;
			}
			if (print == 2) {
				counter.print = true;
			}
			counters.put(name, counter);
		}

		// 设置计数器参数
		if (start != null) {
			counter.start = start;
		}
		if (skip != null) {
			counter.skip = skip;
		}
		if (direction != 0) {
			counter.direction = direction;
		}
		if (print != 2) {
			counter.print = print == 1;
		}

		if (assign != null) {
			return counter.get();
		} else if (counter.print) {
			writer.write(Integer.toString(counter.get()));
		} else {
			counter.get();
		}
		return null;
	}

	@Override
	public void createParameters(Definition[] definitions, Map<String, Expression> fields)
	    throws ParseException {
		super.createParameters(definitions, fields);
		if (PARAMETERS[1] != NullExpression.VALUE) {
			PARAMETERS[1] = new ObjectAdapter(PARAMETERS[1]);
		}
		if (PARAMETERS[2] != NullExpression.VALUE) {
			PARAMETERS[2] = new ObjectAdapter(PARAMETERS[2]);
		}
		if (PARAMETERS[3] != NullExpression.VALUE) {
			String direction = PARAMETERS[3].toString();
			if (direction.equals("up")) {
				PARAMETERS[3] = new ConstInteger(UP);
			} else if (direction.equals("down")) {
				PARAMETERS[3] = new ConstInteger(DOWN);
			} else if (direction.equals("keep")) {
				PARAMETERS[3] = new ConstInteger(KEEP);
			} else {
				throw new ParseException(String.format(MessageFormat.CANNOT_BE_RESOLVED_TO,
				    "\"direction\"", "up, down 或者 keep"));
			}
		} else {
			PARAMETERS[3] = ConstInteger.ZERO;
		}
		if (PARAMETERS[4] == NullExpression.VALUE) {
			PARAMETERS[4] = new ConstInteger(2);
		}
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}