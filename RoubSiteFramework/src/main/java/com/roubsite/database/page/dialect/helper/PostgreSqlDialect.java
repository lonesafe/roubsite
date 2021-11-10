package com.roubsite.database.page.dialect.helper;

import com.roubsite.database.dao.EntityDao;
import com.roubsite.database.page.Page;

/**
 * PostgreSQL 方言.
 *
 */
public class PostgreSqlDialect extends MySqlDialect {

	/**
	 * 构建 <a href=
	 * "https://www.postgresql.org/docs/current/queries-limit.html">PostgreSQL</a>分页查询语句
	 */
	@Override
	public Page getPageSql(String sql, Object[] params, int[] types, int offset, int rows) {
		StringBuilder sqlStr = new StringBuilder(sql.length() + 17);
		sqlStr.append(sql);
		if (offset == 0) {
			sqlStr.append(" LIMIT ?");
			// 重新组合参数
			params = rebuildParams(params, new Object[] { rows });
			types = rebuildTypes(types, new int[] { EntityDao.INTEGER });
		} else {
			sqlStr.append(" OFFSET ? LIMIT ?");
			// 重新组合参数
			params = rebuildParams(params, new Object[] { offset, rows });
			types = rebuildTypes(types, new int[] { EntityDao.INTEGER, EntityDao.INTEGER });
		}
		return new Page(sqlStr.toString(), params, types);
	}

}
