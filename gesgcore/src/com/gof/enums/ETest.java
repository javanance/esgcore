package com.gof.enums;

public enum ETest {
	  ESG1	(1, "ALL", "LIQ PREM")
	, ESG2	(2, "ALL", "Liq Adj Rf Rate")
	, ESG2A	(2, "I", "Biz BottomUp for ifrs")
	;
	
	
	
	private int jobNo;
	private String jobName;
	private String bizDv;

	private ETest(int jobNo, String bizDv, String jobName) {
		this.jobNo = jobNo;
		this.bizDv = bizDv;
		this.jobName = jobName;
	}
	

	public int getJobNo() {
		return jobNo;
	}
	
	public String getBizDv() {
		return bizDv;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobNo(int jobNo) {
		this.jobNo = jobNo;
	}
	
	
}
