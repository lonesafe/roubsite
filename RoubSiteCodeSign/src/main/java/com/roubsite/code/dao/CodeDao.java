package com.roubsite.code.dao;

import java.io.IOException;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.roubsite.database.dao.EntityDao;
import com.roubsite.utils.ConfUtils;
import com.roubsite.utils.StringUtils;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CodeDao extends EntityDao implements ICodeDao {

    @Override
    public void makeOracleFiles(String tableName, LinkedList<BeanField> fields, String groupName, String dataSource)
            throws Exception {
        List pkList = this.query(
                "SELECT * FROM user_cons_columns WHERE constraint_name = ( SELECT constraint_name FROM user_constraints WHERE table_name = ? AND constraint_type = 'P' )",
                new String[]{tableName}, new int[]{Types.VARCHAR});
        String pk = "id";
        if (pkList.size() > 0) {
            pk = (String) ((Map) pkList.get(0)).get("COLUMN_NAME");
        }
        List fileList = this.query("SELECT *  FROM USER_COL_COMMENTS WHERE TABLE_NAME=UPPER(?)",
                new String[]{tableName}, new int[]{Types.VARCHAR});
        Map map = new HashMap();
        List<BeanField> allFields = new LinkedList<>();
        for (int i = 0; i < fileList.size(); i++) {
            Map m = (Map) fileList.get(i);
            BeanField bf = new BeanField();
            bf.setKey((String) m.get("COLUMN_NAME"));
            if (StringUtils.isNotEmptyObject(m.get("COMMENTS"))) {
                bf.setName((String) m.get("COMMENTS"));
            } else {
                bf.setName((String) m.get("COLUMN_NAME"));
            }
            allFields.add(bf);
        }
        createCodeFile(tableName, pk, fields, allFields, groupName, 1, dataSource);
    }

    @Override
    public void makeMysqlFiles(String tableName, LinkedList<BeanField> fields, String groupName, String dataSource)
            throws Exception {
        List pkList = this.query(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND COLUMN_KEY='PRI' AND TABLE_SCHEMA IN (SELECT DATABASE())",
                new String[]{tableName}, new int[]{Types.VARCHAR});
        String pk = "id";
        if (pkList.size() > 0) {
            pk = (String) ((Map) pkList.get(0)).get("COLUMN_NAME");
        }
        List fieldList = this.query(
                "SELECT COLUMN_NAME,COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=? AND TABLE_SCHEMA IN (SELECT DATABASE())",
                new String[]{tableName}, new int[]{Types.VARCHAR});
        List<BeanField> allFields = new LinkedList<BeanField>();
        for (int i = 0; i < fieldList.size(); i++) {
            Map m = (Map) fieldList.get(i);
            BeanField bf = new BeanField();
            bf.setKey((String) m.get("COLUMN_NAME"));
            if (StringUtils.isNotEmptyObject(m.get("COLUMN_COMMENT"))) {
                bf.setName((String) m.get("COLUMN_COMMENT"));
            } else {
                bf.setName((String) m.get("COLUMN_NAME"));
            }
            allFields.add(bf);
        }
        createCodeFile(tableName, pk, fields, allFields, groupName, 0, dataSource);
    }

    /**
     * 生成代码文件
     *
     * @param tableName    表名
     * @param groupName    分组名
     * @param databaseType 0:mysql;1:oracle
     * @throws IOException
     */
    public void createCodeFile(String tableName, String keyField, LinkedList<BeanField> searchFields,
                               List<BeanField> _allFields, String groupName, int databaseType, String dataSource) throws IOException {

        String KeyField = new String(keyField);
        keyField = CodeUtils.getModeName(keyField.toLowerCase());
        keyField = keyField.substring(0, 1).toLowerCase() + keyField.substring(1);

        tableName = tableName.toLowerCase();
        String modeName = CodeUtils.getModeName(tableName);
        String _modeName = modeName.substring(0, 1).toLowerCase() + modeName.substring(1);
        String pack = ConfUtils.getConf("global.group." + groupName, "config.properties", new String[]{"RoubSite", "global", "group", groupName});
        String packPath = pack.replaceAll("\\.", "/");
        String basePath = ConfUtils.getConf("codeSign.outputPath", "codeSign.properties",new String[]{"RoubSite","codeSign","outputPath"});

        CodeSign cs = new CodeSign(pack, _modeName, modeName, groupName, keyField, KeyField, dataSource, tableName,
                searchFields, _allFields);
        // 生成action
        String actionTpl = getTplString("codeTemplate/action/CodeAction.tpl");
        cs.replaceAll(actionTpl, basePath + "/src/main/java/" + packPath + "/action/" + modeName + "Action.java");
        // 生成action结束

        // 生成dao
        String daoTpl = getTplString("codeTemplate/dao/CodeDao.tpl");
        cs.replaceAll(daoTpl, basePath + "/src/main/java/" + packPath + "/dao/" + _modeName + "/" + modeName + "Dao.java");
        // 生成dao结束

        // 生成dao接口
        String iDaoTpl = getTplString("codeTemplate/dao/ICodeDao.tpl");
        cs.replaceAll(iDaoTpl, basePath + "/src/main/java/" + packPath + "/dao/" + _modeName + "/" + "I" + modeName + "Dao.java");
        // 生成dao接口结束

        // 生成bean
        String bean = getTplString("codeTemplate/bean/Code.tpl");
        cs.replaceAll(bean, basePath + "/src/main/java/" + packPath + "/bean/" + modeName + ".java");
        // 生成bean结束

        // 生成列表页js
        String listJs = getTplString("codeTemplate/js/listCode.tpl");
        cs.replaceAll(listJs,
                basePath + "/src/main/webapp/static/" + groupName + "/" + _modeName + "/js/list" + modeName + ".js");
        // 生成列表页js结束

        // 生成编辑页js
        String insertJs = getTplString("codeTemplate/js/insertCode.tpl");
        String updateJs = getTplString("codeTemplate/js/updateCode.tpl");
        cs.replaceAll(insertJs,
                basePath + "/src/main/webapp/static/" + groupName + "/" + _modeName + "/js/insert" + modeName + ".js");
        cs.replaceAll(updateJs,
                basePath + "/src/main/webapp/static/" + groupName + "/" + _modeName + "/js/update" + modeName + ".js");
        // 生成编辑页js结束

        // 生成列表页html
        String listJsp = getTplString("codeTemplate/jsp/listCode.tpl");
        cs.replaceAll(listJsp,
                basePath + "/src/main/webapp/templates/" + groupName + "/" + _modeName + "/list" + modeName + ".html");
        // 生成列表页jsp结束

        // 生成编辑页html
        String insertJsp = getTplString("codeTemplate/jsp/insertCode.tpl");
        String updateJsp = getTplString("codeTemplate/jsp/updateCode.tpl");
        cs.replaceAll(insertJsp,
                basePath + "/src/main/webapp/templates/" + groupName + "/" + _modeName + "/insert" + modeName + ".html");
        cs.replaceAll(updateJsp,
                basePath + "/src/main/webapp/templates/" + groupName + "/" + _modeName + "/update" + modeName + ".html");

        System.out.println("代码生成成功，代码路径：" + basePath);
        // 生成编辑页jsp结束

    }

    public String getTplString(String tplPath) throws IOException {
//		InputStream is = this.getClass().getResourceAsStream(tplPath);
//		return IOUtils.toString(is, "utf-8");
        return tplPath;
    }


}
