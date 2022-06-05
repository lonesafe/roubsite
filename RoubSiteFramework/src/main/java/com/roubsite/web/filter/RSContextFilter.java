package com.roubsite.web.filter;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.roubsite.context.RSFilterContext;
import com.roubsite.database.RSConnection;
import com.roubsite.holder.ActionClassBean;
import com.roubsite.holder.RSFilterContextHolder;
import com.roubsite.utils.ClassBean;
import com.roubsite.utils.ClassUtils;
import com.roubsite.utils.ConfUtils;
import com.roubsite.utils.RequestURIFilter;
import com.roubsite.utils.StringUtils;
import com.roubsite.web.error.RSErrorPage;
import com.roubsite.web.filter.impl.RSSecurityInterface;
import com.roubsite.web.wrapper.RoubSiteRequestWrapper;
import com.roubsite.web.wrapper.RoubSiteResponseWrapper;

/**
 * 框架拦截器
 *
 * @author lones 王振骁
 */
public class RSContextFilter implements Filter {
    private final static Logger logger = LoggerFactory.getLogger(RSContextFilter.class);
    private final static String securityClassPath = ConfUtils.getStringConf("RoubSite.security.class", "");
    private static RequestURIFilter excludes;
    private final static String isInclude = "__IS__INCLUDE__";

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        RoubSiteRequestWrapper req = new RoubSiteRequestWrapper((HttpServletRequest) request);
        RoubSiteResponseWrapper resp = new RoubSiteResponseWrapper((HttpServletResponse) response);
        // 防注入拦截
        if (ConfUtils.getBooleanConf("RoubSite.injection") && !new RSParamCheck().check(req, resp)) {
            // 存在非法字符，直接返回，不执行后续操作
            return;
        }

        // 判断静态资源
        if (excludes.matches(req) || (null != request.getAttribute(isInclude) && (boolean) request.getAttribute(isInclude))) {
            chain.doFilter(req, resp);
            return;
        } else {
            // 非静态资源设置utf-8编码
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");
        }
        // ---------------------------------------非静态资源处理----------------------------------------------
        RSFilterContext rfc = RSFilterContextHolder.initRequestContext();
        rfc.setRequest(req);
        rfc.setResponse(resp);
        // 获取url
        String servletPath = req.getServletPath();
        try {
            // 解析servletPath并转换成类和方法
            ClassBean cb = new ClassUtils().getClassBean(servletPath);
            if (StringUtils.isNotEmpty(cb.getErroMessage())) {
                // 获取ClassBean失败
                logger.error(cb.getErroMessage());
//				new RSErrorPage(resp, req, 404, null, "未找到该url对应的action")
//						.die(new RuntimeException("错误的url:" + servletPath));
                chain.doFilter(request, response);
                return;
            } else {
                // 反射实例化action类
                ActionClassBean acb = new ActionClassBean(cb.getClassPath(), cb.getMethod(), resp, req);
                rfc.setActionClassBean(acb);
                rfc.setClassBean(cb);
                // 权限检查
                if (StringUtils.isNotEmpty(securityClassPath)) {
                    RSSecurityInterface serurityCheck = (RSSecurityInterface) Class.forName(securityClassPath).getDeclaredConstructor().newInstance();
                    if (!serurityCheck.isPermitted(request, response, acb)) {
                        // 权限检查失败，直接返回
                        return;
                    }
                }
                // 通过权限检查后实例化action
                if (acb.newAction()) {
                    // 向action中注入dao
                    new RSInjectDatabase(rfc).doInject();
                    // 执行action方法
                    new RSInvokeAction(cb, acb, req, resp).invokeActon();
                }
            }
        } catch (Exception e) {
            logger.error("action错误:" + servletPath, e);
            new RSErrorPage(resp, req, 500, null, "系统错误，请稍后再试！").die(e);
        } finally {
            closeConn(RSFilterContextHolder.getRSFilterContext().getDbConnList(), !RSFilterContextHolder.getRSFilterContext().isTrans());
            RSFilterContextHolder.destoryRSFilterContext();
        }
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        String excludes = fConfig.getInitParameter("excludes");
        RSContextFilter.excludes = new RequestURIFilter(excludes);
    }

    private void closeConn(Map<String, RSConnection> daoList, boolean isAutoCommit) {
        try {
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
        } catch (Exception e) {
            logger.error("关闭数据库连接报错", e);
        }

    }
}
