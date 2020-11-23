package com.roubsite.smarty4j.statement.function;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.Context;
import com.roubsite.smarty4j.Engine;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.SafeContext;
import com.roubsite.smarty4j.Template;
import com.roubsite.smarty4j.TemplateReader;
import com.roubsite.smarty4j.TemplateWriter;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.expression.Expression;
import com.roubsite.smarty4j.expression.MapExpression;
import com.roubsite.smarty4j.expression.NullExpression;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.LineFunction;

/**
 * The tag is used for including other templates in the current template. Any
 * variables available in the current template are also available within the
 * included template.
 *
 * <table border="1">
 * <colgroup> <col align="center" class="param">
 * <col align="center" class="type"> <col align="center" class="required">
 * <col align="center" class="default"> <col class="desc"> </colgroup> <thead>
 * <tr>
 * <th align="center">Attribute Name</th>
 * <th align="center">Type</th>
 * <th align="center">Required</th>
 * <th align="center">Default</th>
 * <th>Description</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td align="center">file</td>
 * <td align="center">string</td>
 * <td align="center">Yes</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>The name of the template file to include</td>
 * </tr>
 * <tr>
 * <td align="center">assign</td>
 * <td align="center">string</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>The name of the variable that the output of include will be assigned
 * to</td>
 * </tr>
 * <tr>
 * <td align="center">[var ...]</td>
 * <td align="center">[var type]</td>
 * <td align="center">No</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>variable to pass local to template</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <table border="1">
 * <colgroup> <col align="center" class="param"> <col class="desc"> </colgroup>
 * <thead>
 * <tr>
 * <th align="center">Option Name</th>
 * <th>Description</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td align="center">inline</td>
 * <td>If set merge the compile code of the subtemplate into the compiled
 * calling template</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $include extends LineFunction {

	/** 参数定义 */
	private static final Definition[] definitions = { Definition.forFunction("file", Type.STROBJ),
			Definition.forFunction("assign", Type.STRING, NullExpression.VALUE),
			Definition.forFunction("", Type.MAP, NullExpression.VALUE) };

	public static Object execute(SafeContext ctx, TemplateWriter writer, String file, String assign,
			Map<String, Object> data) throws Exception {
		if (assign != null) {
			writer = TemplateWriter.getTemporaryWriter();
		}

		// 加载子模板, 设置子模板的父容器
		Template tpl = ctx.getTemplate();
		Engine engine = tpl.getEngine();
		tpl = engine.getTemplate(tpl.getRelativePath(file));
		Context cc = new Context((Context) ctx);
		cc.putAll(data);
		tpl.merge(cc, writer);

		if (assign != null) {
			return writer.toString();
		}
		return null;
	}

	@Override
	public void createParameters(Definition[] parameters, Map<String, Expression> fields) throws ParseException {
		super.createParameters(parameters, fields);
		// 移除必须存在的参数
		fields.remove("file");
		fields.remove("assign");
		PARAMETERS[2] = new MapExpression(fields);
	}

	@Override
	public void analyzeContent(Analyzer analyzer, TemplateReader reader) throws ParseException {
		if (contain("inline")) {
			Template tpl = analyzer.getTemplate();
			String name;
			InputStream is;
			try {
				name = tpl.getRelativePath(PARAMETERS[0].toString());
				File file = new File(tpl.getEngine().getTemplatePath() + name);
				if (file.exists()) {
					is = new FileInputStream(file);
					tpl.associate(file);
				} else {
					is = Engine.class.getClassLoader().getResourceAsStream(name);
				}

				reader.insertReader(name, new TemplateReader(new InputStreamReader(is, tpl.getEngine().getCharset())));
			} catch (IOException e) {
				reader.addMessage(e.getMessage());
			}
		} else {
			super.analyzeContent(analyzer, reader);
			analyzer.getVariableManager().forcedRewrite();
		}
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
		if (contain("inline")) {
			for (Entry<String, Expression> entry : ((MapExpression) PARAMETERS[2]).getValue().entrySet()) {
				entry.getValue().parseObject(mv, local, vm);
				writeVariable(mv, local, vm, entry.getKey(), null);
			}
		} else {
			super.parse(mv, local, vm);
		}
	}
}