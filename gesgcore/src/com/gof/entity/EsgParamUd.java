package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import com.gof.abstracts.BaseEntity;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name ="USER_ESG_PARAM")
@FilterDef(name="paramApplyEqBaseYymm", parameters= { @ParamDef(name="baseYymm", type="string") })
@Getter
@Setter
public class EsgParamUd extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -8151467682976876533L;
	
	private String applStYymm;
	
	private String irModelId;
	private String paramTypCd;
	private String matCd;
	
	private String applEdYymm;
	
	private Double applParamVal;
	
//	@ManyToOne()
//	@JoinColumn(name ="IR_MODEL_ID", insertable=false, updatable=false)
//	private EsgMst esgMst ;
	
	public EsgParamUd() {}
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public EsgParamBiz convertToBizEsgParam(String bssd, String bizDv) {
		EsgParamBiz rst = new EsgParamBiz();
		rst.setBaseYymm(bssd);
		rst.setApplBizDv(bizDv);
		rst.setIrModelId(this.irModelId);
		rst.setParamTypCd(this.paramTypCd);
		rst.setMatCd(this.matCd);
		rst.setApplParamVal(this.applParamVal);
//		rst.setEsgMst(this.esgMst);
		
		return rst;
	}
}


