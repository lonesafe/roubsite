package com.roubsite.database.page.dialect.helper;

import com.roubsite.database.dao.EntityDao;
import com.roubsite.database.page.Page;
import com.roubsite.database.page.dialect.AbstractDialect;

/**
 * 参考
 * <ul>
 * <li>https://github.com/pagehelper/Mybatis-PageHelper/pull/476</li>
 * <li>https://github.com/hibernate/hibernate-orm/search?utf8=%E2%9C%93&q=rownum&type=</li>
 * </ul>
 */
public class Oracle9iDialect extends AbstractDialect {

	@Override
	public Page getPageSql(String sql, Object[] params, int[] types, int offset, int rows) {
		StringBuilder sqlBuilder = new StringBuilder(sql.length() + 120);
		sqlBuilder.append("SELECT * FROM ( ");
		sqlBuilder.append(" SELECT TMP_PAGE.*, ROWNUM PAGEHELPER_ROW_ID FROM ( \n");
		sqlBuilder.append(sql);
		sqlBuilder.append("\n ) TMP_PAGE WHERE ROWNUM <= ? ");
		sqlBuilder.append(" ) WHERE PAGEHELPER_ROW_ID > ? ");
		// 重新组合参数
		params = rebuildParams(params, new Object[] { offset + rows, offset });
		types = rebuildTypes(types, new int[] { EntityDao.INTEGER, EntityDao.INTEGER });
		return new Page(sqlBuilder.toString(), params, types);
	}

}
