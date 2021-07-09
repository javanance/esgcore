package com.gof.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import com.gof.entity.IrCurveYtmHis;
import com.gof.infra.EntityManagerUtil;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;


/**
 *  <p> 
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class IrCurveYtmHisDao {
//	private static EntityManager em = Persistence.createEntityManagerFactory("ociarazor").createEntityManager();
	private static EntityManager em = EntityManagerUtil.getEntityManger();
	public static String getMaxBaseDate (String bssd, String irCurveNm) {
		String query = "select max(a.baseDate) "
					 + "from IrCurveYtmHis a "
					 + "where 1=1 "
					 + "and a.irCurveNm = :irCurveNm "
					 + "and a.baseDate <= :bssd	"
					 ;
		Object maxDate =  em.createQuery(query)
				 				 .setParameter("irCurveNm", irCurveNm)			
								 .setParameter("bssd", DateUtil.toEndOfMonth(bssd))
								 .getSingleResult();
		if(maxDate==null) {
			log.warn("IR Curve History Data is not found {} at {}!!!" , irCurveNm, DateUtil.toEndOfMonth(bssd));
			return bssd;
		}
		return maxDate.toString();
	}
	
	public static List<IrCurveYtmHis> getIrCurveYtmHis(String bssd, String irCurveNm){
		String query = "select a from IrCurveYtmHis a "
					 + "where 1=1 "
					 + "and a.irCurveNm =:irCurveNm "
					 + "and a.baseDate  = :bssd	"
					 + "order by a.matCd"
					 ;
		
		List<IrCurveYtmHis> curveRst =  em.createQuery(query, IrCurveYtmHis.class)
				.setParameter("irCurveNm", irCurveNm)
				.setParameter("bssd", getMaxBaseDate(bssd, irCurveNm))
				.getResultList();
		
//		log.info("maxDate : {}, curveSize : {}", getMaxBaseDate(bssd, irCurveId),curveRst.size());
		return curveRst;
	}
	
	public static List<IrCurveYtmHis> getIrCurveYtmHisAt(String bssd, String irCurveNm){
		String query = "select a from IrCurveYtmHis a "
					 + "where 1=1 "
					 + "and a.irCurveNm =:irCurveNm "
					 + "and a.baseDate  = :bssd	"
					 + "order by a.matCd"
					 ;
		
		List<IrCurveYtmHis> curveRst =  em.createQuery(query, IrCurveYtmHis.class)
				.setParameter("irCurveNm", irCurveNm)
				.setParameter("bssd", bssd)
				.getResultList();
		
//		log.info("maxDate : {}, curveSize : {}", getMaxBaseDate(bssd, irCurveId),curveRst.size());
		return curveRst;
	}
	
	
	public static List<IrCurveYtmHis> getIrCurveYtmHis(String bssd, String irCurveNm, List<String> tenorList){
		String query = "select a from IrCurveYtmHis a "
					 + "where 1=1 "
					 + "and a.irCurveNm =:irCurveNm "
					 + "and a.baseDate  = :bssd	"
					 + "and a.matCd in (:matCdList)"
					 + "order by a.matCd"
					 ;
		
		List<IrCurveYtmHis> curveRst =  em.createQuery(query, IrCurveYtmHis.class)
				.setParameter("irCurveNm", irCurveNm)
				.setParameter("bssd", getMaxBaseDate(bssd, irCurveNm))
				.setParameter("matCdList", tenorList)
				.getResultList();		

		return curveRst;
	}	
	
}
