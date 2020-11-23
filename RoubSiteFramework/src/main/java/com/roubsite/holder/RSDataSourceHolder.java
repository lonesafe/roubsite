package com.roubsite.holder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.roubsite.database.RSDataSource;

public class RSDataSourceHolder {
	private RSDataSource rds = null;

	private static class LazyHolder {
		private static final RSDataSourceHolder INSTANCE = new RSDataSourceHolder();
	}

	private RSDataSourceHolder() {
		try {
			rds = new RSDataSource();
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("dataSource.properties");
			BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
			Properties pro = new Properties();
			pro.load(bf);
			Set<Entry<Object, Object>> ent = pro.entrySet();
			ent = pro.entrySet();
			for (Entry<Object, Object> e : ent) {
				String tmpString = e.getKey().toString();
				if (tmpString.indexOf("type") != -1) {
					rds.registerDataSource(tmpString.split("\\.")[0]);
				}
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static final RSDataSourceHolder getInstance() {
		return LazyHolder.INSTANCE;
	}

	public RSDataSource get() {
		return this.rds;
	}
}
