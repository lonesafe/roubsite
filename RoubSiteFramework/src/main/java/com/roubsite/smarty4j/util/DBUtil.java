package com.roubsite.smarty4j.util;

import java.sql.SQLException;
import java.util.List;

import com.roubsite.database.dao.DBUtils;

public class DBUtil {
	/** ASM名称 */
	public static final String NAME = DBUtil.class.getName().replace('.', '/');

	public static List execQuery(String dataSource, String sql) {
		try {
			DBUtils db = new DBUtils(dataSource,
					PropertiesUtil.getString(dataSource + ".driverClassName", "dataSource.properties"),
					PropertiesUtil.getString(dataSource + ".url", "dataSource.properties"),
					PropertiesUtil.getString(dataSource + ".username", "dataSource.properties"),
					PropertiesUtil.getString(dataSource + ".password", "dataSource.properties"), 12, true, 100, 100);
			return db.execQuery(sql, null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println("asdfasdf");
		System.out.println(NAME);
	}

}
