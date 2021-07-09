package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import com.gof.abstracts.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="USER_LIQ_PREM")
@Getter
@Setter
public class LiqPremUd extends BaseEntity implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;

	private String applStYymm;
	private String liqModelNm;
	private String matCd;
	private String applEdYymm;
	private Double liqPrem;
	
	public LiqPremUd() {}
	
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
		
		builder.append(applStYymm).append(delimeter)
			   .append(liqModelNm).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(applEdYymm).append(delimeter)
			   .append(liqPrem).append(delimeter)
			   ;
		return builder.toString();
	}
	
	public LiqPremHis convertToLiqPreminum(String bssd) {
		LiqPremHis rst = new LiqPremHis();
		rst.setBaseYymm(bssd);
		rst.setLiqModelNm("COVERED_BOND_KDB");
		rst.setMatCd(this.matCd);
		rst.setLiqPrem(this.liqPrem);
		return rst;
	}
	
	public LiqPremBiz convertToBizLiqPremium(String bssd, String bizDv) {
		LiqPremBiz rst = new LiqPremBiz();
		
		rst.setBaseYymm(bssd);
		rst.setApplBizDv(bizDv);
		rst.setMatCd(this.matCd);
		rst.setLiqPrem(this.liqPrem);
		
		return rst;
	}
}
