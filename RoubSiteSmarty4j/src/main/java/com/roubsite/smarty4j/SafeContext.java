package com.roubsite.smarty4j;

import java.util.Map;

/**
 * The methods of following is safety when they are used in the custom function.
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public interface SafeContext {

	/** The internal name of the method's owner class */
	static final String NAME = SafeContext.class.getName().replace('.', '/');

	/**
	 * Returns the parent context for delegation.
	 * 
	 * @return the parent context
	 */
	SafeContext getParent();

	/**
	 * Returns a template object associated with it.
	 * 
	 * @return a template object associated with it
	 */
	Template getTemplate();
	
	/**
	 * Returns collection of capture associated with it.
	 * 
	 * @return collection of capture associated with it
	 */
	Map<String, Object> getCapture();
	
	/**
	 * Returns collection of config associated with it.
	 * 
	 * @return collection of config associated with it
	 */
	Map<String, Object> getConfig();
	
	/**
	 * Returns property's collection of foreach associated with it.
	 * 
	 * @return property's collection of foreach associated with it
	 */
	Map<String, Object> getForeach();
	
	/**
	 * Returns property's collection of section associated with it.
	 * 
	 * @return property's collection of section associated with it
	 */
	Map<String, Object> getSection();
	
	/**
	 * Returns the value of {$smarty.block.parent} associated with it.
	 * 
	 * @return the value of {$smarty.block.parent} associated with it
	 */
	String getBlockParent();
	
	/**
	 * Returns the value of {$smarty.block.child} associated with it.
	 * 
	 * @return the value of {$smarty.block.child} associated with it
	 */
	String getBlockChild();

	/**
	 * Returns the value to which the specified name is mapped, or {@code null} if the collection of
	 * properties contains no mapping for the name. The property is not an object of data container,
	 * it cannot be rendered in template, and only support for the custom function.
	 * 
	 * @param name
	 *          the name of the property
	 * @return the value with the specified name
	 * @see com.roubsite.smarty4j.statement.function.$counter
	 * @see com.roubsite.smarty4j.statement.function.$cycle
	 */
	Object getProperties(String name);

	/**
	 * Associates the specified value with the specified name in collection of properties. If the
	 * collection previously contained a mapping for the name, the old value is replaced. The property
	 * is not an object of data container, it cannot be rendered in template, and only support for the
	 * custom function.
	 * 
	 * @param name
	 *          the name with which the specified value is to be associated
	 * @param value
	 *          the value to be associated with the specified name
	 */
	void setProperties(String name, Object value);
}
