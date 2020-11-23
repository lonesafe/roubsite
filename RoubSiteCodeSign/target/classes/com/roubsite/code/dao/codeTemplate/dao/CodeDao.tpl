package ${pack}.dao.${_mode};

import java.sql.Types;
import java.util.List;

import ${pack}.bean.${mode};
import com.roubsite.database.bean.Record;
import com.roubsite.database.dao.DataSet;
import com.roubsite.database.dao.EntityDao;

public class ${mode}Dao extends EntityDao implements I${mode}Dao {

	@Override
	public DataSet getInfoById(String id) {
		DataSet ds = this.queryBean("SELECT * FROM ${tableName} WHERE ${keyField} =?", new String[] { id },
				new int[] { Types.VARCHAR }, false, 0, 0, ${mode}.class);
		return ds;
	}

	@Override
	public DataSet queryd(Record record, int start, int rows) {
		return this.queryBean(record.toBean(${mode}.class), null, true, start, rows);
	}

	@Override
	public int saveData(Record record, Class<?> bean) {
		return this.save(record, bean);
	}

	@Override
	public int del(List ids) throws Exception {
		StringBuffer delSql = new StringBuffer("DELETE FROM ${tableName} WHERE ${keyField} IN (");
		for (int i = 0; i < ids.size(); i++) {
			delSql.append("'");
			delSql.append(ids.get(i));
			delSql.append("',");
		}
		String sql = delSql.substring(0, delSql.length() - 1);
		sql = sql + ")";
		
		return this.excute(sql, new Object[] {}, new int[] {});
	}

}
