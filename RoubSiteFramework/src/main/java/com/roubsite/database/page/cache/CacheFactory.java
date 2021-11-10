package com.roubsite.database.page.cache;

public abstract class CacheFactory {

	/**
	 * 创建 SQL 缓存
	 *
	 * @param sqlCacheClass
	 * @return
	 */
	public static <K, V> Cache<K, V> createCache(String prefix) {
		return new GuavaCache<K, V>(prefix);
	}
}
