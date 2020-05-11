package com.fengdui.wheel.hash.bloomFilter.filter;

/**
 * @author Wander.Zeng
 * @data 2015-9-29 下午10:58:19
 * @desc HfFilter.java
 */
public class HfFilter extends AbstractFilter {

	public HfFilter(long maxValue) {
		super(maxValue);
	}

	public HfFilter(long maxValue, int machineNum) {
		super(maxValue, machineNum);
	}

	@Override
	public long hash(String str) {
		int length = str.length();
		long hash = 0;

		for (int i = 0; i < length; i++)
			hash += str.charAt(i) * 3 * i;

		if (hash < 0)
			hash = -hash;

		return hash % size;
	}

}
