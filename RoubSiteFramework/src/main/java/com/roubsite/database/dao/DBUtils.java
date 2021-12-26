package com.roubsite.database.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.roubsite.holder.RSDataSourceHolder;
import com.roubsite.utils.ConfUtils;
import com.roubsite.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtils {
	private DruidDataSource dataSource;
	static Logger log = LoggerFactory.getLogger(BaseCURD.class);

	public DBUtils(String dataSourceName, String driverClassName, String url, String username, String password,
			int size, boolean keepAlive, long maxWaitMillis, int maxWaitThreadCount) throws SQLException {
		this.setDataSource(RSDataSourceHolder.getInstance().get().getDataSource(dataSourceName, driverClassName, url,
				username, password, size, keepAlive, maxWaitMillis, maxWaitThreadCount));
	}

	public DBUtils(String dataSource) throws SQLException {
		this.setDataSource(RSDataSourceHolder.getInstance().get().getDataSource(dataSource));
	}

	/**
	 * 获得连接对象
	 *
	 * @return 连接对象
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		return getDataSource().getConnection();
	}

	/**
	 * 关闭三剑客
	 *
	 * @throws SQLException
	 */
	public void close(ResultSet rs, PreparedStatement pstmt, Connection con) {

		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (pstmt != null)
				pstmt.close();
		} catch (Exception e) {
		}
		try {
			if (con != null)
				con.close();
		} catch (Exception e) {
		}
	}

	/**
	 * 执行更新
	 *
	 * @param sql    传入的预设的 sql语句
	 * @param params 问号参数列表
	 * @return 影响行数
	 */
	public int execUpdate(String sql, Object[] params) {
		log.info("执行的sql:" + sql);
		try {
			log.info("传入的参数:" + JsonUtils.convertToString(params));
		} catch (IOException e1) {
		}
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			conn = this.getConnection();// 获得连接对象
			pstmt = conn.prepareStatement(sql);// 获得预设语句对象

			if (params != null) {
				// 设置参数列表
				for (int i = 0; i < params.length; i++) {
					// 因为问号参数的索引是从1开始，所以是i+1，将所有值都转为字符串形式，好让setObject成功运行
					pstmt.setObject(i + 1, params[i] + "");
				}
			}
			int a = pstmt.executeUpdate(); // 执行更新，并返回影响行数
			this.close(null, pstmt, conn);
			return a;

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.close(null, pstmt, conn);
		}
		return 0;
	}

	/**
	 * 执行查询
	 *
	 * @param sql    传入的预设的 sql语句
	 * @param params 问号参数列表
	 * @return 查询后的结果
	 */
	public List<Map<String, Object>> execQuery(String sql, Object[] params) {
		log.info("执行的sql:" + sql);
		try {
			log.info("传入的参数:" + JsonUtils.convertToString(params));
		} catch (IOException e1) {
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = this.getConnection();// 获得连接对象
			pstmt = conn.prepareStatement(sql);// 获得预设语句对象

			if (params != null) {
				// 设置参数列表
				for (int i = 0; i < params.length; i++) {
					// 因为问号参数的索引是从1开始，所以是i+1，将所有值都转为字符串形式，好让setObject成功运行
					pstmt.setObject(i + 1, params[i] + "");
				}
			}

			// 执行查询
			rs = pstmt.executeQuery();

			List<Map<String, Object>> al = new ArrayList<Map<String, Object>>();

			// 获得结果集元数据（元数据就是描述数据的数据，比如把表的列类型列名等作为数据）
			ResultSetMetaData rsmd = rs.getMetaData();

			// 获得列的总数
			int columnCount = rsmd.getColumnCount();

			// 遍历结果集
			while (rs.next()) {
				Map<String, Object> hm = new HashMap<String, Object>();
				for (int i = 0; i < columnCount; i++) {
					// 根据列索引取得每一列的列名,索引从1开始
					String columnName = rsmd.getColumnLabel(i + 1);
					// 根据列名获得列值
					Object columnValue = rs.getObject(i + 1);
					// 将列名作为key，列值作为值，放入 hm中，每个 hm相当于一条记录
					hm.put(columnName, columnValue);
				}
				// 将每个 hm添加到al中, al相当于是整个表，每个 hm是里面的一条记录
				al.add(hm);
			}
			this.close(rs, pstmt, conn);
			return al;

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.close(rs, pstmt, conn);
		}
		return null;
	}

	public DruidDataSource getDataSource() {
		return dataSource;
	}

	private void setDataSource(DruidDataSource dataSource) {
		this.dataSource = dataSource;
	}
}