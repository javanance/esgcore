package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.gof.abstracts.BaseEntity;
import com.gof.enums.EBoolean;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="IR_CURVE")
@Getter
@Setter
public class IrCurve extends BaseEntity implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
//	@Id
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	private long id;
	
	private String irCurveNm;
	
	private String irCurveName;
	private String curCd;

	private String applBizDv;
	private String applMethDv;
	
	@Column(name ="CRD_GRD_CD")
	private String creditGrate;
	
	@Column(name ="INTP_METH_CD")
	private String interpolMethod;
	
	@ManyToOne
	@JoinColumn(name ="REF_CURVE_ID", insertable=false, updatable= false)
	private IrCurve refCurveId;
	
	private String refCurveNm;
	
	
	@Enumerated(EnumType.STRING)
	private EBoolean useYn;
	
	public IrCurve() {}
	
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
		return irCurveNm;
	}
}
