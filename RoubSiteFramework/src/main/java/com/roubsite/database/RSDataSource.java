package com.roubsite.database;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.DruidDriver;
import com.roubsite.database.dao.EntityDao;
import com.roubsite.database.page.PageHelper;
import com.roubsite.database.pool.DataSourcePool;
import com.roubsite.utils.ConfUtils;
import com.roubsite.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RSDataSource {
	private Map<String, DataSourcePool> dataSourcePoolMap = new HashMap<String, DataSourcePool>();
	private Map<String, PageHelper> pageHelperMap = new HashMap<String, PageHelper>();
	static Logger log = LoggerFactory.getLogger(EntityDao.class);

	public RSDataSource() {
	}

	/**
	 * 注册数据源
	 *
	 * @param dataSourceName
	 */
	public DataSourcePool registerDataSource(String dataSourceName) {
		log.debug("初始化数据源" + dataSourceName);
		DataSourcePool dsp = new DataSourcePool(dataSourceName);
		dataSourcePoolMap.put(dataSourceName, dsp);
		return dsp;
	}

	/**
	 * 注册数据源
	 *
	 * @param dataSourceName     数据源名称
	 * @param driverClassName    数据源驱动类
	 * @param url                数据源连接
	 * @param username           用户名
	 * @param password           密码
	 * @param size               连接池池大小
	 * @param keepAlive          是否保持连接
	 * @param maxWaitMillis      最大等待时间
	 * @param maxWaitThreadCount 最大等待线程数量
	 * @return
	 */
	public DataSourcePool registerDataSource(String dataSourceName, String driverClassName, String url, String username,
			String password, int size, boolean keepAlive, long maxWaitMillis, int maxWaitThreadCount) {
		log.debug("初始化数据源" + dataSourceName);
		DataSourcePool dsp = new DataSourcePool(driverClassName, url, username, password, size, keepAlive,
				maxWaitMillis, maxWaitThreadCount);
		dataSourcePoolMap.put(dataSourceName, dsp);
		pageHelperMap.put(dataSourceName, new PageHelper(url));
		return dsp;
	}

	/**
	 * 获取某个数据源连接
	 *
	 * @param dataSourceName
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public DruidDataSource getDataSource(String dataSourceName) throws SQLException {
		log.debug("获取数据源连接");

		DruidDataSource dataSource;
		Iterator<?> keys = dataSourcePoolMap.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (dataSourceName.equals(key)) {
				dataSource = dataSourcePoolMap.get(dataSourceName).getDataSource();
				return dataSource;
			}
		}
		dataSource = registerDataSource(dataSourceName).getDataSource();
		return dataSource;

	}

	/**
	 * 获取某个数据源连接
	 *
	 * @param dataSourceName     数据源名称
	 * @param driverClassName    数据源驱动类
	 * @param url                数据源连接
	 * @param username           用户名
	 * @param password           密码
	 * @param size               连接池池大小
	 * @param keepAlive          是否保持连接
	 * @param maxWaitMillis      最大等待时间
	 * @param maxWaitThreadCount 最大等待线程数量
	 */
	public DruidDataSource getDataSource(String dataSourceName, String driverClassName, String url, String username,
			String password, int size, boolean keepAlive, long maxWaitMillis, int maxWaitThreadCount)
			throws SQLException {
		log.debug("获取数据源连接");

		DruidDataSource dataSource;
		Iterator<?> keys = dataSourcePoolMap.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (dataSourceName.equals(key)) {
				dataSource = dataSourcePoolMap.get(dataSourceName).getDataSource();
				return dataSource;
			}
		}
		dataSource = registerDataSource(dataSourceName, driverClassName, url, username, password, size, keepAlive,
				maxWaitMillis, maxWaitThreadCount).getDataSource();
		return dataSource;

	}

	public void closeAll() {
		dataSourcePoolMap.forEach((key, value) -> {
			log.info("正在关闭数据源：[" + key + "]");
			try {
				value.getDataSource().close();
				DriverManager.deregisterDriver(value.getDataSource().getDriver());
			} catch (SQLException e) {
				log.error("数据源:[" + key + "]关闭失败", e);
			}
			log.info("数据源：[" + key + "]关闭成功");
		});
		Enumeration drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = (Driver) drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
			} catch (SQLException e) {
			}
		}
		try {
			Class clazz = Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread");
			Method method = clazz.getMethod("checkedShutdown");
			method.invoke(null);
		} catch (Exception e) {
		}
	}

	public PageHelper getPageHelperMap(String dataSourceName) {
		PageHelper pageHelper = pageHelperMap.get(dataSourceName);
		if (!StringUtils.isNotEmptyObject(pageHelperMap.get(dataSourceName))) {
			log.info("获取数据源[" + dataSourceName + "]分页插件");
			pageHelper = new PageHelper(
					ConfUtils.getStringConf("RoubSite.DataSourcePool.dataSources." + dataSourceName + ".url", ""));
			pageHelperMap.put(dataSourceName, pageHelper);
		}
		return pageHelper;
	}
}
