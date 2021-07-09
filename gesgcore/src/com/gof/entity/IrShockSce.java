package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.gof.abstracts.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="IR_SHOCK_SCE")
@Getter
@Setter
public class IrShockSce extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 4458482460359847563L;

	private String baseDate;
	private String irModelId;
	private String matCd;
	private String sceNo;
	private String irCurveNm;	
	private Double rfRate;
	private Double riskAdjIr;
	
	public IrShockSce() {}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return toString(",");
	}
	
	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(baseDate).append(delimeter)
			   .append(irModelId).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(sceNo).append(delimeter)
			   .append(irCurveNm).append(delimeter)
			   .append(rfRate).append(delimeter)
			   .append(riskAdjIr).append(delimeter)
			   ;
		return builder.toString();
	}
}


