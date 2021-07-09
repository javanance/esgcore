package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
//@IdClass(SmithWilsonParamHisId.class)
@Table(name="SW_PARAM_HIS")
@Getter
@Setter
public class SwParamHis implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(name ="APPL_ST_YYMM")
	private String applStartYymm;
	
	@Column(name ="APPL_ED_YYMM")
	private String applEndYymm;
	private String curCd;	

//	@Column(name ="IR_CURVE_DV")
//	private String irCurveDv;
	
	@Column(name ="LLP")
	private Double llp;
	
	@Column(name ="UFR")
	private Double ufr;

	@Column(name ="UFR_T")
	private Double ufrT;
	
	
	public SwParamHis() {
		super();
	}
	
	public SwParamHis(String curCd, double ufr, double ufrT) {
		super();
		this.curCd = curCd;
		this.ufr = ufr;
		this.ufrT = ufrT;
	}

	
	
	
	
}


