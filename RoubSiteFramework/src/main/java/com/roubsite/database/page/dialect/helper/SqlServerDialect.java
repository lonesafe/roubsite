package com.roubsite.database.page.dialect.helper;

import com.roubsite.database.page.Page;
import com.roubsite.database.page.dialect.AbstractDialect;
import com.roubsite.database.page.dialect.RegexWithNolockReplaceSql;
import com.roubsite.database.page.dialect.ReplaceSql;
import com.roubsite.database.page.parser.CountSqlParser;
import com.roubsite.database.page.parser.SqlServerParser;

public class SqlServerDialect extends AbstractDialect {
	protected ReplaceSql replaceSql = new RegexWithNolockReplaceSql();
	protected SqlServerParser pageSql = new SqlServerParser();
	protected CountSqlParser countSqlParser = new CountSqlParser();

	@Override
	public String getCountSql(String sourceSql) {
		String cacheSql = cache_count_sql.get(sourceSql);
		if (cacheSql != null) {
			return cacheSql;
		} else {
			cacheSql = sourceSql;
		}
		cacheSql = replaceSql.replace(cacheSql);
		cacheSql = countSqlParser.getSmartCountSql(cacheSql);
		cacheSql = replaceSql.restore(cacheSql);
		cache_count_sql.put(sourceSql, cacheSql);
		return cacheSql;
	}

	public Page getPageSql(String sql, Object[] params, int[] types, int startRow, int pageSize) {
		// 处理pageKey
		String cacheSql = cache_page_sql.get(sql);
		if (cacheSql == null) {
			cacheSql = sql;
			cacheSql = replaceSql.replace(cacheSql);
			cacheSql = pageSql.convertToPageSql(cacheSql, null, null);
			cacheSql = replaceSql.restore(cacheSql);
			cache_page_sql.put(sql, cacheSql);
		}
		cacheSql = cacheSql.replace(String.valueOf(Long.MIN_VALUE), String.valueOf(startRow));
		cacheSql = cacheSql.replace(String.valueOf(Long.MAX_VALUE), String.valueOf(pageSize));
		return new Page(cacheSql, params, types);
	}
}
