package com.roubsite.database.page.dialect.helper;

import com.roubsite.database.dao.EntityDao;
import com.roubsite.database.page.Page;
import com.roubsite.database.page.dialect.AbstractDialect;

public class Db2Dialect extends AbstractDialect {

	@Override
	public Page getPageSql(String sql, Object[] params, int[] types, int offset, int rows) {
		StringBuilder sqlBuilder = new StringBuilder(sql.length() + 140);
		sqlBuilder.append("SELECT * FROM (SELECT TMP_PAGE.*,ROWNUMBER() OVER() AS PAGEHELPER_ROW_ID FROM ( \n");
		sqlBuilder.append(sql);
		sqlBuilder.append("\n ) AS TMP_PAGE) TMP_PAGE WHERE PAGEHELPER_ROW_ID BETWEEN ? AND ?");
		// 重新组合参数
		params = rebuildParams(params, new Object[] { offset, offset + rows });
		types = rebuildTypes(types, new int[] { EntityDao.INTEGER, EntityDao.INTEGER });
		return new Page(sqlBuilder.toString(), params, types);
	}
}
