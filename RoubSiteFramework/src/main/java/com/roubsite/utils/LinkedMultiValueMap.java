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
		this.targetMap = new LinkedHashMap();
	}

	public LinkedMultiValueMap(int initialCapacity) {
		this.targetMap = new LinkedHashMap(initialCapacity);
	}

	public LinkedMultiValueMap(Map<K, List<V>> otherMap) {
		this.targetMap = new LinkedHashMap(otherMap);
	}

	public void add(K key, V value) {
		List values = (List) this.targetMap.get(key);
		if (values == null) {
			values = new LinkedList();
			this.targetMap.put(key, values);
		}
		values.add(value);
	}

	public V getFirst(K key) {
		List values = (List) this.targetMap.get(key);
		if (values != null)
			return (V) values.get(0);
		else
			return null;
	}

	public void set(K key, V value) {
		List values = new LinkedList();
		values.add(value);
		this.targetMap.put(key, values);
	}

	public void setAll(Map<K, V> values) {
		for (Map.Entry entry : values.entrySet())
			set((K) entry.getKey(), (V) entry.getValue());
	}

	public Map<K, V> toSingleValueMap() {
		LinkedHashMap singleValueMap = new LinkedHashMap(this.targetMap.size());
		for (Map.Entry entry : this.targetMap.entrySet()) {
			singleValueMap.put(entry.getKey(), ((List) entry.getValue()).get(0));
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
		return (List) this.targetMap.get(key);
	}

	public List<V> put(K key, List<V> value) {
		return (List) this.targetMap.put(key, value);
	}

	public List<V> remove(Object key) {
		return (List) this.targetMap.remove(key);
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