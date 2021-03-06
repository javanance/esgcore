package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
//@IdClass(JobLogId.class)
@Table(name ="JOB_INFO")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JobLog implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
//	@Id private String jobId;
//	@Id	private String calcStart;

	private String jobId;
	private String jobNm;
	private String baseYymm;
	private String calcDate;

	private String calcStart;
	private String calcEnd;
//	private Double calcElps;
	private String calcElps;
	private String calcScd;
	private String esgLogInfo;
	
	@Transient
	private LocalDateTime jobStart;
	
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
	
}
