package com.roubsite.database.page;

import com.roubsite.database.page.cache.Cache;
import com.roubsite.database.page.cache.CacheFactory;
import com.roubsite.database.page.parser.CountSqlParser;
import com.roubsite.utils.StringUtils;

public class PageHelper {
	private static Cache<String, String> cache_count_sql = CacheFactory.createCache("count");
	public String getCountSql(String sourceSql) {
		String cacheSql = cache_count_sql.get(sourceSql);
		if(StringUtils.isNotEmpty(cacheSql)) {
			return cacheSql;
		}
		cacheSql = new CountSqlParser().getSmartCountSql(sourceSql);
		cache_count_sql.put(sourceSql, cacheSql);
		return cacheSql;
	}
}
