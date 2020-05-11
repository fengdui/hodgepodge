package com.fengdui.wheel.hash.bloomFilter.filter;

/**
 * @author Wander.Zeng
 * @data 2015-9-29 下午10:58:55
 * @desc HfIpFilter.java
 */
public class HfIpFilter extends AbstractFilter {

	public HfIpFilter(long maxValue) {
		super(maxValue);
	}

	public HfIpFilter(long maxValue, int machineNum) {
		super(maxValue, machineNum);
	}

	@Override
	public long hash(String str) {
		int length = str.length();
		long hash = 0;
		for (int i = 0; i < length; i++) {
			hash += str.charAt(i % 4) ^ str.charAt(i);
		}
		return hash % size;
	}

}
