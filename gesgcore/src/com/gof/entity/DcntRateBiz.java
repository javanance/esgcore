package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.gof.abstracts.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name ="BIZ_DCNT_RATE")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DcntRateBiz extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -4252300668894647002L;
	
	private String baseYymm;
	private String applBizDv;
	private String irCurveNm;
	private String matCd;
	
	private Double rfRate;
	private Double rfFwdRate;
	private Double liqPrem;
	
	private Double riskAdjRfRate;
	private Double riskAdjRfFwdRate;
	
//	public BizDiscountRate() {}

//	@Override
	public Double getIntRate() {
		return riskAdjRfRate ;
	}
	
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
			   .append("I").append(delimeter)
			   .append(irCurveNm).append(delimeter)
			   .append(matCd).append(delimeter)
			   
			   
			   .append(rfRate).append(delimeter)
			   .append(liqPrem).append(delimeter)
			   
			   .append(riskAdjRfRate).append(delimeter)
			   .append(riskAdjRfFwdRate).append(delimeter)
			   
//			   .append(lastModifiedBy).append(delimeter)
//			   .append(lastUpdateDate)
			   ;
		
		return builder.toString();
	}

	public IrCurveHis convert() {
		IrCurveHis rst = new IrCurveHis();

		rst.setBaseDate(this.baseYymm);
		rst.setIrCurveNm(this.irCurveNm);
		rst.setMatCd(this.matCd);
		rst.setIntRate(this.getRiskAdjRfRate());
		
		return rst;
	}
	
	
}


