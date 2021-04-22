package com.roubsite.database.dao;

import com.roubsite.database.RSConnection;
import com.roubsite.database.annotation.bean.KeyFields;
import com.roubsite.database.bean.Record;
import com.roubsite.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Types;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EntityDao extends BaseCURD {
	/**
	 * 将带大写字符的字符串转换成“_”的字符串
	 *
	 * @param beanName 待转换名称
	 * @return
	 */
	private String beanName(String beanName) {
		Pattern pattern = Pattern.compile("[A-Z]");
		Matcher matcher = pattern.matcher(beanName);
		StringBuffer sbr = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sbr, "_" + matcher.group());
		}
		matcher.appendTail(sbr);
		return sbr.toString().toUpperCase();
	}

	public final static Logger log = LoggerFactory.getLogger(EntityDao.class);

	/**
	 * 根据bean删除数据
	 *
	 * @param bean 实体类
	 * @return
	 */
	public int delete(Object bean) {
		BeanInfo beanInfo;
		try {
			// 获取类属性
			Class<?> clazz = bean.getClass();
			beanInfo = Introspector.getBeanInfo(clazz);
			Field[] fields = clazz.getDeclaredFields();
			String aiFields = "";
			String idValue = "";
			for (int i = 0; i < fields.length; i++) {
				Field f = fields[i];
				if (f.isAnnotationPresent(KeyFields.class)) {
					aiFields = f.getName();
				}
			}
			// 获取类名
			String className = beanInfo.getBeanDescriptor().getName();
			// 将类名转换成对应的数据库名
			String dbName = beanName(className);
			if (dbName.length() > 1) {
				dbName = dbName.substring(1, dbName.length());
				// 给 JavaBean 对象的属性赋值
				PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
				for (int i = 0; i < propertyDescriptors.length; i++) {
					PropertyDescriptor descriptor = propertyDescriptors[i];
					String fieldName = descriptor.getName();
					Method getMethod = descriptor.getReadMethod();// 获得get方法
					Object o = getMethod.invoke(bean);// 执行get方法返回一个Object
					if (fieldName.equals(aiFields)) {
						idValue = (String) o;
						continue;
					}
				}
				String sql = "DELETE FROM " + dbName + " WHERE " + beanName(aiFields) + "='" + idValue + "'";
				return excute(sql, new Object[] {}, new int[] {});
			}
		} catch (Exception e) {
			this.getConn().setError(true);
			log.error("执行sql错误", e);
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取单条数据
	 *
	 * @param sql   查询语句
	 * @param args  参数数组
	 * @param types 参数类型数组 java.sql.Types中的常量
	 * @return 数据
	 */
	public Map<?, ?> find(String sql, Object[] args, int[] types) {
		try {
			List<?> query = super.query(sql, args, types);
			if (query.size() > 0) {
				return (Map<?, ?>) query.get(0);
			} else {
				return null;
			}

		} catch (Exception e) {
			this.getConn().setError(true);
			log.error("find方法执行失败", e);
			throw new RuntimeException(e);
		}
	}

	public void init(RSConnection conn, String ds) {
		try {
			super.init(conn, ds);
		} catch (Exception e) {
			log.error("初始化dao出错", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 使用bean的插入方法
	 *
	 * @param bean 实体类
	 * @return 0失败，1成功
	 */
	public int insert(Object bean) {
		BeanInfo beanInfo;
		try {
			Class<?> clazz = bean.getClass();
			beanInfo = Introspector.getBeanInfo(clazz);
			// 获取类名
			String className = beanInfo.getBeanDescriptor().getName();
			// 将类名转换成对应的数据库名
			String dbName = beanName(className);
			if (dbName.length() > 1) {
				dbName = dbName.substring(1, dbName.length());
				// 给 JavaBean 对象的属性赋值
				PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

				StringBuffer fields = new StringBuffer();
				StringBuffer values = new StringBuffer();
				List<Object> valueList = new ArrayList<>();
				List<Integer> types = new ArrayList<>();
				for (int i = 0; i < propertyDescriptors.length; i++) {
					PropertyDescriptor descriptor = propertyDescriptors[i];
					String fieldName = descriptor.getName();
					if ("class".equals(fieldName)) {
						continue;
					}
					Method getMethod = descriptor.getReadMethod();// 获得get方法
					Object o = getMethod.invoke(bean);// 执行get方法返回一个Object
					String beanName = beanName(fieldName);
					if (!StringUtils.isNotEmptyObject(o)) {
						continue;
					}
					fields.append(beanName + ",");
					valueList.add(o);
					values.append("?,");
					types.add(Types.VARCHAR);
				}
				if (fields.toString().length() > 1) {
					String sql = "INSERT INTO " + dbName + " ("
							+ fields.toString().substring(0, fields.toString().length() - 1) + ") VALUES ("
							+ values.toString().substring(0, values.toString().length() - 1) + ") ";
					int[] t = new int[types.size()];
					for (int k = 0; k < types.size(); k++) {
						t[k] = types.get(k);
					}
					return excute(sql, valueList.toArray(), t);
				}
			}
		} catch (Exception e) {
			this.getConn().setError(true);
			log.error("insert方法执行失败", e);
			throw new RuntimeException(e);
		}
		return 0;

	}

	/**
	 * 请求一个查询结果集
	 *
	 * @param tableName 表名
	 * @param where     where的Map集合
	 * @param isByPage  是否开启分页
	 * @param start     开始的记录行
	 * @param rows      取出的数据条数
	 * @return 查询结果
	 */
	public List<?> query(String tableName, Map where, boolean isByPage, int start, int rows) {
		StringBuffer sql = new StringBuffer("SELECT * FROM " + tableName + " WHERE 1=1");
		List args = new ArrayList<>();
		List types = new ArrayList<>();

		Set<String> keys = where.keySet();
		for (String key : keys) {
			sql.append(" AND ");
			sql.append(key + " = ?");
			args.add(where.get(key));
			types.add(Types.VARCHAR);
		}
		int[] _types = new int[types.size()];
		for (int i = 0; i < types.size(); i++) {
			_types[i] = (int) types.get(i);
		}
		try {
			return this.queryByPage(sql.toString(), args.toArray(), _types, rows, rows);
		} catch (Exception e) {
			this.getConn().setError(true);
			log.error("query方法执行失败", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 请求一个dataset
	 *
	 * @param bean     数据表实体类
	 * @param operator where的运算符集合(key为字段名的大写，value为运算符："=","like")(尽量使用LinkedHashMap)
	 * @param isByPage 是否开启分页
	 * @param start    开始的记录行
	 * @param rows     取出的数据条数
	 * @param bean     bean名称
	 * @return 查询结果DataSet类型
	 */
	public DataSet queryBean(Object bean, Map operator, boolean isByPage, int start, int rows) {
		BeanInfo beanInfo;
		boolean opIsNotDef = true;
		if (null == operator || operator.size() <= 0) {
			operator = new LinkedHashMap();
			opIsNotDef = true;
		} else {
			opIsNotDef = false;
		}
		try {
			Class<?> clazz = bean.getClass();
			beanInfo = Introspector.getBeanInfo(clazz);
			// 获取类名
			String className = beanInfo.getBeanDescriptor().getName();
			// 将类名转换成对应的数据库名
			String dbName = beanName(className);
			if (dbName.length() > 1) {
				dbName = dbName.substring(1, dbName.length());
				// 给 JavaBean 对象的属性赋值
				PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
				LinkedHashMap where = new LinkedHashMap<>();
				for (int i = 0; i < propertyDescriptors.length; i++) {
					PropertyDescriptor descriptor = propertyDescriptors[i];
					String fieldName = descriptor.getName();
					if ("class".equals(fieldName)) {
						continue;
					}
					Method getMethod = descriptor.getReadMethod();// 获得get方法
					Object o = getMethod.invoke(bean);// 执行get方法返回一个Object
					String beanName = beanName(fieldName);
					if (!StringUtils.isNotEmptyObject(o)) {
						continue;
					}
					where.put(beanName, o);
					if (opIsNotDef) {
						operator.put(beanName, "=");
					}

				}
				return queryBean(dbName, where, operator, isByPage, start, rows, clazz);
			}
		} catch (Exception e) {
			this.getConn().setError(true);
			log.error("queryBean方法执行失败", e);
			throw new RuntimeException(e);
		}
		return null;

	}

	/**
	 * 请求一个dataset
	 *
	 * @param tableName 表名
	 * @param where     where的Map集合(尽量使用LinkedHashMap)
	 * @param operator  where的运算符集合(尽量使用LinkedHashMap)
	 * @param isByPage  是否开启分页
	 * @param start     开始的记录行
	 * @param rows      取出的数据条数
	 * @param bean      bean名称
	 * @return 查询结果DataSet类型
	 */
	public DataSet queryBean(String tableName, Map where, Map operator, boolean isByPage, int start, int rows,
			Class bean) {
		StringBuffer sql = new StringBuffer("SELECT * FROM " + tableName + " WHERE 1=1");
		List args = new ArrayList<>();
		List types = new ArrayList<>();

		Set<String> keys = where.keySet();
		for (String key : keys) {
			sql.append(" AND ");
			String _operator = "";
			try {
				_operator = (String) operator.get(key);
			} catch (Exception e) {
				_operator = "";
			}
			sql.append(key + " " + _operator + " ?");
			args.add(where.get(key));
			types.add(Types.VARCHAR);
		}
		int[] _types = new int[types.size()];
		for (int i = 0; i < types.size(); i++) {
			_types[i] = (int) types.get(i);
		}
		return this.queryBean(sql.toString(), args.toArray(), _types, isByPage, start, rows, bean);
	}

	/**
	 * 请求一个dataset
	 *
	 * @param sql      sql语句
	 * @param args     占位符所对应数据
	 * @param types    占位符对应的数据类型
	 * @param isByPage 是否分页
	 * @param start    开始行数
	 * @param rows     数量
	 * @param bean     bean名称
	 * @return 查询结果DataSet类型
	 */
	public DataSet queryBean(String sql, Object[] args, int[] types, boolean isByPage, int start, int rows,
			Class bean) {
		DataSet ds = new DataSet();
		List<Object> recordList = new ArrayList<>();
		List<Map> ret = new ArrayList<>();
		try {
			List<Map> retCount = (List<Map>) super.query(
					sql.replaceFirst("(?i)SELECT (.+?) FROM", "SELECT COUNT(1) AS COUNT FROM "), args, types);
			int total = Integer.parseInt(retCount.get(0).get("COUNT").toString());
			if (total > 0) {
				//如果有数据则执行查询方法
				if (isByPage) {
					ret = (List<Map>) this.queryByPage(sql, args, types, start, rows);
				} else {
					ret = (List<Map>) super.query(sql, args, types);
				}

				for (Map m : ret) {
					Record r = new Record();
					r.fromMap(m);
					recordList.add(r.toBean(bean));
				}
			}
			ds.setRows(recordList);
			ds.setCount(ret.size());
			ds.setTotal(total);
			return ds;
		} catch (Exception e) {
			this.getConn().setError(true);
			log.error("queryBean方法执行失败", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 请求一个dataset
	 *
	 * @param sql   sql语句
	 * @param args  占位符所对应数据
	 * @param types 占位符对应的数据类型
	 * @param start 开始行数
	 * @param rows  取出数量
	 * @return 查询结果DataSet类型
	 */
	public List queryByPage(String sql, Object[] args, int[] types, int start, int rows) {
		switch (super.getDataSourceType()) {
		case "1":// mysql
			sql += " limit " + start + "," + rows;
			break;
		case "2":// oracle
			sql = "SELECT * FROM ( SELECT A.*, ROWNUM RN FROM (" + sql + ") A WHERE ROWNUM <= " + (start + rows)
					+ ")WHERE RN > " + start;
			break;

		default:
			break;
		}
		try {
			return super.query(sql, args, types);
		} catch (Exception e) {
			this.getConn().setError(true);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 使用bean的保存方法
	 *
	 * @param record Record
	 * @param bean   实体类
	 * @return 更改数量
	 */
	public int save(Record record, Class<?> bean) {
		if (Record.STATE_UPDATE == record.getState()) {
			return this.update(record.toBean(bean));
		} else if (Record.STATE_INSERT == record.getState()) {
			return this.insert(record.toBean(bean));
		} else if (Record.STATE_DELETED == record.getState()) {
			return this.delete(record.toBean(bean));
		}
		return 0;
	}

	/**
	 * 使用bean的更新方法
	 *
	 * @param bean
	 * @return 0失败，1成功
	 */
	public int update(Object bean) {
		BeanInfo beanInfo;
		try {
			// 获取类属性
			Class<?> clazz = bean.getClass();
			beanInfo = Introspector.getBeanInfo(clazz);
			Field[] fields = clazz.getDeclaredFields();
			String aiFields = "";
			String idValue = "";
			for (int i = 0; i < fields.length; i++) {
				Field f = fields[i];
				if (f.isAnnotationPresent(KeyFields.class)) {
					aiFields = f.getName();
				}
			}
			// 获取类名
			String className = beanInfo.getBeanDescriptor().getName();
			// 将类名转换成对应的数据库名
			String dbName = beanName(className);
			if (dbName.length() > 1) {
				dbName = dbName.substring(1, dbName.length());
				// 给 JavaBean 对象的属性赋值
				PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
				StringBuffer set = new StringBuffer();
				List<Object> valueList = new ArrayList<>();
				List<Integer> types = new ArrayList<>();
				for (int i = 0; i < propertyDescriptors.length; i++) {
					PropertyDescriptor descriptor = propertyDescriptors[i];
					String fieldName = descriptor.getName();
					if ("class".equals(fieldName)) {
						continue;
					}

					String beanName = beanName(fieldName);
					Method getMethod = descriptor.getReadMethod();// 获得get方法
					Object o = getMethod.invoke(bean);// 执行get方法返回一个Object
					if (fieldName.equals(aiFields)) {
						idValue = (String) o;
						continue;
					}

					set.append(beanName + " = ? ,");
					valueList.add(o);
					types.add(Types.VARCHAR);
				}
				if (set.toString().length() > 1) {
					String sql = "UPDATE " + dbName + " SET " + set.toString().substring(0, set.toString().length() - 1)
							+ " WHERE " + beanName(aiFields) + "='" + idValue + "'";
					int[] t1 = new int[types.size()];
					for (int k = 0; k < types.size(); k++) {
						t1[k] = types.get(k);
					}
					return excute(sql, valueList.toArray(), t1);
				}
			}
		} catch (Exception e) {
			this.getConn().setError(true);
			log.error("update方法执行失败", e);
			throw new RuntimeException(e);
		}
		return 0;
	}

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>BIT</code>.
	 */
	public final static int BIT = -7;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>TINYINT</code>.
	 */
	public final static int TINYINT = -6;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>SMALLINT</code>.
	 */
	public final static int SMALLINT = 5;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>INTEGER</code>.
	 */
	public final static int INTEGER = 4;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>BIGINT</code>.
	 */
	public final static int BIGINT = -5;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>FLOAT</code>.
	 */
	public final static int FLOAT = 6;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>REAL</code>.
	 */
	public final static int REAL = 7;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>DOUBLE</code>.
	 */
	public final static int DOUBLE = 8;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>NUMERIC</code>.
	 */
	public final static int NUMERIC = 2;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>DECIMAL</code>.
	 */
	public final static int DECIMAL = 3;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>CHAR</code>.
	 */
	public final static int CHAR = 1;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>VARCHAR</code>.
	 */
	public final static int VARCHAR = 12;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>LONGVARCHAR</code>.
	 */
	public final static int LONGVARCHAR = -1;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>DATE</code>.
	 */
	public final static int DATE = 91;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>TIME</code>.
	 */
	public final static int TIME = 92;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>TIMESTAMP</code>.
	 */
	public final static int TIMESTAMP = 93;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>BINARY</code>.
	 */
	public final static int BINARY = -2;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>VARBINARY</code>.
	 */
	public final static int VARBINARY = -3;

	/**
	 * <p>
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>LONGVARBINARY</code>.
	 */
	public final static int LONGVARBINARY = -4;

	/**
	 * <p>
	 * The constant in the Java programming language that identifies the generic SQL
	 * value <code>NULL</code>.
	 */
	public final static int NULL = 0;

	/**
	 * The constant in the Java programming language that indicates that the SQL
	 * type is database-specific and gets mapped to a Java object that can be
	 * accessed via the methods <code>getObject</code> and <code>setObject</code>.
	 */
	public final static int OTHER = 1111;

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>JAVA_OBJECT</code>.
	 *
	 * @since 1.2
	 */
	public final static int JAVA_OBJECT = 2000;

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>DISTINCT</code>.
	 *
	 * @since 1.2
	 */
	public final static int DISTINCT = 2001;

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>STRUCT</code>.
	 *
	 * @since 1.2
	 */
	public final static int STRUCT = 2002;

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>ARRAY</code>.
	 *
	 * @since 1.2
	 */
	public final static int ARRAY = 2003;

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>BLOB</code>.
	 *
	 * @since 1.2
	 */
	public final static int BLOB = 2004;

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>CLOB</code>.
	 *
	 * @since 1.2
	 */
	public final static int CLOB = 2005;

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>REF</code>.
	 *
	 * @since 1.2
	 */
	public final static int REF = 2006;

	/**
	 * The constant in the Java programming language, somtimes referred to as a type
	 * code, that identifies the generic SQL type <code>DATALINK</code>.
	 *
	 * @since 1.4
	 */
	public final static int DATALINK = 70;

	/**
	 * The constant in the Java programming language, somtimes referred to as a type
	 * code, that identifies the generic SQL type <code>BOOLEAN</code>.
	 *
	 * @since 1.4
	 */
	public final static int BOOLEAN = 16;

	// ------------------------- JDBC 4.0 -----------------------------------

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>ROWID</code>
	 *
	 * @since 1.6
	 */
	public final static int ROWID = -8;

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>NCHAR</code>
	 *
	 * @since 1.6
	 */
	public static final int NCHAR = -15;

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>NVARCHAR</code>.
	 *
	 * @since 1.6
	 */
	public static final int NVARCHAR = -9;

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>LONGNVARCHAR</code>.
	 *
	 * @since 1.6
	 */
	public static final int LONGNVARCHAR = -16;

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>NCLOB</code>.
	 *
	 * @since 1.6
	 */
	public static final int NCLOB = 2011;

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type <code>XML</code>.
	 *
	 * @since 1.6
	 */
	public static final int SQLXML = 2009;

	// --------------------------JDBC 4.2 -----------------------------

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type {@code REF CURSOR}.
	 *
	 * @since 1.8
	 */
	public static final int REF_CURSOR = 2012;

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type {@code TIME WITH TIMEZONE}.
	 *
	 * @since 1.8
	 */
	public static final int TIME_WITH_TIMEZONE = 2013;

	/**
	 * The constant in the Java programming language, sometimes referred to as a
	 * type code, that identifies the generic SQL type
	 * {@code TIMESTAMP WITH TIMEZONE}.
	 *
	 * @since 1.8
	 */
	public static final int TIMESTAMP_WITH_TIMEZONE = 2014;
}
