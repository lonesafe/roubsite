package com.roubsite.smarty4j.util.json;

import static org.objectweb.asm.Opcodes.*;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import com.roubsite.smarty4j.util.json.JsonIgnore;
import com.roubsite.smarty4j.util.json.JsonInclude;
import com.roubsite.smarty4j.util.json.JsonProperty;
import com.roubsite.smarty4j.util.json.Provider;
import com.roubsite.smarty4j.util.SimpleCharBuffer;
import com.roubsite.smarty4j.util.json.JsonInclude.Include;
import com.roubsite.smarty4j.util.json.ser.ArrayListSerializer;
import com.roubsite.smarty4j.util.json.ser.BooleanArraySerializer;
import com.roubsite.smarty4j.util.json.ser.BooleanSerializer;
import com.roubsite.smarty4j.util.json.ser.ByteArraySerializer;
import com.roubsite.smarty4j.util.json.ser.ByteSerializer;
import com.roubsite.smarty4j.util.json.ser.CharArraySerializer;
import com.roubsite.smarty4j.util.json.ser.CharacterSerializer;
import com.roubsite.smarty4j.util.json.ser.DoubleArraySerializer;
import com.roubsite.smarty4j.util.json.ser.DoubleSerializer;
import com.roubsite.smarty4j.util.json.ser.FloatArraySerializer;
import com.roubsite.smarty4j.util.json.ser.FloatSerializer;
import com.roubsite.smarty4j.util.json.ser.Generic;
import com.roubsite.smarty4j.util.json.ser.IntArraySerializer;
import com.roubsite.smarty4j.util.json.ser.IntegerSerializer;
import com.roubsite.smarty4j.util.json.ser.ListSerializer;
import com.roubsite.smarty4j.util.json.ser.LongArraySerializer;
import com.roubsite.smarty4j.util.json.ser.LongSerializer;
import com.roubsite.smarty4j.util.json.ser.MapSerializer;
import com.roubsite.smarty4j.util.json.ser.ObjectArraySerializer;
import com.roubsite.smarty4j.util.json.ser.ObjectSerializer;
import com.roubsite.smarty4j.util.json.ser.Serializer;
import com.roubsite.smarty4j.util.json.ser.SetSerializer;
import com.roubsite.smarty4j.util.json.ser.ShortArraySerializer;
import com.roubsite.smarty4j.util.json.ser.ShortSerializer;
import com.roubsite.smarty4j.util.json.ser.StringSerializer;
import com.roubsite.smarty4j.util.json.ser.ObjectSerializer.BeanItem;

public class Provider {
	public static final String NAME = Provider.class.getName().replace('.', '/');
	private static final Map<Class<?>, Serializer> defBeanMapper = new HashMap<Class<?>, Serializer>();
	private static final Class<?>[] defAssignables;
	private static Method defineClass;

	static {
		try {
			defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class,
					int.class);
			defineClass.setAccessible(true);
		} catch (Exception ex) {
		}

		defAssignables = new Class<?>[] { Map.class, List.class, Set.class };

		defBeanMapper.put(char.class, CharacterSerializer.instance);
		defBeanMapper.put(boolean.class, BooleanSerializer.instance);
		defBeanMapper.put(byte.class, ByteSerializer.instance);
		defBeanMapper.put(short.class, ShortSerializer.instance);
		defBeanMapper.put(int.class, IntegerSerializer.instance);
		defBeanMapper.put(long.class, LongSerializer.instance);
		defBeanMapper.put(float.class, FloatSerializer.instance);
		defBeanMapper.put(double.class, DoubleSerializer.instance);

		defBeanMapper.put(Character.class, CharacterSerializer.instance);
		defBeanMapper.put(Boolean.class, BooleanSerializer.instance);
		defBeanMapper.put(Byte.class, ByteSerializer.instance);
		defBeanMapper.put(Short.class, ShortSerializer.instance);
		defBeanMapper.put(Integer.class, IntegerSerializer.instance);
		defBeanMapper.put(Long.class, LongSerializer.instance);
		defBeanMapper.put(Float.class, FloatSerializer.instance);
		defBeanMapper.put(Double.class, DoubleSerializer.instance);

		defBeanMapper.put(String.class, StringSerializer.instance);

		defBeanMapper.put(Map.class, MapSerializer.instance);
		defBeanMapper.put(List.class, ListSerializer.instance);
		defBeanMapper.put(Set.class, SetSerializer.instance);

		defBeanMapper.put(ArrayList.class, ArrayListSerializer.instance);
		defBeanMapper.put(char[].class, CharArraySerializer.instance);
		defBeanMapper.put(boolean[].class, BooleanArraySerializer.instance);
		defBeanMapper.put(byte[].class, ByteArraySerializer.instance);
		defBeanMapper.put(short[].class, ShortArraySerializer.instance);
		defBeanMapper.put(int[].class, IntArraySerializer.instance);
		defBeanMapper.put(long[].class, LongArraySerializer.instance);
		defBeanMapper.put(float[].class, FloatArraySerializer.instance);
		defBeanMapper.put(double[].class, DoubleArraySerializer.instance);
	}

	private Map<Class<?>, Serializer> beanMapper;
	private Class<?>[] assignables;
	private int assignableSize;

	public Provider() {
		this.beanMapper = new HashMap<Class<?>, Serializer>(defBeanMapper);
		assignableSize = defAssignables.length;
		this.assignables = new Class[assignableSize];
		System.arraycopy(defAssignables, 0, assignables, 0, assignableSize);
	}

	public void addBeanSerializer(Class<?> beanClass, Serializer beanSerializer) {
		synchronized (beanMapper) {
			beanMapper.put(beanClass, beanSerializer);
		}
	}

	public void addAssignableSerializer(Class<?> baseClass, Serializer beanSerializer) {
		synchronized (beanMapper) {
			Class<?>[] newAssignables = new Class<?>[assignableSize + 1];
			System.arraycopy(assignables, 0, newAssignables, 0, assignableSize);
			newAssignables[assignableSize++] = baseClass;
			assignables = newAssignables;
			beanMapper.put(baseClass, beanSerializer);
		}
	}

	public Serializer getSerializer(Class<?> cc) {
		return this.getSerializer(cc, true);
	}

	public Serializer getSerializer(Class<?> cc, boolean needBuild) {
		Serializer serializer = beanMapper.get(cc);
		if (serializer == null && cc != null) {
			for (Class<?> item : assignables) {
				if (item.isAssignableFrom(cc)) {
					serializer = beanMapper.get(item);
					synchronized (beanMapper) {
						beanMapper.put(cc, serializer);
					}
					break;
				}
			}
			if (serializer == null && needBuild) {
				synchronized (beanMapper) {
					if (cc.isArray()) {
						Class<?> type = cc.getComponentType();
						serializer = new ObjectArraySerializer(type, getSerializer(type));
					} else {
						serializer = beanMapper.get(cc);
					}
					if (serializer == null) {
						serializer = createSerializer(cc);
						beanMapper.put(cc, serializer);
					}
				}
			}
		}
		return serializer;
	}

	private static final int OBJ = 0;
	private static final int CB = OBJ + 1;
	private static final int PROVIDER = CB + 1;
	private static final int VALUE = PROVIDER + 1;
	private static final int INDEX = VALUE + 1;
	private static final int LENGTH = INDEX + 1;

	private void dynamicCall(MethodVisitor mv) {
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
		mv.visitVarInsn(ALOAD, PROVIDER);
		mv.visitInsn(SWAP);
		mv.visitMethodInsn(INVOKEVIRTUAL, Provider.NAME, "getSerializer",
				"(Ljava/lang/Class;)L" + Serializer.NAME + ";");
		mv.visitInsn(SWAP);
		mv.visitVarInsn(ALOAD, CB);
		mv.visitVarInsn(ALOAD, PROVIDER);
		mv.visitMethodInsn(INVOKEINTERFACE, Serializer.NAME, "serialize",
				"(Ljava/lang/Object;L" + SimpleCharBuffer.NAME + ";L" + Provider.NAME + ";)V");
	}

	private void staticCall(MethodVisitor mv, Serializer serializer, Type type) {
		Class<?> generic = null;
		if (serializer instanceof Generic) {
			type = ((Generic) serializer).getGeneric(type);
			if (type instanceof Class) {
				if (Modifier.isFinal(((Class<?>) type).getModifiers())) {
					generic = (Class<?>) type;
				}
			}
		}
		Class<?> clazz = serializer.getClass();
		String name = null;
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getName().charAt(0) == '$') {
				Class<?>[] params = method.getParameterTypes();
				if ((generic != null && params.length == 3) || (generic == null && params.length == 4)) {
					continue;
				}
				name = org.objectweb.asm.Type.getDescriptor(params[0]);
				if (name.length() == 1) {
					name = null;
				} else {
					break;
				}
			}
		}
		if (name == null) {
			throw new RuntimeException(
					"Please provide a static method '$serialize(E value, SimpleCharBuffer cb, Provider provider"
							+ (generic != null ? ", Class cc" : "") + ")' in Class " + serializer.getClass().getName()
							+ " to serialize ‘value’");
		}
		mv.visitVarInsn(ALOAD, CB);
		mv.visitVarInsn(ALOAD, PROVIDER);
		if (generic != null) {
			getSerializer(generic);
			mv.visitLdcInsn(org.objectweb.asm.Type.getType(generic));
			mv.visitMethodInsn(INVOKESTATIC, clazz.getName().replace('.', '/'), "$serialize",
					"(" + name + "L" + SimpleCharBuffer.NAME + ";L" + Provider.NAME + ";Ljava/lang/Class;)V");
		} else {
			mv.visitMethodInsn(INVOKESTATIC, clazz.getName().replace('.', '/'), "$serialize",
					"(" + name + "L" + SimpleCharBuffer.NAME + ";L" + Provider.NAME + ";)V");
		}
	}

	public Serializer createSerializer(Class<?> clazz) {
		ClassLoader loader = clazz.getClassLoader();
		String className = clazz.getName().replace('.', '/');
		String mapperName;
		if (className.startsWith("java/")) {
			loader = Provider.class.getClassLoader();
			mapperName = Provider.NAME + '$' + className.replace('/', '$');
		} else {
			mapperName = className + "$RUIXUS_JSON";
		}

		Include classJsonInclude;
		{
			JsonInclude anno = clazz.getAnnotation(JsonInclude.class);
			classJsonInclude = anno != null ? anno.value() : null;
		}
		Map<String, Object> names = new HashMap<String, Object>();

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		MethodVisitor mv;
		cw.visit(V1_5, ACC_PUBLIC, mapperName, null, ObjectSerializer.NAME, null);

		// 定义类的构造方法
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, ObjectSerializer.NAME, "<init>", "()V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		mv = cw.visitMethod(ACC_PUBLIC, "serialize",
				"(Ljava/lang/Object;L" + SimpleCharBuffer.NAME + ";L" + Provider.NAME + ";)V", null, null);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST, className);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(INVOKESTATIC, mapperName, "$serialize",
				"(L" + className + ";L" + SimpleCharBuffer.NAME + ";L" + Provider.NAME + ";)V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		mv = cw.visitMethod(ACC_PUBLIC, "createObject", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		Constructor<?> constructor = clazz.getConstructors()[0];
		if (constructor.getParameterTypes().length == 0) {
			mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "()V");
		} else {
			String name = constructor.getParameterTypes()[0].getName().replace('.', '/');
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, name);
			mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "(L" + name + ";)V");
		}
		mv.visitInsn(ARETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "$serialize",
				"(L" + className + ";L" + SimpleCharBuffer.NAME + ";L" + Provider.NAME + ";)V", null, null);

		mv.visitVarInsn(ALOAD, CB);
		mv.visitLdcInsn('{');
		mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME, "append", "(C)V");

		try {
			// 序列化JavaBean可读属性
			loop: for (PropertyDescriptor prop : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
				String name = prop.getName();
				// class属性不需要序列化
				if ("class".equals(name)) {
					continue;
				}

				Method accessor = prop.getWriteMethod();
				if (accessor != null) {
					names.put(name, accessor);
				}

				accessor = prop.getReadMethod();
				if (accessor == null) {
					continue;
				}

				Annotation[] annos = accessor.getDeclaredAnnotations();
				Include jsonInclude = classJsonInclude;
				boolean directOutput = false;
				for (Annotation anno : annos) {
					Class<?> cc = anno.annotationType();
					if (cc == JsonIgnore.class) {
						continue loop;
					} else if (cc == JsonProperty.class) {
						name = ((JsonProperty) anno).value();
					} else if (cc == JsonInclude.class) {
						jsonInclude = ((JsonInclude) anno).value();
					} else if (cc == JsonDirectOutput.class) {
						directOutput = true;
					}
				}

				Class<?> type = accessor.getReturnType();
				String typeName = org.objectweb.asm.Type.getDescriptor(type);
				if (typeName.length() == 1) {
					mv.visitVarInsn(ALOAD, CB);
					mv.visitLdcInsn("\"" + name + "\":");
					mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME, "append", "(Ljava/lang/String;)V");

					boolean assign = false;

					for (Annotation anno : annos) {
						Serializer serializer = getSerializer(anno.getClass().getInterfaces()[0], false);
						if (serializer != null) {
							Class<?> cc = serializer.getClass();
							try {
								cc.getMethod("$serialize", type, SimpleCharBuffer.class, Provider.class);
							} catch (Exception ex) {
								throw new RuntimeException("Please provide a static method '$serialize("
										+ type.getName() + " value, SimpleCharBuffer cb, Provider provider)' in Class "
										+ cc.getName() + " to serialize ‘value’");
							}

							mv.visitVarInsn(ALOAD, OBJ);
							mv.visitMethodInsn(INVOKEVIRTUAL, className, accessor.getName(), "()" + typeName);
							mv.visitVarInsn(ALOAD, CB);
							mv.visitVarInsn(ALOAD, PROVIDER);
							mv.visitMethodInsn(INVOKESTATIC, cc.getName().replace('.', '/'), "$serialize",
									"(" + typeName + "L" + SimpleCharBuffer.NAME + ";L" + Provider.NAME + ";)V");
							assign = true;
							break;
						}
					}

					if (!assign) {
						mv.visitVarInsn(ALOAD, CB);
						mv.visitVarInsn(ALOAD, OBJ);
						mv.visitMethodInsn(INVOKEVIRTUAL, className, accessor.getName(), "()" + typeName);
						mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME,
								typeName.equals("C") ? "appendString" : "append",
								"(" + (typeName.equals("B") || typeName.equals("S") ? "I" : typeName) + ")V");
					}

					mv.visitVarInsn(ALOAD, CB);
					mv.visitLdcInsn(',');
					mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME, "append", "(C)V");
				} else {
					Label isnull = new Label();
					Label end = new Label();

					mv.visitVarInsn(ALOAD, OBJ);
					mv.visitMethodInsn(INVOKEVIRTUAL, className, accessor.getName(), "()" + typeName);
					mv.visitVarInsn(ASTORE, VALUE);
					mv.visitVarInsn(ALOAD, VALUE);
					mv.visitJumpInsn(IFNULL, isnull);

					if (jsonInclude == Include.NON_EMPTY) {
						mv.visitVarInsn(ALOAD, VALUE);
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I");
						mv.visitJumpInsn(IFEQ, end);
					}

					boolean assign;

					if (directOutput) {
						assign = true;

						mv.visitVarInsn(ALOAD, CB);
						mv.visitLdcInsn("\"" + name + "\":");
						mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME, "append", "(Ljava/lang/String;)V");

						mv.visitVarInsn(ALOAD, CB);
						mv.visitVarInsn(ALOAD, VALUE);
						if (type != String.class) {
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
						}
						mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME, "append", "(Ljava/lang/String;)V");
					} else {
						assign = false;

						for (Annotation anno : annos) {
							Serializer serializer = getSerializer(anno.getClass().getInterfaces()[0], false);
							if (serializer != null) {
								mv.visitVarInsn(ALOAD, CB);
								mv.visitLdcInsn("\"" + name + "\":");
								mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME, "append",
										"(Ljava/lang/String;)V");

								mv.visitVarInsn(ALOAD, VALUE);
								staticCall(mv, serializer, accessor.getGenericReturnType());
								assign = true;
								break;
							}
						}
					}

					if (!assign) {
						mv.visitVarInsn(ALOAD, CB);
						mv.visitLdcInsn("\"" + name + "\":");
						mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME, "append", "(Ljava/lang/String;)V");

						Serializer serializer = getSerializer(type, false);
						if (serializer != null) {
							mv.visitVarInsn(ALOAD, VALUE);
							staticCall(mv, serializer, accessor.getGenericReturnType());
						} else {
							if (type.isArray()) {
								clazz = type.getComponentType();
								boolean isFinal = Modifier.isFinal(clazz.getModifiers());

								Label condition = new Label();
								Label loop = new Label();

								serializer = getSerializer(clazz);
								mv.visitVarInsn(ALOAD, CB);
								mv.visitLdcInsn('[');
								mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME, "append", "(C)V");

								mv.visitVarInsn(ALOAD, VALUE);
								mv.visitInsn(ARRAYLENGTH);
								mv.visitVarInsn(ISTORE, LENGTH);
								mv.visitInsn(ICONST_0);
								mv.visitVarInsn(ISTORE, INDEX);
								mv.visitJumpInsn(GOTO, condition);

								mv.visitLabel(loop);
								if (isFinal) {
									mv.visitVarInsn(ALOAD, VALUE);
									mv.visitVarInsn(ILOAD, INDEX);
									mv.visitInsn(AALOAD);
									staticCall(mv, serializer, accessor.getGenericReturnType());
								} else {
									mv.visitVarInsn(ALOAD, VALUE);
									mv.visitVarInsn(ILOAD, INDEX);
									mv.visitInsn(AALOAD);
									dynamicCall(mv);
								}
								mv.visitVarInsn(ALOAD, CB);
								mv.visitLdcInsn(',');
								mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME, "append", "(C)V");
								mv.visitIincInsn(INDEX, 1);

								mv.visitLabel(condition);
								mv.visitVarInsn(ILOAD, INDEX);
								mv.visitVarInsn(ILOAD, LENGTH);
								mv.visitJumpInsn(IF_ICMPLT, loop);

								mv.visitVarInsn(ALOAD, CB);
								mv.visitLdcInsn(']');
								mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME, "appendClose", "(C)V");
							} else {
								serializer = getSerializer(type);

								if (Modifier.isFinal(type.getModifiers())) {
									mv.visitVarInsn(ALOAD, VALUE);
									staticCall(mv, serializer, accessor.getGenericReturnType());
								} else {
									mv.visitVarInsn(ALOAD, VALUE);
									dynamicCall(mv);
								}
							}
						}
					}

					mv.visitVarInsn(ALOAD, CB);
					mv.visitLdcInsn(',');
					mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME, "append", "(C)V");

					if (jsonInclude != Include.NON_NULL && jsonInclude != Include.NON_EMPTY) {
						mv.visitJumpInsn(GOTO, end);
						mv.visitLabel(isnull);

						mv.visitVarInsn(ALOAD, CB);
						mv.visitLdcInsn("\"" + name + "\":");
						mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME, "append", "(Ljava/lang/String;)V");

						Serializer serializer = getSerializer(null, false);
						if (serializer != null) {
							mv.visitInsn(ACONST_NULL);
							staticCall(mv, serializer, null);
						} else {
							mv.visitVarInsn(ALOAD, CB);
							mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME, "appendNull", "()V");
						}

						mv.visitVarInsn(ALOAD, CB);
						mv.visitLdcInsn(',');
						mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME, "append", "(C)V");
					} else {
						mv.visitLabel(isnull);
					}

					mv.visitLabel(end);
				}
			}
		} catch (IntrospectionException e) {
		}

		mv.visitVarInsn(ALOAD, CB);
		mv.visitLdcInsn('}');
		mv.visitMethodInsn(INVOKEVIRTUAL, SimpleCharBuffer.NAME, "appendClose", "(C)V");

		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		int size = names.size();
		mv = cw.visitMethod(ACC_PUBLIC, "setValue", "(Ljava/lang/Object;ILjava/lang/Object;)V", null, null);
		Label defaultSet = new Label();
		Label[] switchSet = new Label[size];

		if (size > 0) {
			for (int i = 0; i < size; i++) {
				switchSet[i] = new Label();
			}

			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, className);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitTableSwitchInsn(0, size - 1, defaultSet, switchSet);

			int i = 0;
			for (Map.Entry<String, Object> name : names.entrySet()) {
				Method accessor = (Method) name.getValue();

				Class<?> type = accessor.getParameterTypes()[0];
				String typeName = org.objectweb.asm.Type.getDescriptor(type);
				Type generic = null;
				Serializer serializer = getSerializer(type);
				if (serializer instanceof Generic) {
					generic = ((Generic) serializer).getGeneric(accessor.getGenericParameterTypes()[0]);
				}
				name.setValue(new BeanItem(i, serializer, generic));

				mv.visitLabel(switchSet[i]);
				if (typeName.length() > 1) {
					mv.visitTypeInsn(CHECKCAST, type.getName().replace('.', '/'));
				} else {
					switch (typeName.charAt(0)) {
					case 'I':
						mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
						break;
					case 'B':
						mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
						break;
					case 'C':
						mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "byteValue", "()C");
						break;
					case 'S':
						mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()C");
						break;
					case 'J':
						mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
						break;
					case 'Z':
						mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
						break;
					case 'F':
						mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
						break;
					case 'D':
						mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
						break;
					}
				}
				mv.visitMethodInsn(INVOKEVIRTUAL, className, accessor.getName(), "(" + typeName + ")V");
				mv.visitInsn(RETURN);

				i++;
			}
		}

		mv.visitLabel(defaultSet);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		cw.visitEnd();

		byte[] code = cw.toByteArray();
		try {
			ObjectSerializer serializer = (ObjectSerializer) ((Class<?>) defineClass.invoke(loader,
					mapperName.replace('/', '.'), code, 0, code.length)).newInstance();
			for (Map.Entry<String, Object> name : names.entrySet()) {
				serializer.setNameIndex(name.getKey(), (BeanItem) name.getValue());
			}
			return serializer;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new NullPointerException();
		}
	}
}