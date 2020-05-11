package com.fengdui.wheel.hash.bloomFilter.filter;

/**
 * @author Wander.Zeng
 * @data 2015-9-29 下午10:59:34
 * @desc JSFilter.java
 */
public class JSFilter extends AbstractFilter {

	public JSFilter(long maxValue) {
		super(maxValue);
	}

	public JSFilter(long maxValue, int machineNum) {
		super(maxValue, machineNum);
	}

	@Override
	public long hash(String str) {
		int hash = 1315423911;

		for (int i = 0; i < str.length(); i++) {
			hash ^= ((hash << 5) + str.charAt(i) + (hash >> 2));
		}

		if (hash < 0)
			hash *= -1;

		return hash % size;
	}

}
