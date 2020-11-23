package com.roubsite.code.dao;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class CodeSign {
	private String pack;
	private String _mode;// 首字母小写
	private String mode;
	private String groupName;
	private String keyField;
	private String _keyField;
	private String dataSource;
	private String tableName;
	private List<BeanField> searchFields;
	private List<BeanField> _searchFields = new LinkedList<BeanField>();
	private List<BeanField> allFields;
	private List<BeanField> _allFields = new LinkedList<BeanField>();

	CodeSign(String pack, String _modeName, String modeName, String groupName, String _keyField, String keyField,
			String dataSource, String tableName, List<BeanField> searchFields, List<BeanField> allFields) {
		this.pack = pack;
		this._mode = _modeName;
		this.mode = modeName;
		this.groupName = groupName;
		this.keyField = keyField;
		this._keyField = _keyField;
		this.dataSource = dataSource;
		this.tableName = tableName;
		this.searchFields = searchFields;
		this.allFields = allFields;
		for (int i = 0; i < allFields.size(); i++) {
			BeanField bf = new BeanField();
			BeanField bf2 = allFields.get(i);
			String key = CodeUtils.getModeName(bf2.getKey());

			bf.setKey(key.substring(0, 1).toLowerCase() + key.substring(1));
			bf.setName(bf2.getName());
			this._allFields.add(bf);
		}
		for (int i = 0; i < searchFields.size(); i++) {
			BeanField bf = new BeanField();
			BeanField bf2 = searchFields.get(i);
			String key = CodeUtils.getModeName(bf2.getKey());

			bf.setKey(key.substring(0, 1).toLowerCase() + key.substring(1));
			bf.setName(bf2.getName());
			this._searchFields.add(bf);
		}
	}

	public void replaceAll(String inFilePath, String outFilePath) {
		File file = new File(outFilePath);
		File fileParent = file.getParentFile();
		if (!fileParent.exists()) {
			fileParent.mkdirs();
		}
		try {
			file.createNewFile();
			Configuration conf = new Configuration();
			// 加载模板文件(模板的路径)
			conf.setClassForTemplateLoading(this.getClass(), "/com/roubsite/code/dao/");
			// 加载模板
			Template template = conf.getTemplate(inFilePath);
			// 定义数据
			Map root = new HashMap();
			root.put("pack", pack);
			root.put("_mode", _mode);
			root.put("mode", mode);
			root.put("groupName", groupName);
			root.put("keyField", keyField);
			root.put("_keyField", _keyField);
			root.put("dataSource", dataSource);
			root.put("tableName", tableName);
			root.put("searchFields", searchFields);
			root.put("allFields", allFields);
			root.put("_allFields", _allFields);
			root.put("utils", new CodeUtils());
			root.put("_searchFields", _searchFields);

			// 定义输出
			Writer out = new FileWriter(outFilePath);
			template.process(root, out);
			System.out.println("转换成功" + outFilePath);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
