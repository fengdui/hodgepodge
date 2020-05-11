package com.fengdui.wheel.excel;

public interface SQLFieldTransform {
	/**
	 * 转换为需要的显示格式
	 */
	public String transform(Object o);
}
