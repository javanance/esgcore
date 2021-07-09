package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.enums.EBoolean;
import com.gof.enums.EEsgCalibModel;
import com.gof.enums.EIrModelType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name ="ESG_MST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter(value = AccessLevel.PROTECTED)

public class EsgMst implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String irModelId;
	@Column(name  ="IR_MODEL_NM")
	private String irModelName;
	
	@Enumerated(EnumType.STRING)
	private EIrModelType irModelTyp;

	@Enumerated(EnumType.STRING)
	private EEsgCalibModel calibModel;
	
	@Enumerated(EnumType.STRING)
	private EBoolean useYn;
}
