package com.roubsite.web.listener;

import com.roubsite.holder.RSDataSourceHolder;
import com.roubsite.utils.ConfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class RSConfigListenerContext implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(RSConfigListenerContext.class);

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // tomcat结束时执行
        RSDataSourceHolder.getInstance().get().closeAll();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                log.error("contentDestroy", e);
            }
        }
        try {
            Class<?> clazz = null;
            try {
                clazz = Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread");
                Method method = clazz.getMethod("checkedShutdown");
                method.invoke(null);
            } catch (Exception e) {
            }
            try {
                clazz = Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread");
                Method method = clazz.getMethod("checkedShutdown");
                method.invoke(null);
            } catch (Exception e) {
            }


        } catch (Exception e) {
            log.error("contentDestroy", e);
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        // tomcat启动时执行
        System.out.println("------------------------------------------------\n" +
                "------------------------------------------------\n" +
                "---------------RoubSite框架初始化配置--------------\n" +
                "---------官网https://www.roubsite.com -----------\n" +
                "-----------------------V4-----------------------\n" +
                "-------------------优雅源自从容-------------------\n" +
                "------------------------------------------------\n\n");
        ServletContext sc = arg0.getServletContext();
        if (ConfUtils.getBooleanConf("RoubSite.DataSourcePool.console")) {
            log.info("注册druid web监控中心");
            FilterRegistration druidFilter = sc.addFilter("DruidStatViewFilter", "com.alibaba.druid.support.http.WebStatFilter");
            druidFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE), true, "/*");
            druidFilter.setInitParameter("sessionStatEnable", "true");
            druidFilter.setInitParameter("profileEnable", "true");
            druidFilter.setInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");

            ServletRegistration druidServlet = sc.addServlet("DruidStatView", "com.alibaba.druid.support.http.StatViewServlet");
            druidServlet.addMapping("/druid/*");
            druidServlet.setInitParameter("sessionStatEnable", "true");
            druidServlet.setInitParameter("profileEnable", "true");
            log.info("注册druid web监控中心完成");
        }
        // 保证关闭contextHolder的拦截器
        FilterRegistration rSContextFilter = sc.addFilter("RScontextFilter", "com.roubsite.web.filter.RSContextFilter");
        Map<String, String> param = new HashMap<>();
        param.put("excludes", ConfUtils.getStringConf("RoubSite.global.static_suffix", ""));
        rSContextFilter.setInitParameters(param);
        rSContextFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE), true, "/*");

        // 初始化数据源
        RSDataSourceHolder.getInstance();
    }
}
