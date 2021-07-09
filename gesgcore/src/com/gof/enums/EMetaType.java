package com.gof.enums;

public enum EMetaType {
	  STRING		(1)
	, BOOLEAN		(2)
	, INT			(3)
	, DOUBLE		(4)
	, STRING_ARRAY	(5)
	, BOOLEAN_ARRAY	(6)
	, INT_ARRAY		(7)
	, DOUBLE_ARRAY	(8)
	
	;
	
	private int order;

	private EMetaType(int jobNo) {
		this.order = jobNo;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	

	
}
