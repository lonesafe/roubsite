package com.roubsite.database.page;

import com.roubsite.database.page.dialect.AbstractDialect;

public class PageHelper extends AbstractDialect {
	private Dialect dialect;

	public PageHelper(String url) {
		String dialect = PageAutoDialect.fromJdbcUrl(url);
		this.dialect = PageAutoDialect.instanceDialect(dialect);
	}

	@Override
	public String getCountSql(String sourceSql) {
		return dialect.getCountSql(sourceSql);
	}

	@Override
	public Page getPageSql(String sourceSql, Object[] params, int[] types, int offset, int rows) {
		return dialect.getPageSql(sourceSql, params, types, offset, rows);
	}
}
