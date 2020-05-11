package com.fengdui.wheel.excel;

public class SQLField {
	// 字段描述
	private String discription;
	// 字段
	private String fieldName;
	// 转换器
	private SQLFieldTransform transformer;

	/**
	 * @param fieldName
	 *            字段名称
	 * @param discription
	 *            描述
	 */
	public SQLField(String fieldName, String discription) {
		super();
		this.discription = discription;
		this.fieldName = fieldName;
	}

	/**
	 * @param fieldName
	 *            字段名称
	 * @param discription
	 *            描述
	 * @param transformer
	 *            转化器
	 */
	public SQLField(String fieldName, String discription,
			SQLFieldTransform transformer) {
		super();
		this.discription = discription;
		this.fieldName = fieldName;
		this.transformer = transformer;
	}

	public String getDiscription() {
		return discription;
	}

	public void setDiscription(String discription) {
		this.discription = discription;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public SQLFieldTransform getTransformer() {
		return transformer;
	}

	public void setTransformer(SQLFieldTransform transformer) {
		this.transformer = transformer;
	}
}
