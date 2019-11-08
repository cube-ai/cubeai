package com.wyy.domain;


public enum ModelType {
    Classification("分类"), //
    DataSources("数据源"), //
    DataTransformer("数据变换"), //
    Prediction("预测"), //
    Regression("回归");

	private String typeName;

	private ModelType(final String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}

}
