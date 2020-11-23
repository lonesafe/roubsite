package com.roubsite.smarty4j.statement.function;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.MethodVisitorProxy;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.Template;
import com.roubsite.smarty4j.TemplateReader;
import com.roubsite.smarty4j.VariableManager;
import com.roubsite.smarty4j.statement.Block;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Function;

/**
 * This is used in child templates in template inheritance for extending parent templates. For
 * details see section of Template Interitance.<br>
 * The {extends} tag must be on the first line of the template.<br>
 * If a child template extends a parent template with the {extends} tag it may contain only {block}
 * tags. Any other template content is ignored.<br>
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
 * <td align="center">file</td>
 * <td align="center">string</td>
 * <td align="center">Yes</td>
 * <td align="center"><span class="emphasis"><em>n/a</em></span></td>
 * <td>The name of the template file which is extended</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $extends extends Function {

	private static final Definition[] definitions = { Definition.forFunction("file", Type.STRING) };

	@Override
	public void analyzeContent(Analyzer analyzer, TemplateReader reader) throws ParseException {
		Block parent = getParent();
		$block block = new $block();
		block.setName("block");
		block.setParent(parent);
		parent.addStatement(block);

		Template tpl = analyzer.getTemplate();
		String name = tpl.getRelativePath(PARAMETERS[0].toString());
		String path = tpl.getEngine().getTemplatePath() + name;
		File file = new File(path);
		tpl.associate(file);
		try {
			reader.unread("{/block}");
			reader.insertReader(name, new TemplateReader(new InputStreamReader(new FileInputStream(file),
			    tpl.getEngine().getCharset())));
		} catch (IOException e) {
			reader.addMessage(e.getMessage());
		}

		block.analyzeContent(analyzer, reader);
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}

	@Override
	public void parse(MethodVisitorProxy mv, int local, VariableManager vm) {
	}
}
