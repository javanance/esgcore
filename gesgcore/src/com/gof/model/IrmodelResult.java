package com.gof.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IrmodelResult implements Serializable {
	
	private static final long serialVersionUID = 3248965187822308189L;

	private String baseDate;
	private String resultType;
	private String scenType;
	private String matCd;
	
	private Double spotCont;
	private Double spotDisc;
	private Double fwdCont;
	private Double fwdDisc;
	
	public IrmodelResult() {}

	@Override
	public String toString() {
		return "IrmodelResult [baseDate=" + baseDate + ", resultType=" + resultType
				+ ", spotCont=" + spotCont + ", spotDisc=" + spotDisc + ", fwdCont=" + fwdCont
				+ ", fwdDisc=" + fwdDisc + "]";
	}
	
	
}
