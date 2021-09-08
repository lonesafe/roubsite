package com.roubsite.web.wrapper.memoryLoader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

import com.roubsite.web.wrapper.ResponseWrapperInterface;

import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class MemoryClassLoader extends URLClassLoader {

	private Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

	/**
	 * 单利默认的
	 */
	private static final MemoryClassLoader defaultLoader = new MemoryClassLoader();

	private MemoryClassLoader() {
		super( ((URLClassLoader) MemoryClassLoader.class.getClassLoader()).getURLs(), Thread.currentThread().getContextClassLoader());
	}

	public static MemoryClassLoader getInstrance() {
		return defaultLoader;
	}

	public void registerJava(String className, String code) {
		this.classBytes.putAll(compile(className, code));
	}

	private static Map<String, byte[]> compile(String className, String code) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager stdManager = compiler.getStandardFileManager(null, null, null);
		try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
			JavaFileObject javaFileObject = manager.makeStringSource(className, code);
			
			// set the classpath
            List<String> options = new ArrayList<String>();

            options.add("-classpath");
            StringBuilder sb = new StringBuilder();
            URLClassLoader urlClassLoader = (URLClassLoader) MemoryClassLoader.class.getClassLoader();
            for (URL url : urlClassLoader.getURLs()) {
                sb.append(url.getFile()).append(File.pathSeparator);
            }
            options.add(sb.toString());
            System.out.println(sb.toString());
            List<String> classes = new ArrayList<>();
            classes.add("javax.servlet.http.Cookie");
            classes.add("javax.servlet.http.HttpServletResponse");
            classes.add("com.roubsite.license.RoubSiteLicense");
            classes.add("com.roubsite.web.wrapper.ResponseWrapperInterface");
            
			JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, classes,
					Arrays.asList(javaFileObject));
			if (task.call())
				return manager.getClassBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] buf = classBytes.get(name);
		if (buf == null) {
			return super.findClass(name);
		}
		classBytes.remove(name);
		return defineClass(name, buf, 0, buf.length);
	}
}

class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
	final Map<String, byte[]> classBytes = new HashMap<String, byte[]>();
	final Map<String, List<JavaFileObject>> classObjectPackageMap = new HashMap<>();
	MemoryJavaFileManager(JavaFileManager fileManager) {
		super(fileManager);
	}

	public Map<String, byte[]> getClassBytes() {
		return new HashMap<String, byte[]>(this.classBytes);
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
		classBytes.clear();
	}

	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse)
			throws IOException {
		Iterable<JavaFileObject> it = super.list(location, packageName, kinds, recurse);

		if (kinds.contains(Kind.CLASS)) {
			final List<JavaFileObject> javaFileObjectList = classObjectPackageMap.get(packageName);
			if (javaFileObjectList != null) {
				if (it != null) {
					for (JavaFileObject javaFileObject : it) {
						javaFileObjectList.add(javaFileObject);
					}
				}
				return javaFileObjectList;
			} else {
				return it;
			}
		} else {
			return it;
		}
	}

	@Override
	public String inferBinaryName(Location location, JavaFileObject file) {
		if (file instanceof MemoryInputJavaClassObject) {
			return ((MemoryInputJavaClassObject) file).inferBinaryName();
		}
		return super.inferBinaryName(location, file);
	}

	@Override
	public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, Kind kind,
			FileObject sibling) throws IOException {
		if (kind == Kind.CLASS) {
			return new MemoryOutputJavaClassObject(className);
		} else {
			return super.getJavaFileForOutput(location, className, kind, sibling);
		}
	}

	JavaFileObject makeStringSource(String className, final String code) {
		String classPath = className.replace('.', '/') + Kind.SOURCE.extension;

		return new SimpleJavaFileObject(URI.create("string:///" + classPath), Kind.SOURCE) {
			@Override
			public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
				return CharBuffer.wrap(code);
			}
		};
	}

	void makeBinaryClass(String className, final byte[] bs) {
		JavaFileObject javaFileObject = new MemoryInputJavaClassObject(className, bs);

		String packageName = "";
		int pos = className.lastIndexOf('.');
		if (pos > 0) {
			packageName = className.substring(0, pos);
		}
		List<JavaFileObject> javaFileObjectList = classObjectPackageMap.get(packageName);
		if (javaFileObjectList == null) {
			javaFileObjectList = new LinkedList<>();
			javaFileObjectList.add(javaFileObject);

			classObjectPackageMap.put(packageName, javaFileObjectList);
		} else {
			javaFileObjectList.add(javaFileObject);
		}
	}

	class MemoryInputJavaClassObject extends SimpleJavaFileObject {
		final String className;
		final byte[] bs;

		MemoryInputJavaClassObject(String className, byte[] bs) {
			super(URI.create("string:///" + className.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
			this.className = className;
			this.bs = bs;
		}

		@Override
		public InputStream openInputStream() {
			return new ByteArrayInputStream(bs);
		}

		public String inferBinaryName() {
			return className;
		}
	}

	class MemoryOutputJavaClassObject extends SimpleJavaFileObject {
		final String className;

		MemoryOutputJavaClassObject(String className) {
			super(URI.create("string:///" + className.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
			this.className = className;
		}

		@Override
		public OutputStream openOutputStream() {
			return new FilterOutputStream(new ByteArrayOutputStream()) {
				@Override
				public void close() throws IOException {
					out.close();
					ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
					byte[] bs = bos.toByteArray();
					classBytes.put(className, bs);
					makeBinaryClass(className, bs);
				}
			};
		}
	}
}
