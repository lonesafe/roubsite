package com.roubsite.database.page.dialect.helper;

import com.roubsite.database.dao.EntityDao;
import com.roubsite.database.page.Page;

public class SqlServer2012Dialect extends SqlServerDialect {

	@Override
	public Page getPageSql(String sql, Object[] params, int[] types, int offset, int rows) {
		StringBuilder sqlBuilder = new StringBuilder(sql.length() + 64);
		sqlBuilder.append(sql);
		sqlBuilder.append("\n OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
		// 重新组合参数
		params = rebuildParams(params, new Object[] { offset, rows });
		types = rebuildTypes(types, new int[] { EntityDao.INTEGER, EntityDao.INTEGER });
		return new Page(sqlBuilder.toString(), params, types);
	}

}
