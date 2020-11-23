package com.roubsite.smarty4j.statement;

import java.util.HashMap;
import java.util.Map;

import com.roubsite.smarty4j.Analyzer;
import com.roubsite.smarty4j.Node;
import com.roubsite.smarty4j.ParseException;
import com.roubsite.smarty4j.TemplateReader;
import com.roubsite.smarty4j.statement.function.$block;
import com.roubsite.smarty4j.statement.function.$extends;

/**
 * 文档语句，它表示整个文档节点树的根节点。
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class Document extends Block {

	private boolean extend;
	private Map<String, $block> blocks = new HashMap<String, $block>();

	public Document(Analyzer analyzer, TemplateReader reader) {
		this(analyzer, reader, null);
	}

	public Document(Analyzer analyzer, TemplateReader reader, Block parent) {
		if (parent != null) {
			try {
				setParent(parent);
			} catch (ParseException e) {
				reader.addMessage(e);
			}
		}
		analyzeContent(analyzer, reader);
	}

	public $block addBlock(String name, $block block) {
		if (extend) {
			$block ret = blocks.get(name);
			blocks.put(name, block);
			return ret;
		} else {
			return null;
		}
	}

	@Override
	public void addStatement(Node child) throws ParseException {
		if (extend) {
			if (child instanceof $block) {
				super.addStatement(child);
			} else if (!(child instanceof DebugStatement)
			    && (!(child instanceof TextStatement) || !((TextStatement) child).getText().trim()
			        .isEmpty())) {
				throw new ParseException(
				    "The child templates can not define any content besides what's inside {block} tags they override.");
			}
		} else {
			if (child instanceof $extends) {
				for (Node node : children) {
					if (!(node instanceof DebugStatement)
					    && (!(node instanceof TextStatement) || !((TextStatement) node).getText().trim()
					        .isEmpty())) {
						throw new ParseException("{extends} tag must be the first line in the child template.");
					}
				}
				children.clear();
				extend = true;
			}
			super.addStatement(child);
		}
	}
}