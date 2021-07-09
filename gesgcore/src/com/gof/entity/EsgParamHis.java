package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table( name ="ESG_PARAM_HIS")
@Getter
@Setter
public class EsgParamHis implements Serializable {

	private static final long serialVersionUID = -3199922647182076353L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String baseYymm;
	private String irModelId;
	private String irModelTyp;
	private String calibModel; 
	private String paramTypCd;
	private String matCd;	
	
	private Double paramVal;
	
	
	public EsgParamHis() {}

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
		return baseYymm + "_" +  paramTypCd + "_" + matCd +"_" + paramVal;
	}

}


