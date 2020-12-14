package com.roubsite.database.dao;

import com.roubsite.utils.ConfUtils;
import com.roubsite.utils.PropertiesUtil;

import java.sql.SQLException;
import java.util.List;


public class SmartyDBUtil {
	/** ASM名称 */
	public static final String NAME = SmartyDBUtil.class.getName().replace('.', '/');

	public static List execQuery(String dataSource, String sql) {
		try {
			DBUtils db = new DBUtils(dataSource,
					ConfUtils.getConf(dataSource + ".driverClassName", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSource,"driverClassName"}),
					ConfUtils.getConf(dataSource + ".url", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSource,"url"}),
					ConfUtils.getConf(dataSource + ".username", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSource,"username"}),
					ConfUtils.getConf(dataSource + ".password", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSource,"password"}), 12, true, 100, 100);
			return db.execQuery(sql, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
