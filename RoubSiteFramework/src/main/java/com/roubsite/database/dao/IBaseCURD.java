package com.roubsite.database.dao;

import java.util.List;
import java.util.Map;

import com.roubsite.database.RSConnection;

public interface IBaseCURD {
	public void init(RSConnection conn, String ds) throws Exception;

	/**
	 * 执行查询语句，返回查询结果
	 * 
	 * @param sql
	 * @param args  参数数组
	 * @param types 参数类型数组 java.sql.Types中的常量
	 * @return 查询结果列表
	 * @throws Exception
	 */
	public List<Map<String,Object>> query(String sql, Object[] args, int[] types) throws Exception;

	/**
	 * 执行某个sql语句,并返回影响行数
	 * 
	 * @param sql
	 * @param args
	 * @param types 参数类型数组 java.sql.Types中的常量
	 * @return 影响行数
	 * @throws Exception
	 */
	public int excute(String sql, Object[] args, int[] types) throws Exception;

	/**
	 * 释放当前数据库连接
	 */
//	public void closeConn();

	/**
	 * 释放当前数据库连接
	 */
//	public void closeConn(boolean autoCommit);

	/**
	 * 获取数据库连接
	 * 
	 * @return
	 * @throws Exception
	 */
	public RSConnection getConn() throws Exception;
}
