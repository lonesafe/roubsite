package com.roubsite.code.action;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.roubsite.code.dao.BeanField;
import com.roubsite.code.dao.CodeDao;
import com.roubsite.code.dao.ICodeDao;
import com.roubsite.database.RSConnection;
import com.roubsite.database.dao.RSDaoFactory;
import com.roubsite.holder.RSDataSourceHolder;
import com.roubsite.utils.ConfUtils;
import com.roubsite.utils.JsonUtils;
import com.roubsite.utils.YmlUtils;
import com.roubsite.web.action.RSAction;

public class CodeAction extends RSAction {

	/**
	 * 生成代码
	 *
	 * @throws Exception
	 * @throws SQLException
	 */
	public void doCodeSubmitForm() throws SQLException, Exception {
		String dataSource = getString(this.$_P("dataSource"));
		ICodeDao codeDao = (ICodeDao) new RSDaoFactory().getDao(CodeDao.class, dataSource,
				new RSConnection(RSDataSourceHolder.getInstance().get().getDataSource(dataSource).getConnection(),
						RSDataSourceHolder.getInstance().get().getPageHelperMap(dataSource)));
		try {
			// 通过配置文件获取数据库类型
			String type = ConfUtils.getStringConf("RoubSite.DataSourcePool.dataSources." + dataSource + ".type", "");
			String tableName = getString(this.$_P("tableName"));
			String model = getString(this.$_P("mode"));
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
		Map<String, Object> m = YmlUtils.getAllConfig();
		List<String> groupList = new ArrayList<String>();
		List<String> dataSourceList = new ArrayList<String>();
		for (String key : m.keySet()) {
			if (key.startsWith("RoubSite.global.group")) {
				groupList.add(key.replace("RoubSite.global.group.", ""));
			} else if (key.startsWith("RoubSite.DataSourcePool.dataSources") && key.endsWith(".type")) {
				dataSourceList.add(key.replace("RoubSite.DataSourcePool.dataSources.", "").replace(".type", ""));
			}
		}

		Map<String, Object> map = new HashMap<>();
		map.put("groupList", groupList);
		map.put("dataSourceList", dataSourceList);
		this.assign(map);
		this.display("codeSign.html");
	}

	public void doGetTables() throws Exception {
		String dataSource = this.$_G("dataSource");
		ICodeDao codeDao = (ICodeDao) new RSDaoFactory().getDao(CodeDao.class, dataSource,
				new RSConnection(RSDataSourceHolder.getInstance().get().getDataSource(dataSource).getConnection(),
						RSDataSourceHolder.getInstance().get().getPageHelperMap(dataSource)));
		try {
			String type = ConfUtils.getStringConf("RoubSite.DataSourcePool.dataSources." + dataSource + ".type", "");
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
		String dataSource = this.$_G("dataSource");
		String tableName = this.$_G("tableName");
		ICodeDao codeDao = (ICodeDao) new RSDaoFactory().getDao(CodeDao.class, dataSource,
				new RSConnection(RSDataSourceHolder.getInstance().get().getDataSource(dataSource).getConnection(),
						RSDataSourceHolder.getInstance().get().getPageHelperMap(dataSource)));
		try {
			String type = ConfUtils.getStringConf("RoubSite.DataSourcePool.dataSources." + dataSource + ".type", "");
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
		} catch (

		Exception e) {
			e.printStackTrace();
		} finally {
			codeDao.getConn().getConn().close();
		}
	}

	@Override
	public void execute() throws Exception {

	}

}
