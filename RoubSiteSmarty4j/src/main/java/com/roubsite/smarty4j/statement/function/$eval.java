package com.roubsite.smarty4j.statement.function;

import java.util.HashMap;
import java.util.Map;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.Context;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.SafeContext;
import com.roubsite.smarty4j.Template;
import com.roubsite.smarty4j.TemplateReader;
import com.roubsite.smarty4j.TemplateWriter;
import com.roubsite.smarty4j.expression.NullExpression;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.LineFunction;

/**
 * The tag is used to evaluate a variable as a template. This can be used for things like embedding
 * template tags/variables into variables or tags/variables into config file variables.
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
 * <td align="center">var</td>
 * <td align="center">mixed</td>
 * <td align="center">Yes</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>Variable (or string) to evaluate</td>
 * </tr>
 * <tr>
 * <td align="center">assign</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>The template variable the output will be assigned to</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $eval extends LineFunction {

	/** 模板缓存 */
	private static Map<String, Template> templates = new HashMap<String, Template>();

	/** 参数定义 */
	private static final Definition[] definitions = {
	    Definition.forFunction("var", Type.STROBJ),
	    Definition.forFunction("assign", Type.STRING, NullExpression.VALUE) };

	@Override
	public String getAssignName() {
		return null;
	}

	public static Object execute(SafeContext ctx, TemplateWriter writer, String text, String assign)
	    throws Exception {
		if (assign != null) {
			writer = TemplateWriter.getTemporaryWriter();
		}

		if (templates.size() > 1024) {
			synchronized ($eval.class) {
				templates.clear();
			}
		}

		// 生成无名模板用于解析字符串
		Template tpl = templates.get(text);
		if (tpl == null) {
			tpl = new Template(ctx.getTemplate().getEngine(), text);
			templates.put(text, tpl);
		}
		tpl.merge((Context) ctx, writer);

		if (assign != null) {
			((Context) ctx).set((String) assign, writer.toString());
		}

		return null;
	}

	@Override
	public void analyzeContent(Analyzer analyzer, TemplateReader reader) throws ParseException {
		analyzer.getVariableManager().preventAllCache();
		super.analyzeContent(analyzer, reader);
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}