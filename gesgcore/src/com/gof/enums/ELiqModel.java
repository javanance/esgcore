package com.gof.enums;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public enum ELiqModel {
	   COVERED_BOND_DIFF	 (	(kdb, ktb) -> kdb - ktb)
	 , COVERED_BOND_RATIO	 (	(kdb, ktb) -> kdb / ktb )
	
	 ;
	
	private BinaryOperator<Double> spreadOper;

	private ELiqModel(BinaryOperator<Double> spreadOper) {
		this.spreadOper = spreadOper;
	}

	public BinaryOperator<Double> getSpreadOper() {
		return spreadOper;
	}
}
