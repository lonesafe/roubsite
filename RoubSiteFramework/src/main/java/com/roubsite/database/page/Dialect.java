package com.roubsite.database.page;

import com.roubsite.database.page.cache.Cache;
import com.roubsite.database.page.cache.CacheFactory;

public interface Dialect {

	static Cache<String, String> cache_count_sql = CacheFactory.createCache("count");
	static Cache<String, String> cache_page_sql = CacheFactory.createCache("page");

	/**
	 * 生成count查询语句
	 * 
	 * @param sourceSql 查询语句
	 * @return
	 */
	public String getCountSql(String sourceSql);

	/**
	 * 生成分页查询语句
	 * 
	 * @param sourceSql 查询语句
	 * @param offset    从第几行开始
	 * @param rows      取出行数
	 * @return
	 */
	public Page getPageSql(String sourceSql, Object[] params, int[] types, int offset, int rows);

}
