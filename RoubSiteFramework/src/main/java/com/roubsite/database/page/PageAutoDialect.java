package com.roubsite.database.page;

import java.util.HashMap;
import java.util.Map;

import com.roubsite.database.page.dialect.AbstractDialect;
import com.roubsite.database.page.dialect.helper.Db2Dialect;
import com.roubsite.database.page.dialect.helper.FirebirdDialect;
import com.roubsite.database.page.dialect.helper.HerdDBDialect;
import com.roubsite.database.page.dialect.helper.HsqldbDialect;
import com.roubsite.database.page.dialect.helper.InformixDialect;
import com.roubsite.database.page.dialect.helper.MySqlDialect;
import com.roubsite.database.page.dialect.helper.Oracle9iDialect;
import com.roubsite.database.page.dialect.helper.OracleDialect;
import com.roubsite.database.page.dialect.helper.OscarDialect;
import com.roubsite.database.page.dialect.helper.PostgreSqlDialect;
import com.roubsite.database.page.dialect.helper.SqlServer2012Dialect;
import com.roubsite.database.page.dialect.helper.SqlServerDialect;
import com.roubsite.utils.StringUtils;

/**
 * 基础方言信息
 *
 */
public class PageAutoDialect {

	private static Map<String, Class<? extends Dialect>> dialectAliasMap = new HashMap<String, Class<? extends Dialect>>();

	public static void registerDialectAlias(String alias, Class<? extends Dialect> dialectClass) {
		dialectAliasMap.put(alias, dialectClass);
	}

	static {
		// 注册别名
		registerDialectAlias("hsqldb", HsqldbDialect.class);
		registerDialectAlias("h2", HsqldbDialect.class);
		registerDialectAlias("phoenix", HsqldbDialect.class);

		registerDialectAlias("postgresql", PostgreSqlDialect.class);

		registerDialectAlias("mysql", MySqlDialect.class);
		registerDialectAlias("mariadb", MySqlDialect.class);
		registerDialectAlias("sqlite", MySqlDialect.class);

		registerDialectAlias("herddb", HerdDBDialect.class);

		registerDialectAlias("oracle", OracleDialect.class);
		registerDialectAlias("oracle9i", Oracle9iDialect.class);
		registerDialectAlias("db2", Db2Dialect.class);
		registerDialectAlias("informix", InformixDialect.class);
		// 解决 informix-sqli #129，仍然保留上面的
		registerDialectAlias("informix-sqli", InformixDialect.class);

		registerDialectAlias("sqlserver", SqlServerDialect.class);
		registerDialectAlias("sqlserver2012", SqlServer2012Dialect.class);

		registerDialectAlias("derby", SqlServer2012Dialect.class);
		// 达梦数据库,https://github.com/mybatis-book/book/issues/43
		registerDialectAlias("dm", OracleDialect.class);
		// 阿里云PPAS数据库,https://github.com/pagehelper/Mybatis-PageHelper/issues/281
		registerDialectAlias("edb", OracleDialect.class);
		// 神通数据库
		registerDialectAlias("oscar", OscarDialect.class);
		registerDialectAlias("clickhouse", MySqlDialect.class);
		// 瀚高数据库
		registerDialectAlias("highgo", HsqldbDialect.class);
		// 虚谷数据库
		registerDialectAlias("xugu", HsqldbDialect.class);
		registerDialectAlias("impala", HsqldbDialect.class);
		registerDialectAlias("firebirdsql", FirebirdDialect.class);

	}

	/**
	 * 自动获取dialect,如果没有setProperties或setSqlUtilConfig，也可以正常进行
	 */

	public static String fromJdbcUrl(String jdbcUrl) {
		final String url = jdbcUrl.toLowerCase();
		for (String dialect : dialectAliasMap.keySet()) {
			if (url.contains(":" + dialect.toLowerCase() + ":")) {
				return dialect;
			}
		}
		return null;
	}
	
	  /**
     * 初始化 helper
     *
     * @param dialectClass
     * @param properties
     */
    public static AbstractDialect instanceDialect(String dialectClass) {
    	AbstractDialect dialect;
        try {
            Class sqlDialectClass = resloveDialectClass(dialectClass);
            if (AbstractDialect.class.isAssignableFrom(sqlDialectClass)) {
                dialect = (AbstractDialect) sqlDialectClass.newInstance();
            } else {
                throw new RuntimeException("该数据库不支持自动分页");
            }
        } catch (Exception e) {
            throw new RuntimeException("初始化分页类 [" + dialectClass + "]时出错:" + e.getMessage(), e);
        }
        return dialect;
    }

	/**
	 * 反射类
	 *
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public static Class resloveDialectClass(String className) throws Exception {
		if (dialectAliasMap.containsKey(className.toLowerCase())) {
			return dialectAliasMap.get(className.toLowerCase());
		} else {
			return Class.forName(className);
		}
	}

}
