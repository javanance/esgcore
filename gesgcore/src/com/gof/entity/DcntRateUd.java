package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.abstracts.BaseEntity;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name ="USER_DCNT_RATE")
@Getter
@Setter
public class DcntRateUd extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -4252300668894647002L;
	
	private String baseYymm;
	private String applBizDv;
	@Id	private String irCurveId;
	@Id	private String matCd;
	
	private Double rfRate;
	private Double liqPrem;
	private Double riskAdjRfRate;
	private Double riskAdjRfFwdRate;
	
	public DcntRateUd() {}

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
		
		builder.append(baseYymm).append(delimeter)
			   .append("I").append(delimeter)
			   .append(irCurveId).append(delimeter)
			   .append(matCd).append(delimeter)
			   
			   .append(rfRate).append(delimeter)
			   .append(liqPrem).append(delimeter)
			   
			   .append(riskAdjRfRate).append(delimeter)
			   .append(riskAdjRfFwdRate).append(delimeter)
			   
			   .append(0.0)
			   ;
		
		return builder.toString();
	}
	public DcntRateBiz convertToBizDiscountRate() {
		DcntRateBiz rst = new DcntRateBiz();
		rst.setBaseYymm(baseYymm);
		rst.setApplBizDv(applBizDv);
		rst.setIrCurveNm(irCurveId);
		rst.setMatCd(matCd);
		rst.setRfRate(rfRate);
		rst.setLiqPrem(liqPrem);
//		rst.setRefYield(0.0);
//		rst.setCrdSpread(0.0);
		rst.setRiskAdjRfRate(riskAdjRfRate);
		rst.setRiskAdjRfFwdRate(riskAdjRfFwdRate);
		
		return rst;
	}
}


