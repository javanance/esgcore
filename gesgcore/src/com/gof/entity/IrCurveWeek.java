package com.gof.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.gof.abstracts.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="IR_CURVE_WEEK")
@Getter
@Setter
public class IrCurveWeek extends BaseEntity {
	
	private String baseDate; 
	private String irCurveNm;
	private String matCd;
	private Double intRate;
	
	private String dayOfWeek;
//	private IrCurve irCurve;
	
	public IrCurveWeek() {
	
	}
	public IrCurveWeek(String baseDate, String matCd, Double intRate) {
		this.baseDate = baseDate;
		this.matCd = matCd;
		this.intRate = intRate;
	}
	
	public IrCurveWeek(String bssd, IrCurveWeek curveHis) {
		this.baseDate = curveHis.getBaseDate();
		this.irCurveNm = curveHis.getIrCurveNm();
		this.matCd = curveHis.getMatCd();
		this.intRate = curveHis.getIntRate();
				
	}
	
//	@ManyToOne
//	@JoinColumn(name ="IR_CURVE_ID", insertable=false, updatable= false)
//	public IrCurve getIrCurve() {
//		return irCurve;
//	}
//	public void setIrCurve(IrCurve irCurve) {
//		this.irCurve = irCurve;
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
		
		builder.append(baseDate).append(delimeter)
			   .append(irCurveNm).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(intRate).append(delimeter)
			   ;

		return builder.toString();
	}
//******************************************************Biz Method**************************************
	
	public IrCurveHis convertToHis() {
		IrCurveHis rst = new IrCurveHis();
		
		rst.setBaseDate(this.baseDate);
		rst.setIrCurveNm(this.irCurveNm);
		rst.setMatCd(this.matCd);
		rst.setIntRate(this.intRate);
//		rst.setIrCurve(this.irCurve);
		
		return rst;
	}
}
