package com.gof.enums;

public enum ERunArgument {
	  time ( "TIME")
//	  , t( "TIME")
	, job ( "JOB")
	, meta( "META")
//	, properties ( "PROPERTIES")
//	, p ("PROPERTIES")
	;
	
	
	private String alias;

	private ERunArgument(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	
}
