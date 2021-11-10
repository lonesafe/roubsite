package com.roubsite.database;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.roubsite.database.page.PageHelper;

public class RSConnection {
	private boolean error = false;
	private DruidPooledConnection conn;
	private PageHelper pageHelper;

	public RSConnection(DruidPooledConnection conn, PageHelper pageHelper) {
		this.conn = conn;
		this.pageHelper = pageHelper;
	}

	public DruidPooledConnection getConn() {
		return this.conn;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public PageHelper getPageHelper() {
		return pageHelper;
	}
}
