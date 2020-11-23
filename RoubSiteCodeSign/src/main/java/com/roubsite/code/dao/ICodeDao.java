package com.roubsite.code.dao;

import java.util.LinkedList;
import java.util.Map;

import com.roubsite.database.dao.IBaseCURD;

public interface ICodeDao extends IBaseCURD {
	public void makeMysqlFiles(String tableName, LinkedList<BeanField> selectFields, String groupName,
                               String dataSource) throws Exception;

	public void makeOracleFiles(String tableName, LinkedList<BeanField> selectFields, String groupName,
                                String dataSource) throws Exception;

}
