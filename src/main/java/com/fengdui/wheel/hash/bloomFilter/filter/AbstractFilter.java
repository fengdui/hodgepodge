package com.fengdui.wheel.hash.bloomFilter.filter;

import com.xh.market.framework.tool.bloomFilter.bitMap.BitMap;
import com.xh.market.framework.tool.bloomFilter.bitMap.IntMap;
import com.xh.market.framework.tool.bloomFilter.bitMap.LongMap;

/**
 * @author Wander.Zeng
 * @data 2015-9-29 下午10:50:03
 * @desc AbstractFilter.java <br>
 *       默认Bloom过滤器，使用Java的Hash算法
 */
public abstract class AbstractFilter implements Filter {

	private BitMap bm = null;
	protected long size = 0;

	public AbstractFilter(long maxValue) {
		this(maxValue, BitMap.MACHINE32);
	}

	public AbstractFilter(long maxValue, int machineNum) {
		this.size = maxValue;

		switch (machineNum) {
		case BitMap.MACHINE32:
			bm = new IntMap((int) (size / machineNum));
			break;
		case BitMap.MACHINE64:
			bm = new LongMap((int) (size / machineNum));
			break;
		default:
			throw new RuntimeException("Error Machine number!");
		}
	}

	@Override
	public boolean contains(String str) {
		return bm.contains(hash(str));
	}

	@Override
	public void add(String str) {
		bm.add(hash(str));
	}

	@Override
	public boolean containsAndAdd(String str) {
		final long hash = this.hash(str);
		if (bm.contains(hash)) {
			return true;
		}

		bm.add(hash);
		return false;
	}

	@Override
	public abstract long hash(String str);

}
