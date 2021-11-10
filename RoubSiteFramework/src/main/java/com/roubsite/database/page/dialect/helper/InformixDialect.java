package com.roubsite.database.page.dialect.helper;

import com.roubsite.database.dao.EntityDao;
import com.roubsite.database.page.Page;
import com.roubsite.database.page.dialect.AbstractDialect;

public class InformixDialect extends AbstractDialect {

	@Override
	public Page getPageSql(String sql, Object[] params, int[] types, int offset, int rows) {
		StringBuilder sqlBuilder = new StringBuilder(sql.length() + 40);
		sqlBuilder.append("SELECT ");
		if (offset > 0) {
			sqlBuilder.append(" SKIP ? ");
			// 重新组合参数
			params = rebuildParams(params, new Object[] { offset });
			types = rebuildTypes(types, new int[] { EntityDao.INTEGER });
		}
		if (rows > 0) {
			sqlBuilder.append(" FIRST ? ");
			// 重新组合参数
			params = rebuildParams(params, new Object[] { rows });
			types = rebuildTypes(types, new int[] { EntityDao.INTEGER });
		}
		sqlBuilder.append(" * FROM ( \n");
		sqlBuilder.append(sql);
		sqlBuilder.append("\n ) TEMP_T ");
		return new Page(sqlBuilder.toString(), params, types);
	}

}
