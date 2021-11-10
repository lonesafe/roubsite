package com.roubsite.database.page.dialect.helper;

import com.roubsite.database.dao.EntityDao;
import com.roubsite.database.page.Page;
import com.roubsite.database.page.dialect.AbstractDialect;

public class HerdDBDialect extends AbstractDialect {

	@Override
	public Page getPageSql(String sql, Object[] params, int[] types, int offset, int rows) {
		StringBuilder sqlBuilder = new StringBuilder(sql.length() + 14);
		sqlBuilder.append(sql);
		if (offset == 0) {
			sqlBuilder.append("\n LIMIT ? ");
			// 重新组合参数
			params = rebuildParams(params, new Object[] { rows });
			types = rebuildTypes(types, new int[] { EntityDao.INTEGER });
		} else {
			sqlBuilder.append("\n LIMIT ?, ? ");

			// 重新组合参数
			params = rebuildParams(params, new Object[] { offset, rows });
			types = rebuildTypes(types, new int[] { EntityDao.INTEGER, EntityDao.INTEGER });
		}
		return new Page(sqlBuilder.toString(), params, types);
	}

}
