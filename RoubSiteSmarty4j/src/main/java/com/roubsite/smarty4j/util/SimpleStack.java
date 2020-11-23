package com.roubsite.smarty4j.util;

public class SimpleStack {
	
	private Object[] data = new Object[16];
	private int size;
	
	public int size() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public Object get(int index) {
		return data[index];
	}
	
	public void set(int index, Object o) {
		data[index] = o;
	}
	
	public void push(Object o) {
		if (size == data.length) {
			Object[] copy = new Object[size * 2];
			System.arraycopy(data, 0, copy, 0, size);
			data = copy;
		}
		data[size++] = o;
	}
	
	public Object pop() {
		return data[--size];
	}
}
