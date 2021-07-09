package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.abstracts.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name ="IR_SHOCK_PARAM")
@Access(AccessType.FIELD)
@Getter
@Setter
@ToString
public class IrShockParam extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = -6565657307170343742L;
	
	private String baseYymm; 
	private String irShockTyp;
	private String irCurveNm;
	private String paramTypCd;
	
	private Double paramVal;
	
	public IrShockParam() {}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}	
	
}
