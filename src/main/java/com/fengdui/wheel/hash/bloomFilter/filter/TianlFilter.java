package com.fengdui.wheel.hash.bloomFilter.filter;

import com.xh.market.framework.tool.Hashs;

/**
 * @author Wander.Zeng
 * @data 2015-9-29 下午11:01:44
 * @desc TianlFilter.java
 */
public class TianlFilter extends AbstractFilter {

	public TianlFilter(long maxValue) {
		super(maxValue);
	}

	public TianlFilter(long maxValue, int machineNum) {
		super(maxValue, machineNum);
	}

	@Override
	public long hash(String str) {
		return Hashs.tianlHash(str) % size;
	}

}
