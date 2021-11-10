package com.roubsite.database.page.dialect.helper;

import com.roubsite.database.dao.EntityDao;
import com.roubsite.database.page.Page;
import com.roubsite.database.page.dialect.AbstractDialect;

public class HsqldbDialect extends AbstractDialect {

	@Override
	public Page getPageSql(String sql, Object[] params, int[] types, int offset, int rows) {
		StringBuilder sqlBuilder = new StringBuilder(sql.length() + 20);
		sqlBuilder.append(sql);
		if (rows > 0) {
			sqlBuilder.append("\n LIMIT ? ");
			// 重新组合参数
			params = rebuildParams(params, new Object[] { offset + rows });
			types = rebuildTypes(types, new int[] { EntityDao.INTEGER });
		}
		if (offset > 0) {
			sqlBuilder.append("\n OFFSET ? ");
			// 重新组合参数
			params = rebuildParams(params, new Object[] { offset });
			types = rebuildTypes(types, new int[] { EntityDao.INTEGER });
		}
		return new Page(sqlBuilder.toString(), params, types);
	}
}
