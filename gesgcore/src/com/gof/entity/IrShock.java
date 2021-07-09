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
@Table(name ="IR_SHOCK")
@Getter
@Setter
@ToString
public class IrShock extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = -7783664746646277314L;

	private String baseYymm; 
	private String irShockTyp;
	private String irCurveNm;	
	private String shockTypCd;
	private String matCd;	
	
	private Double shockVal;
	
	public IrShock() {}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
}
