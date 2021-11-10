package com.roubsite.database.page;

public class Page {
	private String sql;
	private Object[] params;
	private int[] types;

	public Page(String sql, Object[] params, int[] types) {
		this.sql = sql;
		this.params = params;
		this.types = types;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public int[] getTypes() {
		return types;
	}

	public void setTypes(int[] types) {
		this.types = types;
	}

}
