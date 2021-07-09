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
@Table(name ="BIZ_LIQ_PREM")
@Getter
@Setter
public class LiqPremBiz extends BaseEntity implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;

	private String baseYymm;
	private String applBizDv;
	private String matCd;
	private String liqModelNm;
	private Double liqPrem;
	
	public LiqPremBiz() {
	
	}
	public LiqPremBiz(double liqPrem) {
		this.liqPrem = liqPrem;
	}
	
	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return super.equals(arg0);
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
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
			   .append(matCd).append(delimeter)
			   .append(liqModelNm).append(delimeter)
			   .append(liqPrem).append(delimeter)
//			   .append(lastModifiedBy).append(delimeter)
//			   .append(lastUpdateDate)
			   ;
		return builder.toString();
	}
	
}
