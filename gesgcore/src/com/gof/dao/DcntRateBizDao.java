package com.gof.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.gof.entity.DcntRateBiz;
import com.gof.infra.EntityManagerUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� ����� �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class DcntRateBizDao {
	private static EntityManager em = EntityManagerUtil.getEntityManger();
	
	public static List<DcntRateBiz> getTermStructure(String bssd, String bizDv, String irCurveNm){
		StringBuilder builder = new StringBuilder();
		builder.append("select a from DcntRateBiz a "
				+ "where a.irCurveNm = :irCurveNm "
				+ "and a.baseYymm  = :bssd "
				+ "and a.applBizDv  = :bizDv "
				)
		;
		
		TypedQuery<DcntRateBiz> q = em.createQuery(builder.toString(), DcntRateBiz.class);
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", bizDv);
		q.setParameter("irCurveNm", irCurveNm);
		;
		log.debug("Term Sructure param : {},{},{}", bssd, bizDv, irCurveNm);
		
		return q.getResultList();
	}
	
//	public static List<BizDiscountRateSce> getTermStructureBySceNo(String bssd, String bizDv, String irCurveId,  String sceNo){
//		StringBuilder builder = new StringBuilder();
//		builder.append("select a from BizDiscountRateSce a "
//					 + "where 1=1 "
//					 + "and a.baseYymm  = :bssd "
//					 + "and a.applyBizDv = :bizDv "
//					 + "and a.irCurveId = :irCurveId "
//					 + "and a.sceNo  = :sceNo "
//					)
//		;
//		
//		Query<BizDiscountRateSce> q = session.createQuery(builder.toString(), BizDiscountRateSce.class);
//		q.setParameter("bssd", bssd);
//		q.setParameter("bizDv", bizDv);
//		q.setParameter("irCurveId", irCurveId);
//		q.setParameter("sceNo", sceNo);
//		;
//		
//		return q.getResultList();
//	}
//
//
//	public static List<BizDiscountRateSce> getTermStructureAllScenario(String bssd, String bizDv, String irCurveId){
//			StringBuilder builder = new StringBuilder();
//			builder.append("select a from BizDiscountRateSce a "
//					+ "where a.baseYymm  = :bssd "
//					+ "and a.applyBizDv = :bizDv "
//					+ "and a.irCurveId = :irCurveId "
//	//				+ "and a.sceNo  = :sceNo"
//					)
//			;
//			
//			Query<BizDiscountRateSce> q = session.createQuery(builder.toString(), BizDiscountRateSce.class);
//			q.setParameter("bssd", bssd);
//			q.setParameter("bizDv", bizDv);
//			q.setParameter("irCurveId", irCurveId);
//	//		q.setParameter("sceNo", sceNo);
//			;
//			
//			return q.getResultList();
//		}
//
//
//	public static List<BizDiscountRate> getBizPrecedingByMaturity(String bssd, int monNum, String irCurveId, String matCd){
//		String query ="select a from BizDiscountRate a "
//					+ "where a.irCurveId = :irCurveId "
//					+ "and a.baseYymm > :stDate "
//					+ "and a.baseYymm <= :endDate "
//					+ "and a.matCd=:matCd"
//					;
//		
//		Query<BizDiscountRate> q = session.createQuery(query, BizDiscountRate.class);
//		q.setParameter("stDate", FinUtils.addMonth(bssd,  monNum));
//		q.setParameter("endDate", bssd);
//		q.setParameter("irCurveId", irCurveId);
//		q.setParameter("matCd", matCd);
//		;
//		
//		return q.getResultList();
//	}
//	
//	public static List<BizDiscountRate> getTimeSeries(String bssd, String bizDv, String irCurveId, String matCd, int monNum){
//		StringBuilder builder = new StringBuilder();
//		builder.append("select a from BizDiscountRate a "
//				+ "where a.baseYymm <= :bssd "
//				+ "and a.baseYymm  >=  :stBssd "
//				+ "and a.applyBizDv  = :bizDv "
//				+ "and a.irCurveId   = :irCurveId "
//				+ "and a.matCd = :matCd"
//				+ "order by a.baseYymm desc"
//				)
//		;
//		
//		Query<BizDiscountRate> q = session.createQuery(builder.toString(), BizDiscountRate.class);
//		q.setParameter("bssd", bssd);
//		q.setParameter("stBssd", FinUtils.addMonth(bssd, monNum));
//		q.setParameter("bizDv", bizDv);
//		q.setParameter("irCurveId", irCurveId);
//		q.setParameter("matCd", matCd);
//		;
//		
//		return q.getResultList();
//	}
//	
//	public static List<BizDiscountRate> getTimeSeries(String bssd, String bizDv, String irCurveId, int monNum){
//		StringBuilder builder = new StringBuilder();
//		builder.append("select a from BizDiscountRate a "
//				+ "where a.baseYymm <= :bssd "
//				+ "and a.baseYymm  >=  :stBssd "
//				+ "and a.applyBizDv = :bizDv "
//				+ "and a.irCurveId = :irCurveId "
////				+ "and a.matCd = :matCd "
//				
//				)
//		;
//		
//		Query<BizDiscountRate> q = session.createQuery(builder.toString(), BizDiscountRate.class);
//		q.setParameter("bssd", bssd);
//		q.setParameter("stBssd", FinUtils.addMonth(bssd,  monNum));
//		q.setParameter("bizDv", bizDv);
//		q.setParameter("irCurveId", irCurveId);
////		q.setParameter("matCd", matCd);
//		;
//		
//		return q.getResultList();
//	}
//
//	public static List<BizDiscountRateUd> getBizDiscountRateUd(String bssd, String bizDv){
//		String query ="select a from BizDiscountRateUd a "
//					+ "where a.applyBizDv = :bizDv "
//					+ "and a.baseYymm = :bssd "
//		;
//		
//		Query<BizDiscountRateUd> q = session.createQuery(query, BizDiscountRateUd.class);
//		
//		q.setParameter("bssd", bssd);
//		q.setParameter("bizDv", bizDv);
//		
//		return q.getResultList();
//	}
}
