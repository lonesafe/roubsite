package ${pack}.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import ${pack}.bean.${mode};
import ${pack}.dao.${_mode}.${mode}Dao;
import ${pack}.dao.${_mode}.I${mode}Dao;

import com.roubsite.database.bean.Record;
import com.roubsite.utils.JsonUtils;
import com.roubsite.utils.StringUtils;
import com.roubsite.web.action.RSAction;
import com.roubsite.database.dao.DataSet;
import com.roubsite.database.annotation.Dao;

@SuppressWarnings("rawtypes")
public class ${mode}Action extends RSAction {
	@Dao(dataSource = "${dataSource}", impl = ${mode}Dao.class)
	I${mode}Dao ${_mode}Dao;

	@Override
	public void execute() throws Exception {

		this.display("/${_mode}/list${mode}.html");
	}

	public void doQuery() throws IOException {
		Record record = new Record();
		Map paramSet = (Map) this.getParamSet(false);
		record.fromBeanMap((Map) paramSet.get("__data__"));
		DataSet ret = ${_mode}Dao.queryd(record, (int)paramSet.get("__offset__"),
				(int)paramSet.get("__limit__"));
		this.print(JsonUtils.convertToString(ret));
	}
	
	public void doDel() throws Exception {
		String jsonString = (String) this.getParamSet(true);
		List ids = JsonUtils.readToObject(jsonString,ArrayList.class);
		int r = ${_mode}Dao.del(ids);
		Map ret = new HashMap();
		ret.put("status", r);
		this.print(JsonUtils.convertToString(ret));
	}

	public void doUpdate() throws IOException, ServletException {
		String id = this.g("id");
		if (StringUtils.isEmpty(id)) {
			this.error(403, "非法访问");
		}
		DataSet ret = ${_mode}Dao.getInfoById(id);
		this.assign("retInfo", JsonUtils.convertToString(ret));
		this.display("/${_mode}/update${mode}.html");
	}

	public void doInsert() throws ServletException, IOException {
		this.display("/${_mode}/insert${mode}.html");
	}

	public void doSubmitUpdate() throws IOException {
		// 有数据提交
		String paramSet = (String) this.getParamSet(true);
		Record r = new Record();
		r.setState(Record.STATE_UPDATE);
		r.fromJson(paramSet);
		${mode} bean = (${mode}) r.toBean(${mode}.class);
		// 此处可对bean进行操作

		// --------------------
		r.fromBean(bean);
		save(r, bean);
	}

	public void doSubmitInsert() throws IOException {
		// 有数据提交
		String paramSet = (String) this.getParamSet(true);
		Record r = new Record();
		r.setState(Record.STATE_INSERT);
		r.fromJson(paramSet);
		${mode} bean = (${mode}) r.toBean(${mode}.class);
		// 此处可对bean进行操作
		
		// --------------------
		r.fromBean(bean);
		save(r, bean);
	}

	public void save(Record r, Object bean) throws IOException {
		int status = ${_mode}Dao.saveData(r, ${mode}.class);
		Map ret = new HashMap();
		ret.put("status", status);
		this.print(JsonUtils.convertToString(ret));
	}
}
