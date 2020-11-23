package com.roubsite.smarty4j;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * All data and environment's property store in the context when the template is rendered. The
 * context may have parent context, for example, when the template is included in other templates.
 * When searching for an object, find the current priority object container, in case of cannot find,
 * recursive query the parent container.
 * </p>
 * <p>
 * Because of the variable cache, the setter and getter methods is unsafe, please use the variable
 * assign to attribute way read an object in the custom function, and call the
 * {@link template.smarty4j.VariableManager#preventCache} or
 * {@link template.smarty4j.VariableManager#preventAllCache} method set variable cache to disable in
 * the
 * {@link template.smarty4j.statement.Function#process(template.smarty4j.Analyzer, template.smarty4j.TemplateReader)}
 * when write an object in the custom function. If the programmer wants to use the cache while
 * writing an object operations, please refer to
 * {@link template.smarty4j.statement.LineFunction#execute} and
 * {@link template.smarty4j.Node#writeVariable}
 * </p>
 * 
 * @see com.roubsite.smarty4j.Template#merge
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class Context implements SafeContext {

	/** The internal name of the method's owner class */
	public static final String NAME = Context.class.getName().replace('.', '/');

	private Context parent;
	private Template tpl;

	private Map<String, Object> data = new HashMap<String, Object>();

	private Map<String, Object> configs;
	private Map<String, Object> captures;
	private Map<String, Object> foreachs;
	private Map<String, Object> sections;
	private String block_parent;
	private String block_child;

	// the properties is shared with different context when the template is rendered
	private Map<String, Object> props;

	/**
	 * Constructs a context.
	 */
	public Context() {
	}

	/**
	 * Constructs a new context using the specified parent context for delegation.
	 * 
	 * @param parent
	 *          the parent context
	 * @see template.smarty4j.statement.function.$eval
	 * @see template.smarty4j.statement.function.$include
	 */
	public Context(Context parent) {
		this();
		this.parent = parent;
		if (parent.captures != null) {
			captures = new HashMap<String, Object>();
			captures.putAll(parent.captures);
		}
		if (parent.configs != null) {
			configs = new HashMap<String, Object>();
			configs.putAll(parent.configs);
		}
		this.props = parent.props;
	}

	/**
	 * Returns the value to which the specified name is mapped, or {@code null} if this context
	 * contains no mapping for the name.
	 * 
	 * @param name
	 *          the name of the value
	 * @return the value with the specified name
	 */
	public Object get(String name) {
		Object value = data.get(name);
		if (value != null) {
			return value;
		}
		if (parent != null) {
			return parent.get(name);
		} else {
			return null;
		}
	}

	/**
	 * Associates the specified value with the specified name in collection of properties. If the name
	 * is not "smarty" and the collection previously contained a mapping for the name, the old value
	 * is replaced.
	 * 
	 * @param name
	 *          the name with which the specified value is to be associated
	 * @param value
	 *          the value to be associated with the specified name
	 */
	public void set(String name, Object value) {
		data.put(name, value);
	}

	/**
	 * Copies all of the mappings from the specified map to this context. These mappings will replace
	 * any mappings that this context had for any of the names(exclusive of the "smarty") currently in
	 * the specified map.
	 * 
	 * @param map
	 *          mappings to be stored in this context
	 */
	public void putAll(Map<String, Object> map) {
		data.putAll(map);
	}

	/**
	 * Copies all of the properties from the specified bean to this context. These values of property
	 * will replace any mappings that this context had for any of the names of property(exclusive of
	 * the "smarty") currently in the specified bean.
	 * 
	 * @param bean
	 *          the properties of the object provide to this context
	 */
	public void putBean(Object bean) {
		try {
			for (PropertyDescriptor prop : Introspector.getBeanInfo(bean.getClass())
			    .getPropertyDescriptors()) {
				Method accessor = prop.getReadMethod();
				if (accessor != null) {
					String name = prop.getName();
					try {
						data.put(name, accessor.invoke(bean));
					} catch (Exception e) {
					}
				}
			}
		} catch (IntrospectionException e) {
		}
	}

	/**
	 * Set value to {$smarty.block.parent} and {$smarty.block.child}.
	 * 
	 * @param parent
	 *          the value of {$smarty.block.parent}
	 * @param child
	 *          the value of {$smarty.block.child}
	 */
	public void setBlock(String parent, String child) {
		block_parent = parent;
		block_child = child;
	}

	/**
	 * The template to be associated with the context.
	 * 
	 * @param tpl
	 *          the template
	 */
	void setTemplate(Template tpl) {
		this.tpl = tpl;
		Map<String, String> globalConfig = tpl.getEngine().getConfigures();
		if (globalConfig != null) {
			if (configs == null) {
				configs = new HashMap<String, Object>();
			}
			configs.putAll(globalConfig);
		}
	}

	@Override
	public SafeContext getParent() {
		return parent;
	}

	@Override
	public Template getTemplate() {
		return (tpl == null) && (parent != null) ? parent.getTemplate() : tpl;
	}

	@Override
	public Map<String, Object> getCapture() {
		if (captures == null) {
			captures = new HashMap<String, Object>();
		}
		return captures;
	}

	@Override
	public Map<String, Object> getConfig() {
		if (configs == null) {
			configs = new HashMap<String, Object>();
		}
		return configs;
	}

	@Override
	public Map<String, Object> getForeach() {
		if (foreachs == null) {
			foreachs = new HashMap<String, Object>();
		}
		return foreachs;
	}

	@Override
	public Map<String, Object> getSection() {
		if (sections == null) {
			sections = new HashMap<String, Object>();
		}
		return sections;
	}

	@Override
	public String getBlockParent() {
		return block_parent;
	}

	@Override
	public String getBlockChild() {
		return block_child;
	}

	@Override
	public Object getProperties(String name) {
		return props != null ? props.get(name) : null;
	}

	@Override
	public void setProperties(String name, Object value) {
		if (props == null) {
			props = new HashMap<String, Object>();
			Context ctx = parent;
			while (ctx != null) {
				ctx.props = props;
				ctx = ctx.parent;
			}
		}
		props.put(name, value);
	}
}