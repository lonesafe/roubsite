package com.roubsite.web.filter;

import com.roubsite.database.RSConnection;
import com.roubsite.database.annotation.Dao;
import com.roubsite.database.annotation.Trans;
import com.roubsite.database.dao.RSDaoFactory;
import com.roubsite.holder.ActionClassBean;
import com.roubsite.holder.RSDataSourceHolder;
import com.roubsite.holder.RSFilterContextHolder;
import com.roubsite.utils.ClassUtils;
import com.roubsite.utils.RequestURIFilter;
import com.roubsite.utils.StringUtils;
import com.roubsite.web.classBean.ClassBean;
import com.roubsite.web.error.RSErrorPage;
import com.roubsite.web.wrapper.RoubSiteRequestWrapper;
import com.roubsite.web.wrapper.RoubSiteResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

public class RoubSiteInjectDatabaseFilter implements Filter {
    public final static Logger logger = LoggerFactory.getLogger(RoubSiteInjectDatabaseFilter.class);
    private static RequestURIFilter excludes;
    private final static String isInclude = "__IS__INCLUDE__";

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        RoubSiteRequestWrapper req = (RoubSiteRequestWrapper) request;
        RoubSiteResponseWrapper resp = (RoubSiteResponseWrapper) response;
        req.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String servletPath = req.getServletPath();
        boolean trans = false;
        Map<String, RSConnection> connList = new HashMap<>();
        try {
            // 判断静态资源
            if (excludes.matches(req)
                    || (null != request.getAttribute(isInclude) && (boolean) request.getAttribute(isInclude))) {
                chain.doFilter(req, resp);
                return;
            } else {
                // 非静态资源转发
                Object action = null;
                Class<?> clazz = null;
                ClassBean cb = new ClassUtils().getClassBean(servletPath);
                if (StringUtils.isNotEmpty(cb.getErroMessage())) {
                    // 获取ClassBean失败
                    logger.error(cb.getErroMessage());
                    chain.doFilter(request, resp);
                    return;
                } else {

                    // 反射实例化获取action类
                    ActionClassBean acb = new ActionClassBean(cb.getClassPath(), cb.getMethod());
                    RSFilterContextHolder.setLocalRequestContext(acb, cb);
                    clazz = acb.getClazz();// 获取action的class
                    action = acb.getActionObject();// 获取action
                    Method method = acb.getMethod();
                    // 获取方法所有注解
                    trans = method.isAnnotationPresent(Trans.class);
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
                            if (connList.containsKey(daoAnn.dataSource())) {
                                conn = connList.get(daoAnn.dataSource());
                            } else {
                                conn = new RSConnection(RSDataSourceHolder.getInstance().get()
                                        .getDataSource(daoAnn.dataSource()).getConnection());
                                connList.put(daoAnn.dataSource(), conn);
                                conn.getConn().setAutoCommit(!trans);
                            }
                            Object curd = new RSDaoFactory().getDao(daoAnn.impl(), daoAnn.dataSource(), conn);
                            f.set(action, curd);
                        }
                    }
                }
                connAutoCommit(connList, !trans);
                chain.doFilter(request, resp);
            }
        } catch (Exception e) {
            logger.error("action错误:" + servletPath, e);
            new RSErrorPage(resp, req, 500, null, "系统错误，请稍后再试！").die(e);
        } finally {
            closeConn(connList, !trans);
        }
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        String excludes = fConfig.getInitParameter("excludes");
        RoubSiteInjectDatabaseFilter.excludes = new RequestURIFilter(excludes);
    }

    private void closeConn(Map<String, RSConnection> daoList, boolean isAutoCommit) {
        for (Entry<String, RSConnection> entry : daoList.entrySet()) {
            RSConnection conn = entry.getValue();
            logger.debug("释放连接池");
            if (!isAutoCommit) {
                try {
                    if (conn.isError()) {
                        conn.getConn().rollback();
                    } else {
                        conn.getConn().commit();
                    }
                } catch (Exception e) {
                    logger.error("提交事务出错", e);
                }
            }

            try {
                conn.getConn().close();
            } catch (Exception e) {
                logger.error("关闭数据库连接出错", e);
            }

        }
    }

    private void connAutoCommit(Map<String, RSConnection> daoList, boolean isAutoCommit) throws Exception {
        for (Entry<String, RSConnection> entry : daoList.entrySet()) {
            entry.getValue().getConn().setAutoCommit(isAutoCommit);
        }
    }
}
