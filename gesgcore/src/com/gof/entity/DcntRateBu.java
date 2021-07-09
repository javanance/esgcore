package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.gof.abstracts.BaseEntity;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name ="DCNT_RATE_BOTTOMUP")
@Getter
@Setter
public class DcntRateBu extends BaseEntity implements Serializable{

	private static final long serialVersionUID = -8105176349509184506L;

	private String baseYymm;
	private String irCurveNm;
	private String matCd;	
	
	@Transient
	private String sceNo;
	
	private Double rfRate;
	private String liqModelNm;
	private Double liqPrem;
	private Double riskAdjRfRate;
	private Double riskAdjRfFwdRate;
	
	public DcntRateBu() {}

//	@Override
	public Double getSpread() {
		return liqPrem;
	}
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
				.append(irCurveNm).append(delimeter)
				.append(matCd).append(delimeter)
				.append(rfRate).append(delimeter)
				.append(liqModelNm).append(delimeter)
				.append(liqPrem).append(delimeter)
				.append(riskAdjRfRate).append(delimeter)
				.append(riskAdjRfFwdRate).append(delimeter)
				;

		return builder.toString();

	}
//	@Transient
//	@Override
	public Double getIntRate() {
		return getRiskAdjRfRate();
	}

	public DcntRateBiz convertTo(String bizDv) {
		DcntRateBiz tempIr = new DcntRateBiz();
		
		tempIr.setBaseYymm(this.getBaseYymm());
		tempIr.setApplBizDv(bizDv);
		tempIr.setIrCurveNm(this.getIrCurveNm());
		tempIr.setMatCd(this.getMatCd());
		tempIr.setRfRate(this.getRfRate());
		tempIr.setLiqPrem(this.getLiqPrem());
		
		
		tempIr.setRiskAdjRfRate(this.getRiskAdjRfRate());
		tempIr.setRiskAdjRfFwdRate(this.getRiskAdjRfFwdRate());
		
		return tempIr;
	}

	
	public DcntSceBiz convertToSce(String bizDv) {
		DcntSceBiz tempIr = new DcntSceBiz();
		
		tempIr.setBaseYymm(this.getBaseYymm());
		tempIr.setApplBizDv(bizDv);
		tempIr.setIrCurveNm(this.getIrCurveNm());
		tempIr.setMatCd(this.getMatCd());
		tempIr.setSceNo(this.getSceNo());
//		tempIr.setRfRate(this.getRfRate());
//		tempIr.setLiqPrem(this.getLiqPrem());
		
//		tempIr.setRefYield(0.0);
//		tempIr.setCrdSpread(0.0);
		
		tempIr.setRiskAdjRfRate(this.getRiskAdjRfRate());
		tempIr.setRiskAdjRfFwdRate(this.getRiskAdjRfFwdRate());
		
		
		return tempIr;
	}

	
}


