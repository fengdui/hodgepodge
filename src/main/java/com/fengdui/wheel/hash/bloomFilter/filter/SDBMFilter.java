package com.fengdui.wheel.hash.bloomFilter.filter;

import com.xh.market.framework.tool.Hashs;

/**
 * @author Wander.Zeng
 * @data 2015-9-29 下午11:01:17
 * @desc SDBMFilter.java
 */
public class SDBMFilter extends AbstractFilter {

	public SDBMFilter(long maxValue) {
		super(maxValue);
	}

	public SDBMFilter(long maxValue, int machineNum) {
		super(maxValue, machineNum);
	}

	@Override
	public long hash(String str) {
		return Hashs.sdbmHash(str) % size;
	}

}
