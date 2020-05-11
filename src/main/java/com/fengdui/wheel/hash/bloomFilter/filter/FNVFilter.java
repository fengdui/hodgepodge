package com.fengdui.wheel.hash.bloomFilter.filter;

import com.xh.market.framework.tool.Hashs;

/**
 * @author Wander.Zeng
 * @data 2015-9-29 下午10:57:15
 * @desc FNVFilter.java
 */
public class FNVFilter extends AbstractFilter {

	public FNVFilter(long maxValue) {
		super(maxValue);
	}

	public FNVFilter(long maxValue, int machineNum) {
		super(maxValue, machineNum);
	}

	@Override
	public long hash(String str) {
		return Hashs.fnvHash(str);
	}

}
