package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.gof.util.DateUtil;

import lombok.Getter;
import lombok.Setter;

@Entity
//@IdClass(IrCurveHisId.class)
@Table(name ="IR_CURVE_HIS")
@Getter
@Setter

public class IrCurveHis implements Serializable{
//public class IrCurveHis implements Serializable, IIntRate, Pricable{
	
	private static final long serialVersionUID = -8151467682976876533L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String baseDate; 
	private String irCurveNm;
	private String matCd;
	
	private Double intRate;
	@Transient	private String sceNo; 
	@Transient	private int forwardNum;
	
	@Transient
//	@ManyToOne
//	@JoinColumn(name ="IR_CURVE_ID", insertable=false, updatable= false)
	private IrCurve irCurve;
	
	public IrCurveHis() {
	
	}
	
	public IrCurveHis(String baseDate, String irCurveNm, String matCd,String sceNo, Double intRate) {
		this.baseDate = baseDate;
		this.irCurveNm = irCurveNm;
		this.matCd = matCd;
		this.sceNo = sceNo;
		this.intRate = intRate;
	}
	public IrCurveHis(String baseDate, String matCd, Double intRate) {
		this.baseDate = baseDate;
		this.matCd = matCd;
		this.intRate = intRate;
	}
	
	public IrCurveHis(String bssd, IrCurveHis curveHis) {
		this.baseDate = curveHis.getBaseDate();
		this.irCurveNm = curveHis.getIrCurveNm();
		this.matCd = curveHis.getMatCd();
		this.sceNo = curveHis.getSceNo();
		this.intRate = curveHis.getIntRate();

		this.forwardNum = (int)DateUtil.monthBetween(bssd, curveHis.getBaseDate());
				
	}
	
//	@Override
	public Double getSpread() {
		return 0.0;
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
		
		builder.append(baseDate).append(delimeter)
			   .append(sceNo==null? "0":sceNo).append(delimeter)
			   .append(irCurveNm).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(intRate).append(delimeter)
			   .append(forwardNum)
//			   .append(lastUpdateDate)
			   ;

		return builder.toString();
	}
//******************************************************Biz Method**************************************
//	@Transient
	public int getMatNum() {
		return Integer.parseInt(matCd.substring(1));
	}
	public IrCurveHis addForwardTerm(String bssd) {
		return new IrCurveHis(bssd, this);
	}
	
	public String getBaseYymm() {
		return getBaseDate().substring(0,6);
	}
	public boolean isBaseTerm() {
		if(matCd.equals("M0003") 
				|| matCd.equals("M0006") 
				|| matCd.equals("M0009")
				|| matCd.equals("M0012")
				|| matCd.equals("M0024")
				|| matCd.equals("M0036")
				|| matCd.equals("M0060")
				|| matCd.equals("M0084")
				|| matCd.equals("M0120")
				|| matCd.equals("M0240")
				) {
			return true;
		}
		return false;	
			
	}
	
//	public LiqPremium convertTo(String modelId) {
//		LiqPremium rst = new LiqPremium();
//		rst.setBaseYymm(this.getBaseYymm());
//		rst.setModelId(modelId);
//		rst.setMatCd(this.matCd);
//		rst.setLiqPrem(this.getIntRate());
//		rst.setLastModifiedBy("ESG");
//		rst.setLastUpdateDate(LocalDateTime.now());
//		
//		return rst;
//		
//	}

//	@Override
	public double getPrice() {
		return intRate;
//		return Math.pow(intRate, -1.0* getMatNum()/12.0);
	}
	
	public IrCurveWeek convertToWeek() {
		IrCurveWeek rst = new IrCurveWeek();
		
		String dayOfWeek = LocalDate.parse(baseDate, DateTimeFormatter.BASIC_ISO_DATE).getDayOfWeek().name();
		
		rst.setBaseDate(this.baseDate);
		rst.setIrCurveNm(this.irCurveNm);
		rst.setMatCd(this.matCd);
		rst.setIntRate(this.intRate);
		rst.setDayOfWeek(dayOfWeek);
//		rst.setIrCurve(this.irCurve);
//		rst.setLastModifiedBy("ESG");
//		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst; 
		
	}
	
	
}
