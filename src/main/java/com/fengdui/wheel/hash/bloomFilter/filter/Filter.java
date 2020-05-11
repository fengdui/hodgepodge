package com.fengdui.wheel.hash.bloomFilter.filter;

/**
 * @author Wander.Zeng
 * @data 2015-9-29 下午10:49:34
 * @desc Filter.java
 */
public interface Filter {

	/** 判断一个字符串是否在bitMap中存在 */
	public boolean contains(String str);

	/** 在bitMap中增加一个字符串 */
	public void add(String str);

	/** 如果存在就返回true .如果不存在.先增加这个字符串.再返回false */
	public boolean containsAndAdd(String str);

	/** 自定义Hash方法 */
	public long hash(String str);

}
