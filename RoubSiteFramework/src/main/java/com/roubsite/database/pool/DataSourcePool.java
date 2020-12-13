package com.roubsite.database.pool;

import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;
import com.roubsite.utils.ConfUtils;
import com.roubsite.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourcePool // implements DataSource
{
    private String url = null;
    private String username = null;
    private String password = null;
    private int size = 10;
    // private ConcurrentLinkedQueue<Connection> list = new
    // ConcurrentLinkedQueue<Connection>();
    DruidDataSource dataSource = new DruidDataSource();
    private static Logger log = LoggerFactory.getLogger(DataSourcePool.class);
    private String keepAlive;
    private long maxWaitMillis;
    private int maxWaitThreadCount;
    private int validationQueryTimeout;

    // 创建对象就初始化size个数据库连接
    public DataSourcePool(String dataSourceName) {
        url = ConfUtils.getConf(dataSourceName + ".url", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSourceName,"url"});
        username = ConfUtils.getConf(dataSourceName + ".username", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSourceName,"username"});
        password = ConfUtils.getConf(dataSourceName + ".password", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSourceName,"password"});
        if (StringUtils.isNotEmpty(ConfUtils.getConf(dataSourceName + ".poolSize", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSourceName,"poolSize"}))) {
            size = Integer.parseInt(ConfUtils.getConf(dataSourceName + ".poolSize", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSourceName,"poolSize"}));
        }

        if (StringUtils.isNotEmpty(ConfUtils.getConf(dataSourceName + ".keepAlive", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSourceName,"keepAlive"}))) {
            keepAlive = ConfUtils.getConf(dataSourceName + ".keepAlive", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSourceName,"keepAlive"});
        }

        if (StringUtils
                .isNotEmpty(ConfUtils.getConf(dataSourceName + ".maxWaitMillis", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSourceName,"maxWaitMillis"}))) {
            maxWaitMillis = Long
                    .parseLong(ConfUtils.getConf(dataSourceName + ".maxWaitMillis", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSourceName,"maxWaitMillis"}));
        }
        if (StringUtils.isNotEmpty(
                ConfUtils.getConf(dataSourceName + ".maxWaitThreadCount", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSourceName,"maxWaitThreadCount"}))) {
            maxWaitThreadCount = Integer.parseInt(
                    ConfUtils.getConf(dataSourceName + ".maxWaitThreadCount", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSourceName,"maxWaitThreadCount"}));
        }

        if (StringUtils.isNotEmpty(
                ConfUtils.getConf(dataSourceName + ".validationQueryTimeout", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSourceName,"validationQueryTimeout"}))) {
            validationQueryTimeout = Integer.parseInt(
                    ConfUtils.getConf(dataSourceName + ".validationQueryTimeout", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSourceName,"validationQueryTimeout"}));
        }

        dataSource.setDriverClassName(
                ConfUtils.getConf(dataSourceName + ".driverClassName", "dataSource.properties",new String[]{"RoubSite","DataSourcePool","dataSources",dataSourceName,"driverClassName"}));
        dataSource.setUsername(username);// 用户名
        dataSource.setPassword(password);// 密码
        dataSource.setUrl(url);// 链接字符串
        dataSource.setInitialSize(size);// 初始化连接数
        dataSource.setMinIdle(-1);
        dataSource.setMaxActive(size);// 最大连接数
        if ("true".equals(keepAlive)) {// 链接保持
            dataSource.setKeepAlive(true);
        } else {
            dataSource.setKeepAlive(false);
        }
        dataSource.setMaxWait(maxWaitMillis);// 最大等待时间
        dataSource.setMaxWaitThreadCount(maxWaitThreadCount);// 最大等待线程数，当超过这个数量的线程在等待时，新的请求将直接返回超时
        dataSource.setValidationQueryTimeout(validationQueryTimeout);
        try {
            dataSource.setFilters("stat");
        } catch (SQLException e) {
        }
        dataSource.setTestOnBorrow(true);
        dataSource.setPoolPreparedStatements(false);
        dataSource.setRemoveAbandoned(true);// 超过时间限制是否回收
        dataSource.setRemoveAbandonedTimeout(600);// 超时时间；单位为秒。180秒=3分钟
        dataSource.setLogAbandoned(false);// 关闭abanded连接时输出错误日志
        try {
            dataSource.init();
        } catch (SQLException e) {
            log.error("初始化数据源失败",e);
        }
    }

    // 创建对象就初始化size个数据库连接
    public DataSourcePool(String driverClassName, String url, String username, String password, int size,
                          boolean keepAlive, long maxWaitMillis, int maxWaitThreadCount) {
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(username);// 用户名
        dataSource.setPassword(password);// 密码
        dataSource.setUrl(url);// 链接字符串
        dataSource.setInitialSize(size);// 初始化连接数
        dataSource.setMinIdle(-1);
        dataSource.setMaxActive(size);// 最大连接数
        // 链接保持
        dataSource.setKeepAlive(keepAlive);
        dataSource.setMaxWait(maxWaitMillis);// 最大等待时间
        dataSource.setMaxWaitThreadCount(maxWaitThreadCount);// 最大等待线程数，当超过这个数量的线程在等待时，新的请求将直接返回超时
        try {
            dataSource.setFilters("stat");
        } catch (SQLException e) {
        }
        dataSource.setTestOnBorrow(true);
        dataSource.setPoolPreparedStatements(false);
        dataSource.setRemoveAbandoned(true);// 超过时间限制是否回收
        dataSource.setRemoveAbandonedTimeout(600);// 超时时间；单位为秒。180秒=3分钟
        dataSource.setLogAbandoned(false);// 关闭abanded连接时输出错误日志
        try {
            dataSource.init();
        } catch (SQLException e) {
            log.error("初始化数据源失败",e);
        }
    }

    /**
     * 请求一个新的connection
     */
    public DruidDataSource getDataSource() throws SQLException {
        return dataSource;
    }
}
