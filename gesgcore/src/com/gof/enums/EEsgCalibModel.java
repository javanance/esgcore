package com.gof.enums;

public enum EEsgCalibModel {
	   FLAT    		("1")
	 , LOCAL		 	("2")
	 , SIGMA_LOCAL 	("3")
;
	
	private String legacyCode;

	private EEsgCalibModel(String legacy) {
		this.legacyCode = legacy;
	}

	public String getLegacyCode() {
		return legacyCode;
	}

	public static EEsgCalibModel getEIrModelType(String legacyCode) {
		for(EEsgCalibModel aa : EEsgCalibModel.values()) {
			if(aa.getLegacyCode().equals(legacyCode)) {
				return aa;
			}
		}
		return SIGMA_LOCAL;
	}

}
