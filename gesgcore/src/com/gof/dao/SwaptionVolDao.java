package com.gof.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import com.gof.entity.SwaptionVol;
import com.gof.infra.EntityManagerUtil;
import com.gof.util.DateUtil;


/**
 *  <p> Swaption Vol �� �̷�  ������ ������.
 *  <p> 
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
public class SwaptionVolDao {

//	private static EntityManager em = Persistence.createEntityManagerFactory("ociarazor").createEntityManager();
	private static EntityManager em = EntityManagerUtil.getEntityManger();
	
	/** 
	*  @param bssd 	
	*  @param	monthNum
	*  @return		                    
	*/ 
	public static List<SwaptionVol> getPrecedingSwaptionVol(String bssd, int monthNum){
		String q = " select a from SwaptionVol a where a.baseYymm between :stDate and :endDate ";
		
		return em.createQuery(q, SwaptionVol.class)
						.setParameter("stDate", DateUtil.addMonth(bssd, monthNum))
						.setParameter("endDate", bssd)
						.getResultList()
						;
	}
	
	public static List<SwaptionVol> getSwaptionVol(String bssd){
		String q = " select a from SwaptionVol a " 
				+ "where a.baseYymm = :endDate "
				+ "order by a.swapTenor, a.swaptionMaturity "
				;
		return em.createQuery(q, SwaptionVol.class)
						.setParameter("endDate", bssd)
						.getResultList()
						;
	}
	
}
