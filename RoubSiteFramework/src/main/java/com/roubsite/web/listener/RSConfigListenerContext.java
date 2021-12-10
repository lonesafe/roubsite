package com.roubsite.web.listener;

import com.roubsite.holder.RSDataSourceHolder;
import com.roubsite.utils.ConfUtils;
import com.roubsite.utils.StringUtils;
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
	private static Logger log = LoggerFactory.getLogger(RSConfigListenerContext.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// tomcat结束时执行
		RSDataSourceHolder.getInstance().get().closeAll();
		Enumeration drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = (Driver) drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
			} catch (SQLException e) {
			}
		}
		try {
			Class clazz = Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread");
			Method method = clazz.getMethod("checkedShutdown");
			method.invoke(null);
		} catch (Exception e) {
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// tomcat启动时执行
		System.out.println("------------------------------------------------");
		System.out.println("------------------------------------------------");
		System.out.println("---------------RoubSite框架初始化配置--------------");
		System.out.println("---------官网 http://www.roubsite.com -----------");
		System.out.println("-----------------------V4-----------------------");
		System.out.println("-------------------优雅源自从容-------------------");
		System.out.println("------------------------------------------------");
		System.out.println("");
		ServletContext sc = arg0.getServletContext();
		if (ConfUtils.getBooleanConf("RoubSite.DataSourcePool.console")) {
			log.info("注册druid web监控中心");
			FilterRegistration druidFilter = sc.addFilter("DruidStatViewFilter",
					"com.alibaba.druid.support.http.WebStatFilter");
			druidFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE), true,
					"/*");
			druidFilter.setInitParameter("sessionStatEnable", "true");
			druidFilter.setInitParameter("profileEnable", "true");
			druidFilter.setInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");

			ServletRegistration druidServlet = sc.addServlet("DruidStatView",
					"com.alibaba.druid.support.http.StatViewServlet");
			druidServlet.addMapping("/druid/*");
			druidServlet.setInitParameter("sessionStatEnable", "true");
			druidServlet.setInitParameter("profileEnable", "true");
			log.info("注册druid web监控中心完成");
		}
		// 保证关闭contextHolder的拦截器
		FilterRegistration rSContextFilter = sc.addFilter("RScontextFilter", "com.roubsite.web.filter.RSContextFilter");
		rSContextFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE), true,
				"/*");
		// 防注入拦截器
		if (ConfUtils.getBooleanConf("RoubSite.injection")) {
			log.info("加载RoubSite安全模块");
			FilterRegistration rSWebSectFilter = sc.addFilter("rSWebSectFilter",
					"com.roubsite.web.filter.RSWebSecurityFilter");
			rSWebSectFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE), true,
					"/*");
			log.info("RoubSite安全模块加载成功，正在保护您的代码免受sql注入和xss跨站攻击");
		}
		log.info("启动框架解析器");
		// 获取action并向dao中注入数据源
		FilterRegistration rSInitFilter = sc.addFilter("RSFilter",
				"com.roubsite.web.filter.RoubSiteInjectDatabaseFilter");
		Map<String, String> param = new HashMap<String, String>();
		param.put("excludes", ConfUtils.getStringConf("RoubSite.global.static_suffix", ""));
		rSInitFilter.setInitParameters(param);
		rSInitFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE), true, "/*");
		// 权限控制器
		if (StringUtils.isNotEmpty(ConfUtils.getStringConf("RoubSite.security.class", ""))) {
			log.info("初始化权限控制模块");
			FilterRegistration securityFilter = sc.addFilter("RSSecFilter",
					ConfUtils.getStringConf("RoubSite.security.class", ""));
			Map<String, String> securityFilterparam = new HashMap<String, String>();
			securityFilterparam.put("excludes", ConfUtils.getStringConf("RoubSite.security.missing", ""));
			securityFilter.setInitParameters(securityFilterparam);
			securityFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE), true,
					"/*");
			log.info("初始化权限控制模块完成");
		}

		// 反射执行action
		FilterRegistration rSMainFilter = sc.addFilter("RSMainFilter",
				"com.roubsite.web.filter.RoubSiteInvokeActionFilter");
		rSMainFilter.setInitParameters(param);
		rSMainFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE), true, "/*");

		// 初始化数据源
		RSDataSourceHolder.getInstance();
		System.out.println("-----------------RoubSite启动成功------------------");
	}
}
