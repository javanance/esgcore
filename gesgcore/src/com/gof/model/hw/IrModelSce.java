package com.gof.model.hw;


import java.io.Serializable;

import com.gof.entity.DcntSceBiz;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IrModelSce implements Serializable {
	
	private static final long serialVersionUID = -5971256119173516419L;

	private String baseDate;
	private String irModelId;
	private String matCd;
	private String sceNo;
	private String irCurveNm;
	private Integer monthSeq;
	
	private Double spotRateDisc;
	private Double spotRateCont;
	private Double fwdRateDisc;
	private Double fwdRateCont;
	private Double dcntFactor;
	private double theta;
	
	
	public IrModelSce() {}
	
	public DcntSceBiz convert(String bssd, String bizDv) {
		DcntSceBiz rst = new DcntSceBiz();
		
		rst.setBaseYymm(bssd);
		rst.setApplBizDv(bizDv);
		rst.setIrCurveNm(this.irCurveNm);
		rst.setSceNo(this.sceNo);
		rst.setMatCd(this.matCd);
		
//		rst.setRfRate(this.spotRateDisc);
//		rst.setLiqPrem(0.0);
		
		rst.setRiskAdjRfRate(this.spotRateDisc);
		rst.setRiskAdjRfFwdRate(this.fwdRateDisc);
		

		return rst;
	}

}
