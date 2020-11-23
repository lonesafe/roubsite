package com.roubsite.database.pool;

import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;
import com.roubsite.utils.PropertiesUtil;
import com.roubsite.utils.StringUtils;

public class DataSourcePool // implements DataSource
{
	private String url = null;
	private String username = null;
	private String password = null;
	private int size = 10;
	// private ConcurrentLinkedQueue<Connection> list = new
	// ConcurrentLinkedQueue<Connection>();
	DruidDataSource dataSource = new DruidDataSource();
	org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DataSourcePool.class);
	private String keepAlive;
	private long maxWaitMillis;
	private int maxWaitThreadCount;
	private int validationQueryTimeout;
	// 创建对象就初始化size个数据库连接
	public DataSourcePool(String dataSourceName) {
		url = PropertiesUtil.getString(dataSourceName + ".url", "dataSource.properties");
		username = PropertiesUtil.getString(dataSourceName + ".username", "dataSource.properties");
		password = PropertiesUtil.getString(dataSourceName + ".password", "dataSource.properties");
		if (StringUtils.isNotEmpty(PropertiesUtil.getString(dataSourceName + ".poolSize", "dataSource.properties"))) {
			size = Integer.parseInt(PropertiesUtil.getString(dataSourceName + ".poolSize", "dataSource.properties"));
		}

		if (StringUtils.isNotEmpty(PropertiesUtil.getString(dataSourceName + ".keepAlive", "dataSource.properties"))) {
			keepAlive = PropertiesUtil.getString(dataSourceName + ".keepAlive", "dataSource.properties");
		}

		if (StringUtils
				.isNotEmpty(PropertiesUtil.getString(dataSourceName + ".maxWaitMillis", "dataSource.properties"))) {
			maxWaitMillis = Long
					.parseLong(PropertiesUtil.getString(dataSourceName + ".maxWaitMillis", "dataSource.properties"));
		}
		if (StringUtils.isNotEmpty(
				PropertiesUtil.getString(dataSourceName + ".maxWaitThreadCount", "dataSource.properties"))) {
			maxWaitThreadCount = Integer.parseInt(
					PropertiesUtil.getString(dataSourceName + ".maxWaitThreadCount", "dataSource.properties"));
		}
		
		if (StringUtils.isNotEmpty(
				PropertiesUtil.getString(dataSourceName + ".validationQueryTimeout", "dataSource.properties"))) {
			validationQueryTimeout = Integer.parseInt(
					PropertiesUtil.getString(dataSourceName + ".validationQueryTimeout", "dataSource.properties"));
		}

		dataSource.setDriverClassName(
				PropertiesUtil.getString(dataSourceName + ".driverClassName", "dataSource.properties"));
		dataSource.setUsername(username);// 用户名
		dataSource.setPassword(password);// 密码
		dataSource.setUrl(url);// 链接字符串
		dataSource.setInitialSize(size);// 初始化连接数
		dataSource.setMinIdle(-1);
		dataSource.setMaxActive(size);// 最大连接数
		if ("true".equals(keepAlive)) {// 链接保持
			dataSource.setKeepAlive(true);
		} else {
			dataSource.setKeepAlive(false);
		}
		dataSource.setMaxWait(maxWaitMillis);// 最大等待时间
		dataSource.setMaxWaitThreadCount(maxWaitThreadCount);// 最大等待线程数，当超过这个数量的线程在等待时，新的请求将直接返回超时
		dataSource.setValidationQueryTimeout(validationQueryTimeout);
		try {
			dataSource.setFilters("stat");
		} catch (SQLException e) {
		}
		dataSource.setTestOnBorrow(true);
		dataSource.setPoolPreparedStatements(false);
		dataSource.setRemoveAbandoned(true);// 超过时间限制是否回收
		dataSource.setRemoveAbandonedTimeout(600);// 超时时间；单位为秒。180秒=3分钟
		dataSource.setLogAbandoned(false);// 关闭abanded连接时输出错误日志
		try {
			dataSource.init();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 创建对象就初始化size个数据库连接
	public DataSourcePool(String driverClassName, String url, String username, String password, int size,
			boolean keepAlive, long maxWaitMillis, int maxWaitThreadCount) {
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUsername(username);// 用户名
		dataSource.setPassword(password);// 密码
		dataSource.setUrl(url);// 链接字符串
		dataSource.setInitialSize(size);// 初始化连接数
		dataSource.setMinIdle(-1);
		dataSource.setMaxActive(size);// 最大连接数
		// 链接保持
		dataSource.setKeepAlive(keepAlive);
		dataSource.setMaxWait(maxWaitMillis);// 最大等待时间
		dataSource.setMaxWaitThreadCount(maxWaitThreadCount);// 最大等待线程数，当超过这个数量的线程在等待时，新的请求将直接返回超时
		try {
			dataSource.setFilters("stat");
		} catch (SQLException e) {
		}
		dataSource.setTestOnBorrow(true);
		dataSource.setPoolPreparedStatements(false);
		dataSource.setRemoveAbandoned(true);// 超过时间限制是否回收
		dataSource.setRemoveAbandonedTimeout(600);// 超时时间；单位为秒。180秒=3分钟
		dataSource.setLogAbandoned(false);// 关闭abanded连接时输出错误日志
		try {
			dataSource.init();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 请求一个新的connection
	 */
	public DruidDataSource getDataSource() throws SQLException {
		return dataSource;
	}
}
