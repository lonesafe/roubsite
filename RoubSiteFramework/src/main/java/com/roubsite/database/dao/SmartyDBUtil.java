package com.roubsite.database.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.roubsite.utils.ConfUtils;

public class SmartyDBUtil {
	/** ASM名称 */
	public static final String NAME = SmartyDBUtil.class.getName().replace('.', '/');

	public static List<Map<String, Object>> execQuery(String dataSource, String sql) {
		try {
			DBUtils db = new DBUtils(dataSource,
					ConfUtils.getStringConf("RoubSite.DataSourcePool.dataSources." + dataSource + ".driverClassName",
							"com.mysql.jdbc.Driver"),
					ConfUtils.getStringConf("RoubSite.DataSourcePool.dataSources." + dataSource + ".url", ""),
					ConfUtils.getStringConf("RoubSite.DataSourcePool.dataSources." + dataSource + ".username", ""),
					ConfUtils.getStringConf("RoubSite.DataSourcePool.dataSources." + dataSource + ".password", ""), 12,
					true, 100, 100);
			return db.execQuery(sql, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
