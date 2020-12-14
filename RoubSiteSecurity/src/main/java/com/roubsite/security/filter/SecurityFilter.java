package com.roubsite.security.filter;

import com.roubsite.holder.ActionClassBean;
import com.roubsite.holder.RSFilterContextHolder;
import com.roubsite.security.annotation.RSSecRetType;
import com.roubsite.security.annotation.RSSecurity;
import com.roubsite.security.securityInfo.RSSecurityBaseUserInfo;
import com.roubsite.security.utils.RSSecurityUtils;
import com.roubsite.utils.*;
import com.roubsite.web.error.RSErrorPage;
import com.roubsite.web.error.RSFrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rick Jone 王振骁
 */
public class SecurityFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(SecurityFilter.class);
    private FilterConfig filterConfig;
    private RequestURIFilter excludes;
    private String isInclude = "__IS__INCLUDE__";

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        req.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String url = ((HttpServletRequest) request).getServletPath();
        Map<Object, Object> errorMap = new HashMap<>();
        errorMap.put("status", "-401");
        errorMap.put("message", "未登录");
        String errorJson = JsonUtils.convertToString(errorMap);
        // 判断静态资源
        if (this.excludes.matches(req)
                || (null != request.getAttribute(isInclude) && (boolean) request.getAttribute(isInclude))) {
            chain.doFilter(request, response);
            return;
        }
        if (!StringUtils.isNotEmptyObject(RSFilterContextHolder.getRSFilterContext())) {
            chain.doFilter(request, response);
            return;
        }
        ClassBean cb = RSFilterContextHolder.getRSFilterContext().getClassBean();
        if (StringUtils.isNotEmpty(cb.getErroMessage())) {
            // 获取ClassBean失败
            chain.doFilter(request, response);
            return;
        }
        ActionClassBean acb = RSFilterContextHolder.getRSFilterContext().getActionClassBean();
        Method method = acb.getMethod();
        // 非静态资源转发
        // 获取方法所有注解
        RSSecurity rsSecurity = method.getAnnotation(RSSecurity.class);
        if (null == rsSecurity) {
            logger.debug("不需要检查权限");
            chain.doFilter(request, response);
            return;
        } else {
            HttpSession session = req.getSession();
            String[] roleIds = rsSecurity.role();
            String redirectUrl = rsSecurity.redirectUrl();
            RSSecRetType retType = rsSecurity.retType();
            if (retType == RSSecRetType.retJson) {
                resp.setContentType("application/json");
            }
            if (StringUtils.isEmpty(redirectUrl)) {
                redirectUrl = ConfUtils.getConf("security.login.url", "security.properties",new String[]{"RoubSite", "security", "loginUrl"});
            }
            RSSecurityBaseUserInfo rsSecUserInfo = (RSSecurityBaseUserInfo) session.getAttribute("rsSecUserInfo");
            logger.debug("检查权限");
            // 检查是否登陆
            if (RSSecurityUtils.checkUserLogin(session)) {
                if (StringUtils.isEmptyArr(roleIds)) {
                    logger.debug("未配置权限id，根据url进行判断");
                    logger.debug("当前访问url:" + url);
                    if (rsSecUserInfo.getUrls().contains(url)) {
                        chain.doFilter(request, response);
                        return;
                    } else {
                        String message = "权限不足";
                        logger.error(message, new RSFrameworkException(403, message, url));
                        PrintWriter out = resp.getWriter();
                        if (retType == RSSecRetType.retJson) {
                            errorMap.put("message", "权限不足");
                            errorJson = JsonUtils.convertToString(errorMap);
                            out.print(errorJson);
                            return;
                        }
                        RSFrameworkException e = new RSFrameworkException(403, message, url);
                        new RSErrorPage(resp, req, 403, null, message).die(e);
                        return;
                    }
                }
                // 检查用户是否拥有该权限
                if (RSSecurityUtils.checkUserRoles(rsSecUserInfo, roleIds)) {
                    chain.doFilter(request, response);
                    return;
                } else {
                    PrintWriter out = resp.getWriter();
                    String message = "权限不足";
                    logger.error(message, new RSFrameworkException(403, message, url));
                    if (retType == RSSecRetType.retJson) {
                        errorMap.put("message", "权限不足");
                        errorJson = JsonUtils.convertToString(errorMap);
                        out.print(errorJson);
                        return;
                    }
                    RSFrameworkException e = new RSFrameworkException(403, message, url);
                    new RSErrorPage(resp, req, 403, null, message).die(e);
                    return;
                }
            } else {
                PrintWriter out = resp.getWriter();
                String message = "用户没有登陆或登陆失效";
                logger.error(message, new RSFrameworkException(401, message, url));
                if (retType == RSSecRetType.retJson) {
                    out.print(errorJson);
                    return;
                }
                String webPath = req.getContextPath();
                if (StringUtils.isNotEmpty(webPath)) {
                    String retUrl = req.getRequestURL().toString();
                    if (StringUtils.isEmpty(retUrl)) {
                        retUrl = "";
                    }
                    req.getSession().setAttribute("retUrl", retUrl);
                    if (webPath.endsWith("/")) {
                        resp.sendRedirect(webPath + redirectUrl);
                    } else {
                        resp.sendRedirect(webPath + "/" + redirectUrl);
                    }
                } else {
                    if (redirectUrl.startsWith("/")) {
                        resp.sendRedirect(redirectUrl);
                    } else {
                        if (redirectUrl.startsWith("http://")) {
                            resp.sendRedirect(redirectUrl);
                        } else {
                            resp.sendRedirect("/" + redirectUrl);
                        }
                    }
                }
                return;
            }
        }
    }

    public void init(FilterConfig fConfig) throws ServletException {
        this.filterConfig = fConfig;
        String excludes = this.filterConfig.getInitParameter("excludes");
        this.excludes = new RequestURIFilter(excludes);
    }

}
