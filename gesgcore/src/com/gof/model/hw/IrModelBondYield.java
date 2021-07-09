package com.gof.model.hw;


import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IrModelBondYield implements Serializable {
	
	private static final long serialVersionUID = 8283356676488673425L;

	//@Id
	private String baseDate;
	
	private String irModelId;
	
	private String matCd;
	
	private String sceNo;
	
	private String irCurveId;
	
	private Integer monthSeq;
	
	private Double bondYieldCont;
	
	private Double bondYieldDisc;
	
	private String lastModifiedBy;
	
	private LocalDateTime lastUpdateDate;
	
	public IrModelBondYield() {}

}
