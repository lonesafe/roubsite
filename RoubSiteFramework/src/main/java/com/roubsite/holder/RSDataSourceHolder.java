package com.roubsite.holder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

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
