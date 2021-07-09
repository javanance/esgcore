package com.gof.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.gof.entity.DcntRateBiz;
import com.gof.entity.DcntRateBu;
import com.gof.infra.EntityManagerUtil;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� ����� �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class DcntRateDao {
//	private static Session session = HibernateUtil.getSessionFactory().openSession();
	private static EntityManager em = EntityManagerUtil.getEntityManger();
	
	
	/** 
	*  @param bssd 	   
	*  @param irCurveNm 
	*  @return        Bottom Up
	*/ 
	
	public static List<DcntRateBu> getTermStructure(String bssd, String irCurveNm){
		StringBuilder builder = new StringBuilder();
		builder.append("select a from DcntRateBu a "
				+ "where a.irCurveNm = :irCurveNm "
				+ "and a.baseYymm  = :bssd "
				)
		;
		
		TypedQuery<DcntRateBu> q = em.createQuery(builder.toString(), DcntRateBu.class);
		q.setParameter("bssd", bssd);
		q.setParameter("irCurveNm", irCurveNm);
		;
		log.debug("test : {}", bssd);
		
		return q.getResultList();
	}
	
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
	
//	public static List<DcntSce> getTermStructureScenario(String bssd, String irCurveNm, String sceNo){
//		StringBuilder builder = new StringBuilder();
//		builder.append("select a from DcntSce a "
//				+ "where a.baseYymm  = :bssd "
//				+ "and a.irCurveNm = :irCurveNm "
//				+ "and a.sceNo  = :sceNo "
//				)
//		;
//		
//		TypedQuery<DcntSce> q = em.createQuery(builder.toString(), DcntSce.class);
//		q.setParameter("bssd", bssd);
//		q.setParameter("irCurveNm", irCurveNm);
//		q.setParameter("sceNo", sceNo);
//		;
//		
//		return q.getResultList();
//	}
//	
	
	/** 
	*  @param bssd 	  
	*  @param monNum 
	*  @return       
	*/ 
	
	public static List<DcntRateBu> getPrecedingByMaturity(String bssd, int monNum, String irCurveNm, String matCd){
		String query ="select a from DcntRateBu a "
					+ "where a.irCurveNm = :irCurveNm "
					+ "and a.baseYymm > :stDate "
					+ "and a.baseYymm <= :endDate "
					+ "and a.matCd=:matCd"
					;
		
		TypedQuery<DcntRateBu> q = em.createQuery(query, DcntRateBu.class);
		q.setParameter("stDate", DateUtil.addMonthToString(bssd,  monNum));
		q.setParameter("endDate", bssd);
		q.setParameter("irCurveNm", irCurveNm);
		q.setParameter("matCd", matCd);
		;
		
		return q.getResultList();
	}
	
	public static DcntRateBu getShortRateHis(String bssd, String curveId){
		String query = "select a from DcntRateBu a "
				+ "where 1=1 "
				+ "and a.baseYymm = :bssd	"
				+ "and a.irCurveNm =:param1 "
				+ "and a.matCd = :matCd "
				+ "order by a.baseYymm"
				;
		
		DcntRateBu curveRst =  em.createQuery(query, DcntRateBu.class)
				.setParameter("param1", curveId)
				.setParameter("bssd", bssd)
				.setParameter("matCd", "M0003")
				.getSingleResult()
				;		
		

		return curveRst;
	}
	
		
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
