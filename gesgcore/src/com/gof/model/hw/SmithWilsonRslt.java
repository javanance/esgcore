package com.gof.model.hw;

import java.io.Serializable;

import com.gof.entity.DcntRateBiz;
import com.gof.entity.DcntRateBu;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SmithWilsonRslt implements Serializable {
	
	private static final long serialVersionUID = 3248965187822308189L;

	private String baseDate;
	private String resultType;
	private String scenType;
	private String matCd;
	private Double matTerm;

	private Double dcntFactor;
	private Double spotCont;
	private Double spotDisc;
	private Double fwdCont;
	private Double fwdDisc;
	
	public SmithWilsonRslt() {}
	
	public DcntRateBu convertTo(String irCurveNm) {
		DcntRateBu temp = new DcntRateBu();
		temp.setBaseYymm(baseDate.substring(0,6));
		temp.setIrCurveNm(irCurveNm);
		temp.setMatCd(matCd);
		
		temp.setLiqPrem(0.0);
		temp.setRiskAdjRfRate(spotCont);
		temp.setRiskAdjRfFwdRate(fwdDisc);
		
		return temp;
		
	}
	
	public DcntRateBiz convertToBiz(String bssd, String irCurveNm, String bizDv) {
		DcntRateBiz temp = new DcntRateBiz();
		temp.setBaseYymm(bssd);
		temp.setApplBizDv(bizDv);
		temp.setIrCurveNm(irCurveNm);
		temp.setMatCd(matCd);
		
		
		temp.setLiqPrem(0.0);
		temp.setRiskAdjRfRate(spotCont);
		temp.setRiskAdjRfFwdRate(fwdDisc);
		
		return temp;
		
	}
	
}

