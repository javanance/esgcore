package com.gof.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.gof.entity.EsgParamBiz;
import com.gof.entity.EsgParamHis;
import com.gof.entity.EsgParamUd;
import com.gof.infra.EntityManagerUtil;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> �ݸ������� �Ű����� ������ ������.
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class EsgParamDao {
	private static EntityManager em = EntityManagerUtil.getEntityManger();
	
	public static List<EsgParamHis> getFullLocalCalibParamCalHis(String bssd , int monthNum, String paramType, String matCd) {
		String q = "select a from EsgParamHis a "
				+ " where 1=1" 
				+ " and a.baseYymm between :stDate and :endDate "
				+ " and a.paramTypCd =  :paramType"
				+ " and a.paramCalcCd = :paramCalcCd"
				+ " and a.matCd = :matCd" ;
		
		return em.createQuery(q, EsgParamHis.class)
		.setParameter("stDate", DateUtil.addMonthToString(bssd, monthNum))
		.setParameter("endDate", bssd)
		.setParameter("paramType", paramType)
		.setParameter("matCd", matCd)
		.setParameter("paramCalcCd", "FULL_LOCAL_CALIB")
		.getResultList()
		;

	}

	public static List<EsgParamHis> getSigmaLocalParamHis(String bssd , int monthNum, String paramType, String matCd, String irModelId) {
		String q = "select a from EsgParamHis a "
				+ " where 1=1" 
				+ " and a.baseYymm between :stDate and :endDate "
				+ " and a.irModelId =  :irModelId"
				+ " and a.paramTypCd =  :paramType"
				+ " and a.matCd 	= :matCd" ;
		
		return em.createQuery(q, EsgParamHis.class)
		.setParameter("stDate", DateUtil.addMonthToString(bssd, monthNum))
		.setParameter("endDate", bssd)
		.setParameter("matCd", matCd)
		.setParameter("irModelId", irModelId)
		.setParameter("paramType", paramType)
		.getResultList()
		;

	}
	
	public static List<EsgParamHis> getEsgParamHis(String bssd , String irModelId) {
		String q = "select a from EsgParamHis a "
				+ " where 1=1" 
				+ " and a.baseYymm =:bssd "
				+ " and a.irModelId =  :irModelId"
				;
		
		return em.createQuery(q, EsgParamHis.class)
		.setParameter("bssd", bssd)
		.setParameter("irModelId", irModelId)
		.getResultList()
		;

	}

//	public static List<EsgParamUd> getBizEsgParamUd(String bssd ) {
////		String irModelId ="HW_1_VOL_SURFACE";							//Default Value
//		String q = "select a from EsgParamUd a "
//				+ " where 1=1" 
//				+ " and a.applyStartYymm = :bssd "
//				+ " and a.applyBizDv 	= :bizDv "
//				+ " and a.irModelId 	= :irModelId "
//				;
//		
//		return em.createQuery(q, EsgParamUd.class)
//				.setParameter("bssd", getAppliedDate(bssd))
//				.setParameter("bizDv", "I")
//				.setParameter("irModelId", irModelId)
//				.getResultList()
//		;
//
//	}
	
	public static List<EsgParamUd> getEsgParamUd(String bssd , String irModelId) {
		
		String q = "select a from EsgParamUd a "
				+ " where 1=1" 
				+ " and a.applStYymm = :bssd "
				+ " and a.irModelId 	= :irModelId "
				;
		
		return em.createQuery(q, EsgParamUd.class)
				.setParameter("bssd", getAppliedDate(bssd, irModelId))
				.setParameter("irModelId", irModelId)
				.getResultList()
		;

	}
	

	public static List<EsgParamBiz> getBizEsgParam(String bssd, String irModelId ) {
		String q = "select a from EsgParamBiz a "
				+ " where 1=1" 
				+ " and a.baseYymm = :bssd "
				+ " and a.irModelId 	= :irModelId "
				;
		
		return em.createQuery(q, EsgParamBiz.class)
				.setParameter("bssd", bssd)
				.setParameter("irModelId", irModelId)
				.getResultList()
		;

	}
//	private static String getAppliedDate(String bssd) {
//		String irModelId ="HW_1_VOL_SURFACE";							//Default Value
//		List<EsgMst> esgMstList = EsgMstDao.getEsgMst(EBoolean.Y);
//		if(!esgMstList.isEmpty()) {
//			irModelId = esgMstList.get(0).getIrModelId();
//		}
//		
//		StringBuilder builder = new StringBuilder();
//		builder.append("select max(a.applyStartYymm) from BizEsgParamUd a "
//				+ "		where 1=1"
//				+ "		and a.applyBizDv = :bizDv"
//				+ "		and a.irModelId  = :irModelId"
//				+ "		and a.applyStartYymm <= :bssd"
//				+ "		and a.applyEndYymm >=  :bssd"
//				)
//		;
//		TypedQuery<String> q = em.createQuery(builder.toString(), String.class);
//		
//		q.setParameter("bssd", bssd);
//		q.setParameter("bizDv", "I");
//		q.setParameter("irModelId", irModelId);
//		;
//		
//		log.info("apply Date for Biz Applied Parameter : {}", q.getSingleResult());
//		return q.getSingleResult();
//	}
	
	private static String getAppliedDate(String bssd, String irModelId) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("select max(a.applStYymm) from EsgParamUd a "
				+ "		where 1=1"
				+ "		and a.irModelId  = :irModelId"
				+ "		and a.applStYymm <= :bssd"
				+ "		and a.applEdYymm >=  :bssd"
				)
		;
		TypedQuery<String> q = em.createQuery(builder.toString(), String.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("irModelId", irModelId);
		;
		
		log.info("apply Date for Biz Applied Parameter : {}", q.getSingleResult());
		return q.getSingleResult();
	}
}
