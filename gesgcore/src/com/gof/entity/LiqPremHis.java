package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import com.gof.abstracts.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="LIQ_PREM_HIS")
@Getter
@Setter
public class LiqPremHis extends BaseEntity implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;

	private String baseYymm; 
	private String liqModelNm;
	private String matCd;
	private Double liqPrem;
	
	public LiqPremHis() {
	}

	public LiqPremHis(double liqPrem) {
		this.liqPrem = liqPrem;
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
			   .append(liqModelNm).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(liqPrem).append(delimeter)
			   ;
		return builder.toString();
	}

//	@Override
	public String getIrCurveId() {
		return null;
	}

//	@Override
	public Double getIntRate() {
		return liqPrem;
	}
//	@Override
	public Double getSpread() {
		return liqPrem;
	}
	
	public int getMatNum() {
		return Integer.parseInt(matCd.substring(1));
	}
	
	public LiqPremBiz convertTo(String bizDv) {
		LiqPremBiz rst = new LiqPremBiz();
		rst.setBaseYymm(this.baseYymm);
		rst.setApplBizDv(bizDv);
		rst.setMatCd(this.matCd);
		rst.setLiqPrem(this.liqPrem);
		return rst;
		
	}

	public LiqPremHis(String baseYymm, String liqModelNm, String matCd, Double liqPrem) {
		this.baseYymm = baseYymm;
		this.liqModelNm = liqModelNm;
		this.matCd = matCd;
		this.liqPrem = liqPrem;
	}
}
