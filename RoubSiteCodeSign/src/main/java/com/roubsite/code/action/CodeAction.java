package com.roubsite.code.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;

import com.roubsite.code.dao.BeanField;
import com.roubsite.code.dao.CodeDao;
import com.roubsite.code.dao.ICodeDao;
import com.roubsite.database.RSConnection;
import com.roubsite.database.dao.RSDaoFactory;
import com.roubsite.holder.RSDataSourceHolder;
import com.roubsite.utils.*;
import com.roubsite.web.action.RSAction;

public class CodeAction extends RSAction {

	/**
	 * 生成代码
	 *
	 * @throws Exception
	 * @throws SQLException
	 */
	public void doCodeSubmitForm() throws SQLException, Exception {
		String dataSource = this.g("dataSource");
		ICodeDao codeDao = (ICodeDao) new RSDaoFactory().getDao(CodeDao.class, dataSource,
				new RSConnection(RSDataSourceHolder.getInstance().get().getDataSource(dataSource).getConnection()));
		try {
			// 通过配置文件获取数据库类型
			String type = ConfUtils.getConf(dataSource + ".type", "dataSource.properties",
					new String[] { "RoubSite", "DataSourcePool", "dataSources", dataSource, "type" });
			String tableName = this.g("tableName");
			String model = this.g("mode");
			String[] _searchFields = this.request.getParameterValues("searchFields");

			LinkedList<BeanField> searchFields = new LinkedList<BeanField>();

			for (String field : _searchFields) {
				String[] fieldInfo = field.split("_________ROUBSITE________");
				if (fieldInfo.length > 1) {
					BeanField bf = new BeanField();
					bf.setKey(fieldInfo[0]);
					bf.setName(fieldInfo[1]);
					searchFields.add(bf);
				}
			}

			if ("1".equals(type)) {
				// mysql数据库
				codeDao.makeMysqlFiles(tableName, searchFields, model, dataSource);
			} else {
				// oracle数据库
				codeDao.makeOracleFiles(tableName, searchFields, model, dataSource);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			codeDao.getConn().getConn().close();
		}

	}

	public void doCodeSignForm() throws ServletException, IOException {
		Properties pro = new Properties();
		InputStream inputStream = null;
		inputStream = this.getClass().getClassLoader().getResourceAsStream("config.properties");
		BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
		pro.load(bf);
		Set<Entry<Object, Object>> ent = pro.entrySet();
		List<String> groupList = new ArrayList<String>();
		for (Entry<Object, Object> e : ent) {
			String tmpString = e.getKey().toString();
			if (tmpString.indexOf("global.group") != -1) {
				groupList.add(tmpString.substring(13));
			}
		}

		inputStream = this.getClass().getClassLoader().getResourceAsStream("dataSource.properties");
		bf = new BufferedReader(new InputStreamReader(inputStream));
		pro.load(bf);
		ent = pro.entrySet();
		List<String> dataSourceList = new ArrayList<String>();
		for (Entry<Object, Object> e : ent) {
			String tmpString = e.getKey().toString();
			if (tmpString.indexOf("type") != -1) {
				dataSourceList.add(tmpString.split("\\.")[0]);
			}
		}

		Map<String, Object> map = new HashMap<>();
		map.put("groupList", groupList);
		map.put("dataSourceList", dataSourceList);
		this.assign(map);
		this.display("codeSign.html");
	}

	public void doGetTables() throws Exception {
		String dataSource = this.g("dataSource");
		ICodeDao codeDao = (ICodeDao) new RSDaoFactory().getDao(CodeDao.class, dataSource,
				new RSConnection(RSDataSourceHolder.getInstance().get().getDataSource(dataSource).getConnection()));
		try {
//			ICodeDao codeDao = (ICodeDao) new RSDaoFactory().getDao(CodeDao.class, dataSource);
			String type = ConfUtils.getConf(dataSource + ".type", "dataSource.properties",
					new String[] { "RoubSite", "DataSourcePool", "dataSources", dataSource, "type" });
			String sql = "select   *   from   user_tables";
			switch (type) {
			case "1":
				sql = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='base table' AND TABLE_SCHEMA IN (SELECT DATABASE())";
				break;

			default:
				sql = "SELECT * FROM USER_TABLES";
				break;
			}
			List<?> list = codeDao.query(sql, null, null);
			this.print(JsonUtils.convertToString(list));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			codeDao.getConn().getConn().close();
		}

	}

	public void doGetColunms() throws Exception {
		String dataSource = this.g("dataSource");
		String tableName = this.g("tableName");
		ICodeDao codeDao = (ICodeDao) new RSDaoFactory().getDao(CodeDao.class, dataSource,
				new RSConnection(RSDataSourceHolder.getInstance().get().getDataSource(dataSource).getConnection()));
		try {
			String type = ConfUtils.getConf(dataSource + ".type", "dataSource.properties",
					new String[] { "RoubSite", "DataSourcePool", "dataSources", dataSource, "type" });
			String sql = "";
			switch (type) {
			case "1":
				sql = "SELECT COLUMN_NAME AS ID,COLUMN_COMMENT AS NAME  FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=?  AND TABLE_SCHEMA IN (SELECT DATABASE())";
				break;

			default:
				sql = "SELECT COLUMN_NAME AS ID,COMMENTS AS NAME  FROM USER_COL_COMMENTS WHERE TABLE_NAME=UPPER(?)";
				break;
			}

			List<?> list = codeDao.query(sql, new String[] { tableName }, new int[] { Types.VARCHAR });
			this.print(JsonUtils.convertToString(list));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			codeDao.getConn().getConn().close();
		}
	}

	@Override
	public void execute() throws Exception {

	}

}
