package com.gof.model.hw;


import java.io.Serializable;

import com.gof.entity.SwaptionVol;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/*
* Non Entity. Just For Model 
* TODO : model Change to Entity...
* 
*/

@Getter
@Setter
@ToString
public class SwpnVolInfo implements Serializable {
	
	private static final long serialVersionUID = -6291459869154222962L;

	private String baseYymm;
	private Integer swpnMat;
	private Integer swapTenor;
	private Double vol;

	public SwpnVolInfo() {}
	
	public static SwpnVolInfo convertFrom(SwaptionVol swapVol) {
		SwpnVolInfo rst  = new SwpnVolInfo();
		
		rst.baseYymm = swapVol.getBaseYymm();
		rst.swpnMat = Integer.valueOf(swapVol.getSwaptionMaturity().intValue());
		rst.swapTenor = Integer.valueOf(swapVol.getSwapTenor().intValue());
		rst.vol = swapVol.getVol();
		
		return rst;
	}
	
}
