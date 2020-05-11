package com.fengdui.wheel.hash.bloomFilter.filter;

import com.xh.market.framework.tool.Hashs;

/**
 * @author Wander.Zeng
 * @data 2015-9-29 下午11:00:13
 * @desc PJWFilter.java
 */
public class PJWFilter extends AbstractFilter {

	public PJWFilter(long maxValue) {
		super(maxValue);
	}

	public PJWFilter(long maxValue, int machineNum) {
		super(maxValue, machineNum);
	}

	@Override
	public long hash(String str) {
		return Hashs.pjwHash(str) % size;
	}

}
