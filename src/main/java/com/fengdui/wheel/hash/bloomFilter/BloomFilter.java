package com.fengdui.wheel.hash.bloomFilter;

/**
 * @desc BloomFilter.java <br>
 *       BlommFilter 实现 <br>
 *       1.构建hash算法 <br>
 *       2.散列hash映射到数组的bit位置 <br>
 *       3.验证<br>
 *       此实现方式可以指定Hash算法
 */
public class BloomFilter {

	private Filter[] filters;

	/** 使用默认的5个过滤器 */
	public BloomFilter(int m) {
		float mNum = m / 5;
		long size = (long) (1L * mNum * 1024 * 1024 * 8);
		filters = new Filter[] { new DefaultFilter(size), new ELFFilter(size), new JSFilter(size), new PJWFilter(size), new SDBMFilter(size) };
	}

	/**
	 * 使用自定的多个过滤器建立BloomFilter<br>
	 * 
	 * @param m 决定BitMap的大小 <br>
	 * @param filters Bloom过滤器列表
	 */
	public BloomFilter(int m, Filter... filters) {
		this.filters = filters;
	}

	/** 增加字符串到Filter映射中 */
	public void add(String str) {
		for (Filter filter : filters) {
			filter.add(str);
		}
	}

	/** 是否可能包含此字符串，此处存在误判 */
	public boolean contains(String str) {
		for (Filter filter : filters) {
			if (!filter.contains(str)) {
				return false;
			}
		}
		return true;
	}

	/** 判定字符串是否存在于各Filter中，如果不存在加入此Filter中 */
	public boolean containsAndAdd(String str) {
		boolean flag = true;
		for (Filter filter : filters) {
			flag &= filter.containsAndAdd(str);
		}
		return flag;
	}

}
