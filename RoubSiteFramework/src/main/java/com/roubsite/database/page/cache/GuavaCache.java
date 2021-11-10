
package com.roubsite.database.page.cache;

import com.google.common.cache.CacheBuilder;

public class GuavaCache<K, V> implements Cache<K, V> {

	private final com.google.common.cache.Cache<K, V> CACHE;

	public GuavaCache(String prefix) {
		CacheBuilder cacheBuilder = CacheBuilder.newBuilder();
		cacheBuilder.maximumSize(1000);
		CACHE = cacheBuilder.build();
	}

	@Override
	public V get(K key) {
		return CACHE.getIfPresent(key);
	}

	@Override
	public void put(K key, V value) {
		CACHE.put(key, value);
	}
}
