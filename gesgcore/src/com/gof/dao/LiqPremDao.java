package com.gof.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.gof.entity.LiqPremHis;
import com.gof.entity.LiqPremBiz;
import com.gof.entity.LiqPremUd;
import com.gof.infra.EntityManagerUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class LiqPremDao {
	private static EntityManager em = EntityManagerUtil.getEntityManger();
	
	private static String  getApplyDateLiqPremiumUd(String bssd, String liqModelNm){
		String maxQuery = "select max(a.applStYymm) from LiqPremUd a "
				+ "		where 1=1"
				+ "		and a.liqModelNm = :liqModelNm"
				+ "		and a.applStYymm <= :bssd"
				+ "		and a.applEdYymm >=  :bssd"
		;
		
		TypedQuery<String> q = em.createQuery(maxQuery, String.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("liqModelNm", liqModelNm);
		;
		
		log.info("Applied Date for User Input Liq premium : {}", q.getSingleResult());
		return q.getSingleResult();
	}
	
		
	public static List<LiqPremUd> getLiqPremiumUd(String bssd,String liqModelNm){
		
		String query = "select a from LiqPremUd a "
				+ "		where 1=1"
				+ "		and a.liqModelNm = :liqModelNm"
				+ "		and a.applStYymm = :stDate"
		;
		
		TypedQuery<LiqPremUd> q = em.createQuery(query, LiqPremUd.class);
		
		q.setParameter("stDate", getApplyDateLiqPremiumUd(bssd, liqModelNm));
		q.setParameter("liqModelNm", liqModelNm);
		
		
		return q.getResultList();
	}

	public static List<LiqPremHis> getLiqPremium(String bssd, String liqModelNm, String order){
		
		String query = "select a from LiqPremHis a "
				+ "where a.liqModelNm = :liqModelNm "
				+ "and a.baseYymm = :bssd "
				+ "order by a.matCd :desc"
		;
		
		TypedQuery<LiqPremHis> q = em.createQuery(query, LiqPremHis.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("liqModelNm", liqModelNm);
		q.setParameter("desc", order);
		
		return q.getResultList();
	}
	
	public static List<LiqPremHis> getLiqPremium(String bssd, String liqModelNm){
		
		String query = "select a from LiqPremHis a "
				+ "where a.liqModelNm = :liqModelNm "
				+ "and a.baseYymm = :bssd "
				
		;
		
		TypedQuery<LiqPremHis> q = em.createQuery(query, LiqPremHis.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("liqModelNm", liqModelNm);
		
		return q.getResultList();
	}

	public static List<LiqPremHis> getLiqPremiumBtw(String stBssd, String bssd, String liqModelNm ){
		
		String query = "select a from LiqPremHis a "
				+ "where a.liqModelNm = :liqModelNm "
				+ "and a.baseYymm > :stBssd "
				+ "and a.baseYymm <= :bssd "
		;
		
		TypedQuery<LiqPremHis> q = em.createQuery(query, LiqPremHis.class);
		
		q.setParameter("stBssd", stBssd);
		q.setParameter("bssd", bssd);
		q.setParameter("liqModelNm", liqModelNm);
		
		return q.getResultList();
	}
	
	public static List<LiqPremBiz> getBizLiqPremium(String bssd, String bizDv){
		
		String query = "select a from LiqPremBiz a "
					+  "where a.applBizDv = :bizDv "
					+  "and a.baseYymm = :bssd"
					;
		
		TypedQuery<LiqPremBiz> q = em.createQuery(query, LiqPremBiz.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", bizDv);
		
		return q.getResultList();
	}
}
