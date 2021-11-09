package com.roubsite.database.page;

public interface Dialect {
	/**
	 * 生成 count 查询 sql
	 *
	 * @param sourceSql       sql
	 * @return
	 */
	String getCountSql(String sourceSql);

}
