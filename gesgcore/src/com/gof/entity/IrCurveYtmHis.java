package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="IR_CURVE_YTM_HIS")
@Getter
@Setter
public class IrCurveYtmHis implements Serializable {	
	
	private static final long serialVersionUID = 1340116167808300605L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String baseDate;	
	private String irCurveNm;	
	private String matCd;
	private Double ytmRate;
	
	public IrCurveYtmHis() {}	   
	
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
		return "ytmCurveHis [baseDate=" + baseDate + ", irCurveNm=" + irCurveNm + ", matCd=" + matCd + ", intRate="	+ ytmRate + "]";
	}
	
}