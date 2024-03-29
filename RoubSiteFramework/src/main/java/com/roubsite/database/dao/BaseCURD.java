package com.roubsite.database.dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.roubsite.database.RSConnection;
import com.roubsite.database.page.PageHelper;
import com.roubsite.utils.ConfUtils;
import com.roubsite.utils.JsonUtils;

public class BaseCURD {
	private RSConnection conn = null;
	private PageHelper pageHelper;
	private String dataSource;
	private PreparedStatement pStatement = null;
	static Logger log = LoggerFactory.getLogger(BaseCURD.class);

	public void init(RSConnection conn, PageHelper pageHelper, String ds) throws Exception {
		this.dataSource = ds;
		this.conn = conn;
		this.setPageHelper(pageHelper);
	}

	public String getDataSourceType() {
		return ConfUtils.getStringConf("RoubSite.DataSourcePool.dataSources." + this.dataSource + ".type", "1");
	}

	/**
	 * 执行查询语句，返回查询结果
	 *
	 * @param sql
	 * @param args  参数数组
	 * @param types 参数类型数组 java.sql.Types中的常量
	 * @return 查询结果列表
	 */
	public List<Map<String, Object>> query(String sql, Object[] args, int[] types) {
		log.info("执行的sql:" + sql);
		try {
			log.info("传入的参数:" + JsonUtils.convertToString(args));
		} catch (IOException e1) {
		}
		ResultSet resultset = null;
		LinkedList<Map<String, Object>> list = new LinkedList<>();

		if (null == args) {
			args = new Object[] {};
		}
		if (null == types) {
			types = new int[] {};
		}
		try {
			pStatement = conn.getConn().prepareStatement(sql);
		} catch (Exception e) {
			this.conn.setError(true);
			try {
				if (null != pStatement && !pStatement.isClosed()) {
					pStatement.close();
				}
			} catch (Exception e3) {
			}
			log.error("预编译sql出错", e);
			return list;
		}

		int iPos = 0;
		if (null != args) {
			if (args.length != types.length) {
				log.error("sql参数数组（args）中的成员数和参数类型数组（types）中的成员数不同");
				try {
					if (null != pStatement && !pStatement.isClosed()) {
						pStatement.close();
					}
				} catch (Exception e) {
				}
				return list;
			}
			try {
				for (int i = 0; i < args.length; i++) {
					Object objParm = args[i];
					pStatement.setObject(++iPos, objParm, types[i]);
				}
			} catch (Exception e) {
				this.conn.setError(true);
				log.error("预编译sql出错", e);
				try {
					if (null != pStatement && !pStatement.isClosed()) {
						pStatement.close();
					}
				} catch (Exception e3) {
				}
				return list;
			}

		}
		try {
			resultset = pStatement.executeQuery();
		} catch (Exception e) {
			this.conn.setError(true);
			log.error("执行query出错", e);
			try {
				if (null != resultset && !resultset.isClosed()) {
					resultset.close();
				}
			} catch (Exception e2) {
				this.conn.setError(true);
				log.error("关闭resultset出错", e2);
			}
			try {
				if (!pStatement.isClosed()) {
					pStatement.close();
				}
			} catch (SQLException e1) {
				this.conn.setError(true);
				log.error("关闭statement出错", e1);
			}
			return list;
		}
		try {
			ResultSetMetaData metaDate = resultset.getMetaData();
			int columnCount = metaDate.getColumnCount();
			while (resultset.next()) {
				Map<String, Object> rowData = new LinkedHashMap<String, Object>();
				for (int k = 1; k <= columnCount; k++) {
					String metaDateKey = metaDate.getColumnLabel(k);// 获取列名
					Object re = resultset.getObject(k);
					// Clob字段另行处理
					if (resultset.getObject(k) instanceof java.sql.Clob) {
						re = clobToString((java.sql.Clob) re);
					}
					rowData.put(metaDateKey, re);
				}
				list.add(rowData);
			}
		} catch (Exception e) {
			this.conn.setError(true);
			log.error("组装返回数据失败", e);
		}
		try {
			if (null != resultset && !resultset.isClosed()) {
				resultset.close();
			}
			if (null != pStatement && !pStatement.isClosed()) {
				pStatement.close();
			}
			return list;
		} catch (Exception e) {
			this.conn.setError(true);
			try {
				if (null != resultset && !resultset.isClosed()) {
					resultset.close();
				}
			} catch (Exception e2) {
				this.conn.setError(true);
				log.error("关闭resultset出错", e2);
			}
			try {
				if (!pStatement.isClosed()) {
					pStatement.close();
				}
			} catch (SQLException e1) {
				this.conn.setError(true);
				log.error("关闭statement出错", e1);
			}
		}
		return list;
	}

	/**
	 * 将Clob转成String ,静态方法
	 *
	 * @param clob 字段
	 * @return 内容字串，如果出现错误，返回 null
	 */
	private String clobToString(java.sql.Clob clob) {
		if (clob == null)
			return null;
		StringBuffer sb = new StringBuffer();
		Reader clobStream = null;
		try {
			clobStream = clob.getCharacterStream();
			char[] b = new char[60000];// 每次获取60K
			int i = 0;
			while ((i = clobStream.read(b)) != -1) {
				sb.append(b, 0, i);
			}
		} catch (Exception ex) {
			this.conn.setError(true);
			sb = null;
		} finally {
			try {
				if (clobStream != null) {
					clobStream.close();
				}
			} catch (Exception e) {
				log.error("关闭stream出错", e);
			}
		}
		if (sb == null)
			return null;
		else
			return sb.toString();
	}

	/**
	 * 执行某个sql语句,并返回影响行数
	 *
	 * @param sql
	 * @param args
	 * @param types 参数类型数组 java.sql.Types中的常量
	 * @return 影响行数
	 */
	public int excute(String sql, Object[] args, int[] types) {
		log.info("执行的sql:" + sql);
		try {
			log.info("传入的参数:" + JsonUtils.convertToString(args));
		} catch (IOException e2) {
			this.conn.setError(true);
		}
		if (null == args) {
			args = new Object[] {};
		}
		if (null == types) {
			types = new int[] {};
		}
		int re = 0;
		try {
			pStatement = conn.getConn().prepareStatement(sql);
		} catch (SQLException e) {
			this.conn.setError(true);
			log.error("打开statement出错", e);
			return 0;
		}
		int iPos = 0;
		if (args.length != types.length) {
			throw new RuntimeException("sql参数数组（args）中的成员数和参数类型数组（types）中的成员数不同");
		}
		for (int i = 0; i < args.length; i++) {
			Object objParm = args[i];
			try {
				switch (types[i]) {
				case Types.BLOB:
					if (objParm instanceof Blob) {
						pStatement.setBlob(++iPos, (Blob) objParm);
					} else if (objParm instanceof byte[]) {
						pStatement.setBlob(++iPos, new ByteArrayInputStream((byte[]) objParm));
					} else if (objParm instanceof InputStream) {
						pStatement.setBlob(++iPos, (InputStream) objParm);
					} else if (objParm instanceof String) {
						pStatement.setBlob(++iPos, new ByteArrayInputStream(((String) objParm).getBytes()));
					} else {
						pStatement.setObject(++iPos, objParm, types[i]);
					}
					break;
				case Types.CLOB:
					if (objParm instanceof Clob) {
						pStatement.setClob(++iPos, (Clob) objParm);
					} else if (objParm instanceof byte[]) {
						pStatement.setClob(++iPos, new InputStreamReader(new ByteArrayInputStream((byte[]) objParm)));
					} else if (objParm instanceof InputStream) {
						pStatement.setClob(++iPos, new InputStreamReader((InputStream) objParm));
					} else if (objParm instanceof String) {
						pStatement.setClob(++iPos, new StringReader(((String) objParm)));
					} else {
						pStatement.setObject(++iPos, objParm, types[i]);
					}
					break;
				case Types.DATE:
					if (objParm instanceof Date) {
						pStatement.setDate(++iPos, new java.sql.Date(((Date) objParm).getTime()));
					} else if (objParm instanceof java.sql.Date) {
						pStatement.setDate(++iPos, (java.sql.Date) objParm);
					} else if (objParm instanceof Long) {
						pStatement.setDate(++iPos, new java.sql.Date((Long) objParm));
					} else {
						pStatement.setObject(++iPos, objParm, types[i]);
					}
					break;
				default:
					pStatement.setObject(++iPos, objParm, types[i]);
					break;
				}
			} catch (SQLException e) {
				this.conn.setError(true);
				log.error("sql编译错误", e);
				try {
					if (!pStatement.isClosed()) {
						pStatement.close();
					}
				} catch (SQLException e1) {
					log.error("关闭statement出错", e1);
				}
				return 0;
			}
		}
		try {
			re = pStatement.executeUpdate();
		} catch (SQLException e) {
			this.conn.setError(true);
			log.error("sql执行错误", e);
			try {
				if (!pStatement.isClosed()) {
					pStatement.close();
				}
			} catch (SQLException e1) {
				this.conn.setError(true);
				log.error("关闭statement出错", e1);
			}
			return 0;
		}

		try {
			if (!pStatement.isClosed()) {
				pStatement.close();
			}
		} catch (SQLException e1) {
			this.conn.setError(true);
			log.error("关闭statement出错", e1);
		}
		return re;

	}

	public RSConnection getConn() {
		return conn;
	}

	public PageHelper getPageHelper() {
		return pageHelper;
	}

	public void setPageHelper(PageHelper pageHelper) {
		this.pageHelper = pageHelper;
	}
}
