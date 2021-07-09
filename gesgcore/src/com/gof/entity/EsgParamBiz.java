package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name ="BIZ_ESG_PARAM")
@Getter
@Setter
public class EsgParamBiz implements Serializable {
	private static final long serialVersionUID = 1524655691890282755L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;

	private String baseYymm;
	private String applBizDv;
	private String irModelId;
	private String paramTypCd;
	private String matCd;
	
	private Double applParamVal;

	
//	@ManyToOne()
//	@JoinColumn(name ="IR_MODEL_ID", insertable=false, updatable=false)
//	private EsgMst esgMst ;
	
	public EsgParamBiz() {}
	

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public double getAppliedVal() {
		if(paramTypCd.equals("ALPHA") && matCd.equals("M1200")) {
//			return Math.max(applParamVal,  0.025);
//			return Math.max(applParamVal,  0.015);
		}
		return applParamVal;
	}
}


