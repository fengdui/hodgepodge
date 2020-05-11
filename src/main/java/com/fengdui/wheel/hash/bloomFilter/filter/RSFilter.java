package com.fengdui.wheel.hash.bloomFilter.filter;

import com.xh.market.framework.tool.Hashs;

/**
 * @author Wander.Zeng
 * @data 2015-9-29 下午11:00:45
 * @desc RSFilter.java
 */
public class RSFilter extends AbstractFilter {

	public RSFilter(long maxValue) {
		super(maxValue);
	}

	public RSFilter(long maxValue, int machineNum) {
		super(maxValue, machineNum);
	}

	@Override
	public long hash(String str) {
		return Hashs.rsHash(str) % size;
	}

}
