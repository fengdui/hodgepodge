package com.fengdui.wheel.hash.bloomFilter.filter;

import com.xh.market.framework.tool.Hashs;

/**
 * @author Wander.Zeng
 * @data 2015-9-29 下午10:52:59
 * @desc DefaultFilter.java <br>
 *       默认Bloom过滤器，使用Java自带的Hash算法
 */
public class DefaultFilter extends AbstractFilter {

	public DefaultFilter(long maxValue) {
		super(maxValue);
	}

	public DefaultFilter(long maxValue, int MACHINENUM) {
		super(maxValue, MACHINENUM);
	}

	@Override
	public long hash(String str) {
		return Hashs.javaDefaultHash(str) % size;
	}

}
