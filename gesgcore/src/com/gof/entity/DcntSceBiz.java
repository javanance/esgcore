package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.abstracts.BaseEntity;
import com.gof.model.hw.IrModelSce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name ="BIZ_DCNT_SCE")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DcntSceBiz extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -4252300668894647002L;
//	@Id
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	private long id;
	
	private String baseYymm;
	@Column(name="APPL_BIZ_DV", nullable=false)
	private String applBizDv;
	private String irCurveNm;
	private String sceNo;
	private String matCd;
	
	private Double riskAdjRfRate;
	private Double riskAdjRfFwdRate;
	
	
//	public BizDiscountRateSce() {}

//	@Override
	public Double getIntRate() {
		return riskAdjRfRate ;
	}
	
//	@Override
//	public Double getSpread() {
//		return liqPrem;
//	}
	
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
			   .append(applBizDv).append(delimeter)
			   .append(irCurveNm).append(delimeter)
			   .append(sceNo).append(delimeter)
			   .append(matCd).append(delimeter)
			   
//			   .append(rfRate).append(delimeter)
//			   .append(liqPrem).append(delimeter)
			   
//			   .append(refYield).append(delimeter)
//			   .append(crdSpread).append(delimeter)
			   
			   .append(riskAdjRfRate).append(delimeter)
			   .append(riskAdjRfFwdRate).append(delimeter)
			   
//			   .append(0.0)
//			   .append(lastModifiedBy).append(delimeter)
//			   .append(lastUpdateDate)
			   ;
		
		return builder.toString();
	}
	
	public static DcntSceBiz convert(String bizDv, IrModelSce irSce) {
		DcntSceBiz rst = new DcntSceBiz();
		
		rst.setBaseYymm(irSce.getBaseDate());
		rst.setApplBizDv(bizDv);
		rst.setIrCurveNm(irSce.getIrCurveNm());
		rst.setSceNo(irSce.getSceNo());
		rst.setMatCd(irSce.getMatCd());
		
//		rst.setRfRate(irSce.getSpotRateDisc());
//		rst.setLiqPrem(0.0);
		
		return rst;
	}
}


