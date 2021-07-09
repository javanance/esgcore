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
@Table(name ="IR_CURVE_MST")
@Getter
@Setter
public class IrCurveMst implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
//	@Column(name ="IR_CURVE_ID")
	private String irCurveId;
	
	private String irCurveNm;
	private String curCd;

	private String applBizDv;
	private String applMethDv;
	
	@Column(name ="CRD_GRD_CD")
	private String creditGrate;
	
	@Column(name ="INTP_METH_CD")
	private String interpolMethod;
	
	private String refCurveId;
	
	
//	@Enumerated(EnumType.STRING)
//	private EBoolean useYn;
	
	public IrCurveMst() {}
	
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
		return irCurveId;
	}
}
