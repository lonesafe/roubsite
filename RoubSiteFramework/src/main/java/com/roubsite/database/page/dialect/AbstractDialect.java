package com.roubsite.database.page.dialect;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.roubsite.database.page.Dialect;
import com.roubsite.database.page.parser.CountSqlParser;
import com.roubsite.utils.StringUtils;

public abstract class AbstractDialect implements Dialect {

	@Override
	public String getCountSql(String sourceSql) {
		String cacheSql = cache_count_sql.get(sourceSql);
		if (StringUtils.isNotEmpty(cacheSql)) {
			return cacheSql;
		}
		cacheSql = new CountSqlParser().getSmartCountSql(sourceSql);
		cache_count_sql.put(sourceSql, cacheSql);
		return cacheSql;
	}

	protected Object[] rebuildParams(Object[] params, Object... objs) {
		LinkedList<Object> alist = new LinkedList<Object>();
		if (StringUtils.isNotEmptyObject(params)) {
			for (int j = 0; j < params.length; j++) {
				alist.add(params[j]);
			}
		}
		if (StringUtils.isNotEmptyObject(objs)) {
			for (int k = 0; k < objs.length; k++) {
				alist.add(objs[k]);
			}
		}
		Object c[] = new Object[alist.size()];
		for (int i = 0; i < alist.size(); i++) {
			c[i] = alist.get(i);
		}
		return c;

	}

	protected int[] rebuildTypes(int[] params, int... objs) {
		LinkedList<Integer> alist = new LinkedList<Integer>();
		if (StringUtils.isNotEmptyObject(params)) {
			for (int j = 0; j < params.length; j++) {
				alist.add(params[j]);
			}
		}
		if (StringUtils.isNotEmptyObject(objs)) {
			for (int k = 0; k < objs.length; k++) {
				alist.add(objs[k]);
			}
		}
		int c[] = new int[alist.size()];
		for (int i = 0; i < alist.size(); i++) {
			c[i] = alist.get(i);
		}
		return c;
	}
}
