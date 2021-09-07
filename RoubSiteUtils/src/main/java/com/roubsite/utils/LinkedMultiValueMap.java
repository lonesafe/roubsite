package com.roubsite.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LinkedMultiValueMap<K, V> implements MultiValueMap<K, V>, Serializable {
	private static final long serialVersionUID = 3801124242820219131L;
	private final Map<K, List<V>> targetMap;

	public LinkedMultiValueMap() {
		this.targetMap = new LinkedHashMap<K, List<V>>();
	}

	public LinkedMultiValueMap(int initialCapacity) {
		this.targetMap = new LinkedHashMap<K, List<V>>(initialCapacity);
	}

	public LinkedMultiValueMap(Map<K, List<V>> otherMap) {
		this.targetMap = new LinkedHashMap<K, List<V>>(otherMap);
	}

	public void add(K key, V value) {
		List<V> values = this.targetMap.get(key);
		if (values == null) {
			values = new LinkedList<V>();
			this.targetMap.put(key, values);
		}
		values.add(value);
	}

	public V getFirst(K key) {
		List<V> values = this.targetMap.get(key);
		if (values != null)
			return values.get(0);
		else
			return null;
	}

	public void set(K key, V value) {
		List<V> values = new LinkedList<V>();
		values.add(value);
		this.targetMap.put(key, values);
	}

	public void setAll(Map<K, V> values) {
		for (Map.Entry<K, V> entry : values.entrySet())
			set(entry.getKey(), entry.getValue());
	}

	public Map<K, V> toSingleValueMap() {
		LinkedHashMap<K, V> singleValueMap = new LinkedHashMap<K, V>(this.targetMap.size());
		for (Map.Entry<K, List<V>> entry : this.targetMap.entrySet()) {
			singleValueMap.put(entry.getKey(), entry.getValue().get(0));
		}
		return singleValueMap;
	}

	public int size() {
		return this.targetMap.size();
	}

	public boolean isEmpty() {
		return this.targetMap.isEmpty();
	}

	public boolean containsKey(Object key) {
		return this.targetMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return this.targetMap.containsValue(value);
	}

	public List<V> get(Object key) {
		return this.targetMap.get(key);
	}

	public List<V> put(K key, List<V> value) {
		return this.targetMap.put(key, value);
	}

	public List<V> remove(Object key) {
		return this.targetMap.remove(key);
	}

	public void putAll(Map<? extends K, ? extends List<V>> m) {
		this.targetMap.putAll(m);
	}

	public void clear() {
		this.targetMap.clear();
	}

	public Set<K> keySet() {
		return this.targetMap.keySet();
	}

	public Collection<List<V>> values() {
		return this.targetMap.values();
	}

	public Set<Map.Entry<K, List<V>>> entrySet() {
		return this.targetMap.entrySet();
	}

	public boolean equals(Object obj) {
		return this.targetMap.equals(obj);
	}

	public int hashCode() {
		return this.targetMap.hashCode();
	}

	public String toString() {
		return this.targetMap.toString();
	}
}