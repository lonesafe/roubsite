package com.roubsite.database;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSource;
import com.roubsite.database.dao.EntityDao;
import com.roubsite.database.pool.DataSourcePool;

public class RSDataSource {
	private Map<String, DataSourcePool> dataSourcePoolMap = new HashMap<String, DataSourcePool>();
	static Logger log = Logger.getLogger(EntityDao.class);

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
}
