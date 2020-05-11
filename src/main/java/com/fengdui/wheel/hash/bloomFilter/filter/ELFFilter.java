package com.fengdui.wheel.hash.bloomFilter.filter;

import com.xh.market.framework.tool.Hashs;

/**
 * @author Wander.Zeng
 * @data 2015-9-29 下午10:56:25
 * @desc ELFFilter.java
 */
public class ELFFilter extends AbstractFilter {

	public ELFFilter(long maxValue) {
		super(maxValue);
	}

	public ELFFilter(long maxValue, int MACHINENUM) {
		super(maxValue, MACHINENUM);
	}

	@Override
	public long hash(String str) {
		return Hashs.elfHash(str) % size;
	}

}
