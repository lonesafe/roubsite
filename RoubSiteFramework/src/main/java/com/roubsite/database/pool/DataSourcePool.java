package com.roubsite.database.pool;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.roubsite.database.page.PageHelper;
import com.roubsite.utils.ConfUtils;

public class DataSourcePool {
	private String url = null;
	private String username = null;
	private String password = null;
	private int size = 10;
	DruidDataSource dataSource = new DruidDataSource();
	private static Logger log = LoggerFactory.getLogger(DataSourcePool.class);
	private boolean keepAlive = false;
	private int maxWaitMillis;
	private int maxWaitThreadCount;
	private int validationQueryTimeout;

	// 创建对象就初始化size个数据库连接
	public DataSourcePool(String dataSourceName) {
		url = ConfUtils.getStringConf("RoubSite.DataSourcePool.dataSources." + dataSourceName + ".url", "");
		username = ConfUtils.getStringConf("RoubSite.DataSourcePool.dataSources." + dataSourceName + ".username", "");
		password = ConfUtils.getStringConf("RoubSite.DataSourcePool.dataSources." + dataSourceName + ".password", "");
		size = ConfUtils.getIntConf("RoubSite.DataSourcePool.dataSources." + dataSourceName + ".poolSize");
		keepAlive = ConfUtils.getBooleanConf("RoubSite.DataSourcePool.dataSources." + dataSourceName + ".keepAlive");
		maxWaitMillis = ConfUtils
				.getIntConf("RoubSite.DataSourcePool.dataSources." + dataSourceName + ".maxWaitMillis");
		maxWaitThreadCount = ConfUtils
				.getIntConf("RoubSite.DataSourcePool.dataSources." + dataSourceName + ".maxWaitThreadCount");

		validationQueryTimeout = ConfUtils
				.getIntConf("RoubSite.DataSourcePool.dataSources." + dataSourceName + ".validationQueryTimeout");

		dataSource.setDriverClassName(ConfUtils
				.getStringConf("RoubSite.DataSourcePool.dataSources." + dataSourceName + ".driverClassName", ""));
		dataSource.setUsername(username);// 用户名
		dataSource.setPassword(password);// 密码
		dataSource.setUrl(getUrl());// 链接字符串
		dataSource.setInitialSize(size);// 初始化连接数
		dataSource.setMinIdle(-1);
		dataSource.setMaxActive(size);// 最大连接数
		dataSource.setKeepAlive(keepAlive);// 链接保持
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
			log.error("初始化数据源失败", e);
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
			log.error("初始化数据源失败", e);
		}
	}

	/**
	 * 请求一个新的connection
	 */
	public DruidDataSource getDataSource() throws SQLException {
		return dataSource;
	}

	public String getUrl() {
		return url;
	}
}
