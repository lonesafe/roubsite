package com.roubsite.smarty4j;

/**
 * The class of template parsing needs to implement this interface.
 * 
 * @see com.roubsite.smarty4j.Template
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public interface Parser {

	/**
	 * Merge data container to the template, and output the render result by the writer.
	 * 
	 * @param ctx
	 *          a context object
	 * @param writer
	 *          the writer of template
	 */
	void merge(Context ctx, TemplateWriter writer);
}