package com.roubsite.web.filter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.roubsite.context.RSFilterContext;
import com.roubsite.database.RSConnection;
import com.roubsite.database.RSDataSource;
import com.roubsite.database.annotation.Dao;
import com.roubsite.database.annotation.Trans;
import com.roubsite.database.dao.RSDaoFactory;
import com.roubsite.holder.RSDataSourceHolder;

/**
 * 数据库注入
 *
 * @author lones 王振骁
 */
public class RSInjectDatabase {
    public final static Logger logger = LoggerFactory.getLogger(RSInjectDatabase.class);
    RSFilterContext rfc;

    public RSInjectDatabase(RSFilterContext rfc) {
        this.rfc = rfc;
    }

    public void doInject() throws SQLException, IllegalAccessException {
        try {
            Object action = rfc.getActionClassBean().getActionObject();// 获取action
            Class<?> clazz = rfc.getActionClassBean().getClazz();// 获取action的class
            Method method = rfc.getActionClassBean().getMethod();
            // 设置开启数据库事务控制
            rfc.setTrans(method.isAnnotationPresent(Trans.class));
            List<Field> fieldList = new ArrayList<>();
            Class<?> tempClass = clazz;
            while (tempClass != null) {// 当父类为null的时候说明到达了最上层的父类(Object类).
                fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
                tempClass = tempClass.getSuperclass(); // 得到父类,然后赋给自己
            }
            // 获取action类中所有dao变量，实例化dao，给dao注入数据源
            for (Field f : fieldList) {
                // 设置action类中的成员变量为可读写
                f.setAccessible(true);
                // 判断该成员变量是否使用了Dao注解，如果使用了Dao注解则为Dao的接口变量
                Dao daoAnn = f.getAnnotation(Dao.class);
                if (daoAnn != null) {
                    RSConnection conn;
                    if (rfc.getDbConnList().containsKey(daoAnn.dataSource())) {
                        conn = rfc.getDbConnList().get(daoAnn.dataSource());
                    } else {
                        RSDataSource rsDataSource = RSDataSourceHolder.getInstance().get();
                        conn = new RSConnection(rsDataSource.getDataSource(daoAnn.dataSource()).getConnection(),
                                rsDataSource.getPageHelperMap(daoAnn.dataSource()));
                        rfc.addDbConnList(daoAnn.dataSource(), conn);
                        conn.getConn().setAutoCommit(!rfc.isTrans());
                    }
                    Object curd = new RSDaoFactory().getDao(daoAnn.impl(), daoAnn.dataSource(), conn);
                    f.set(action, curd);
                }
            }
        } catch (Exception e) {
            logger.error("向action中注入dao实例错误:", e);
            throw e;
        }

    }
}
