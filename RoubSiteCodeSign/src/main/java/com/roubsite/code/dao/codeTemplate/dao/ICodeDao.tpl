package ${pack}.dao.${_mode};

import java.util.List;

import com.roubsite.database.bean.Record;
import com.roubsite.database.dao.DataSet;

public interface I${mode}Dao {
	public DataSet queryd(Record record, int start, int rows);

	public DataSet getInfoById(String id);

	public int saveData(Record record, Class<?> bean);

	public int del(List ids) throws Exception;
}
