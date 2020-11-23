package com.roubsite.smarty4j;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_5;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import com.roubsite.smarty4j.statement.Document;
import com.roubsite.smarty4j.util.DynamicClassLoader;
import com.roubsite.smarty4j.util.SimpleStack;
import com.roubsite.smarty4j.util.SimpleStringReader;

/**
 * The template class save the information of template file, and built-in
 * template parser that implements the interface. It by calling the merge
 * method, the ability to merge the source data and template files to generate
 * output.
 * 
 * @version 1.1.0, 2015/05/16
 * @author Ouyang Xianwei
 * @since Smarty 1.1
 */
public class Template {

	/** The internal name of the method's owner class */
	public static final String NAME = Template.class.getName().replace('.', '/');

	private static final String[] INTERFACES = { Parser.class.getName().replace('.', '/') };

	SimpleStack bytes = new SimpleStack();
	SimpleStack strs = new SimpleStack();

	private Engine engine;
	private String name;
	private long modified;
	private File file;
	private List<File> associated;
	private SimpleStack nodes = new SimpleStack();
	private Map<String, Template> funcs;
	private Parser parser;

	/**
	 * Constructs a template according to the file.
	 * 
	 * @param engine
	 *            the template engine
	 * @param file
	 *            the file
	 * @throws IOException
	 *             if template file cannot be read
	 * @throws TemplateException
	 *             if syntax error in template
	 */
	public Template(Engine engine, File file) throws IOException, TemplateException {
		this(engine, file.getAbsolutePath().substring(engine.getTemplatePath().length()),
				new InputStreamReader(new FileInputStream(file), engine.getCharset()), true);
		this.file = file;
		modified = file.lastModified();
	}

	/**
	 * Constructs a template according to a string.
	 * 
	 * @param engine
	 *            the template engine
	 * @param text
	 *            the string containing smarty syntax
	 * @throws TemplateException
	 *             if syntax error in template
	 */
	public Template(Engine engine, String text) throws TemplateException {
		this(engine, null, new SimpleStringReader(text), true);
	}

	/**
	 * Constructs a template.
	 * 
	 * @param engine
	 *            the template engine
	 * @param name
	 *            the name of the template file
	 * @param reader
	 *            the reader of the template file
	 * @param resolve
	 *            if <tt>true</tt> then resolve the class
	 * @throws TemplateException
	 *             if syntax error in template
	 */
	public Template(Engine engine, String name, Reader reader, boolean resolve) throws TemplateException {
		this.engine = engine;
		this.name = name;
		TemplateReader tplReader = new TemplateReader(reader);
		VariableManager vm = new VariableManager(engine);
		Analyzer analyzer = new Analyzer(this);
		analyzer.setVariableManager(vm);
		Node doc = new Document(analyzer, tplReader);
		tplReader.checkStatus(name);
		if (resolve) {
			parse(doc, vm);
		}
	}

	/**
	 * Constructs a template.
	 * 
	 * @param engine
	 *            the template engine
	 * @param name
	 *            the name of the template file
	 * @param root
	 *            the syntax tree
	 * @param vm
	 *            variable manager
	 * @throws TemplateException
	 *             if syntax error in template
	 */
	public Template(Engine engine, String name, Node root, VariableManager vm) throws TemplateException {
		this.engine = engine;
		this.name = name;
		parse(root, vm);
	}

	/**
	 * Returns engine which contains the template.
	 * 
	 * @return engine which contains the template
	 */
	public Engine getEngine() {
		return engine;
	}

	/**
	 * Returns the template name.
	 * 
	 * @return the template name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 检测模板文件的更新情况。
	 * 
	 * @return <tt>true</tt>模板相关文件自上次编译后被更新; <tt>false</tt>模板相关文件自上次编译后没有更新.
	 */
	public boolean isUpdated() {
		if (file.lastModified() > modified) {
			return true;
		}
		if (associated != null) {
			for (File file : associated) {
				if (file.lastModified() > modified) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获得相对于模板地址的地址，如果以'/'开头，将取得相对模板控制器根路径的地址， 否则相对于当前模板地址转换。
	 * 
	 * @param path
	 *            相对地址描述
	 * @param isTemplateName
	 *            <tt>true</tt>表示计算模板的名称; <tt>false</tt>表示计算绝对路径
	 * @return 转换后的文件名
	 * @see com.roubsite.smarty4j.statement.function.$include
	 */
	public String getRelativePath(String path) {
		if (name != null && (path.charAt(0) != '/' || path.length() == 0)) {
			int last = name.lastIndexOf('/');
			if (last >= 0) {
				path = name.substring(0, last + 1) + path;
			}
		}
		return path;
	}

	/**
	 * 增加与模板文件相关联的文件。
	 * 
	 * @param file
	 *            需要增加的文件描述对象
	 */
	public void associate(File file) {
		if (associated == null) {
			associated = new ArrayList<File>();
		}
		modified = Math.max(file.lastModified(), modified);
		associated.add(file);
	}

	/**
	 * 获取模板对象中指定的扩展节点。
	 * 
	 * @param index
	 *            扩展节点的编号
	 * @return 扩展节点
	 */
	public Object getNode(int index) {
		return nodes.get(index);
	}

	/**
	 * 往模板对象中新增扩展节点。
	 * 
	 * @param node
	 *            扩展节点
	 * @return 添加成功后扩展节点对应的序号
	 */
	public int addNode(Node node) {
		nodes.push(node);
		return nodes.size() - 1;
	}

	/**
	 * 获取模板中的自定义函数。
	 * 
	 * @param name
	 *            自定义函数名称
	 * @return 自定义函数的模板对象
	 */
	public Template getFunction(String name) {
		return funcs.get(name);
	}

	/**
	 * 向模板中添加自定义函数。
	 * 
	 * @param name
	 *            自定义函数名称
	 * @param root
	 *            自定义函数节点对象
	 * @param vm
	 *            变量管理器
	 */
	public void addFunction(String name, Node root, VariableManager vm) {
		try {
			if (funcs == null) {
				funcs = new HashMap<String, Template>();
			}
			funcs.put(name, new Template(engine, this.name, root, vm));
		} catch (TemplateException e) {
		}
	}

	/**
	 * 获取文本对应的字节数组
	 * 
	 * @param index
	 *            字节数组编号
	 * @return 文本字节数组
	 */
	public byte[] getTextBytes(int index) {
		return (byte[]) bytes.get(index);
	}

	/**
	 * 获取文本对应的字符串
	 * 
	 * @param index
	 *            字节数组编号
	 * @return 文本内容
	 */
	public String getTextString(int index) {
		return (String) strs.get(index);
	}

	/**
	 * 根据数据容器的内容解析模板，将结果输出到指定的nio缓冲区。
	 * 
	 * @param ctx
	 *            数据容器
	 * @param ch
	 *            nio字节流写通道
	 * @throws Exception
	 *             如果合并执行时发生错误
	 */
	public void merge(Context ctx, WritableByteChannel ch) throws Exception {
		TemplateWriter templateWriter = new TemplateWriter(ch, engine.getCharset());
		merge(ctx, templateWriter);
	}

	/**
	 * 根据数据容器的内容解析模板，将结果输出到指定的二进制输出流。
	 * 
	 * @param ctx
	 *            数据容器
	 * @param out
	 *            二进制输出流
	 * @throws Exception
	 *             如果合并执行时发生错误
	 */
	public void merge(Context ctx, OutputStream out) throws Exception {
		TemplateWriter templateWriter = new TemplateWriter(out, engine.getCharset());
		merge(ctx, templateWriter);
	}

	/**
	 * 根据数据容器的内容解析模板，将结果输出到指定的输出对象。
	 * 
	 * @param ctx
	 *            数据容器
	 * @param writer
	 *            输出对象
	 */
	public void merge(Context ctx, Writer writer) throws Exception {
		TemplateWriter templateWriter = new TemplateWriter(writer);
		merge(ctx, templateWriter);
	}

	/**
	 * 根据数据容器的内容解析模板，将结果输出到指定的输出对象。
	 * 
	 * @param ctx
	 *            数据容器
	 * @param out
	 *            输出对象
	 */
	public void merge(Context ctx, TemplateWriter writer) throws Exception {
		ctx.setTemplate(this);
		try {
			parser.merge(ctx, writer);
		} finally {
			writer.flush();
		}
	}

	public void parse(Node root, VariableManager vm) throws TemplateException {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		if (name != null) {
			cw.visitSource(name, null);
		}
		MethodVisitor mv;
		cw.visit(V1_5, ACC_PUBLIC, "tpl", null, "java/lang/Object", INTERFACES);

		// 定义类的构造方法
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// 定义类的merge方法
		mv = cw.visitMethod(ACC_PUBLIC, "merge", "(L" + Context.NAME + ";L" + TemplateWriter.NAME + ";)V", null, null);
		mv.visitVarInsn(ALOAD, Node.CONTEXT);
		if (vm.hasCached()) {
			mv.visitInsn(DUP);
		}
		mv.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "getTemplate", "()L" + Template.NAME + ";");
		mv.visitInsn(DUP);
		mv.visitVarInsn(ASTORE, Node.TEMPLATE);
		mv.visitMethodInsn(INVOKEVIRTUAL, Template.NAME, "getEngine", "()L" + Engine.NAME + ";");
		mv.visitVarInsn(ASTORE, Node.ENGINE);

		// 计算变量缓存占用的堆栈位置
		root.parse(new MethodVisitorProxy(mv), vm.parse(mv, Node.LOCAL_START), vm);

		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		cw.visitEnd();
		byte[] code = cw.toByteArray();
		try {
			this.parser = (Parser) DynamicClassLoader.defineClass("tpl", code).newInstance();
		} catch (Exception e) {
			// 出现概率极低
			throw new RuntimeException(String.format(MessageFormat.CANNOT_BE_INSTANTIATED, "The parser"));
		}
	}
}