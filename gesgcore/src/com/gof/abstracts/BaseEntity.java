package com.gof.abstracts;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;


@MappedSuperclass
@Getter
@Setter
public class BaseEntity implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
//	public BaseEntity(String lastModifiedBy, LocalDateTime lastModifiedDate) {
//		this.lastModifiedBy = lastModifiedBy;
//		this.lastModifiedDate = lastModifiedDate;
//	}
//	private String remark;
	
//	private String updatedBy;
//	private LocalDateTime updateDate;

}
