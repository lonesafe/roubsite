package com.roubsite.database;

import com.alibaba.druid.pool.DruidPooledConnection;

public class RSConnection {
	private boolean error = false;
	private DruidPooledConnection conn;

	public RSConnection(DruidPooledConnection conn) {
		this.conn = conn;
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

}
