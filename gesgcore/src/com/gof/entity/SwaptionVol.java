package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
//@IdClass(SwaptionVolId.class)
@Table(name ="SWAPTION_VOL")
@Getter
@Setter
public class SwaptionVol implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;	

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String baseYymm; 
	private Double swaptionMaturity;
	private Double swapTenor;
	private Double vol;

	
	public SwaptionVol() {}
	
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
		
		builder.append(baseYymm).append(delimeter)
			   .append(swaptionMaturity).append(delimeter)
			   .append(swapTenor).append(delimeter)
			   .append(vol).append(delimeter)
			   ;
		return builder.toString();
	}
	
	
	
	
}
