package com.roubsite.holder;

import com.roubsite.database.RSDataSource;
import com.roubsite.utils.ConfUtils;
import com.roubsite.utils.StringUtils;

public class RSDataSourceHolder {
    private RSDataSource rds = null;

    private static class LazyHolder {
        private static final RSDataSourceHolder INSTANCE = new RSDataSourceHolder();
    }

    private RSDataSourceHolder() {
        rds = new RSDataSource();
        String defaultDataSource = ConfUtils.getConf(new String[]{"RoubSite", "DataSourcePool", "default"}, "");
        if (StringUtils.isNotEmpty(defaultDataSource)) {
            rds.registerDataSource(defaultDataSource);
        }
    }

    public static final RSDataSourceHolder getInstance() {
        return LazyHolder.INSTANCE;
    }

    public RSDataSource get() {
        return this.rds;
    }
}
