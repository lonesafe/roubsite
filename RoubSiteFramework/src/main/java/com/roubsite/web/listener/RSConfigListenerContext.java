package com.roubsite.web.listener;

import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;

import org.apache.log4j.Logger;

import com.roubsite.holder.RSDataSourceHolder;
import com.roubsite.utils.PropertiesUtil;
import com.roubsite.utils.StringUtils;

public class RSConfigListenerContext implements ServletContextListener {
	Logger log = Logger.getLogger(RSConfigListenerContext.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// tomcat结束时执行

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// tomcat启动时执行
		System.out.println("------------------------------------------------");
		System.out.println("------------------------------------------------");
		System.out.println("-------------------RS框架初始化配置----------------");
		System.out.println("------------官网 http://www.routsite.com---------");
		System.out.println("------------------------V3----------------------");
		System.out.println("------------------------------------------------");
		System.out.println("");
		ServletContext sc = arg0.getServletContext();
		if ("true".equals(PropertiesUtil.getConfigString("DataSourcePool.console"))) {
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
		if ("true".equals(PropertiesUtil.getString("injection", "config.properties"))) {
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
		param.put("excludes", PropertiesUtil.getConfigString("global.static_suffix"));
		rSInitFilter.setInitParameters(param);
		rSInitFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE), true, "/*");
		// 权限控制器
		if (new File(this.getClass().getClassLoader().getResource("").getPath() + "security.properties").exists()) {
			if (StringUtils.isNotEmpty(PropertiesUtil.getString("security.class", "security.properties"))) {
				log.info("初始化权限控制模块");
				FilterRegistration securityFilter = sc.addFilter("RSSecFilter",
						PropertiesUtil.getString("security.class", "security.properties"));
				Map<String, String> securityFilterparam = new HashMap<String, String>();
				securityFilterparam.put("excludes",
						PropertiesUtil.getString("security.missing", "security.properties"));
				securityFilter.setInitParameters(securityFilterparam);
				securityFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE),
						true, "/*");
				log.info("初始化权限控制模块完成");
			}
		}

		// 反射执行action
		FilterRegistration rSMainFilter = sc.addFilter("RSMainFilter",
				"com.roubsite.web.filter.RoubSiteInvokeActionFilter");
		rSMainFilter.setInitParameters(param);
		rSMainFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE), true, "/*");

		// 初始化数据源
		RSDataSourceHolder.getInstance();
		log.info("启动框架解析器完成");
	}

	/**
	 * 防止反编译
	 * 
	 * @author lones
	 *
	 */
	private class Invalid {
		public Invalid() {
		}
	}
}