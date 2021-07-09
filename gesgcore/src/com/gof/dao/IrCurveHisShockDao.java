package com.gof.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.gof.entity.IrCurveHis;
import com.gof.entity.IrCurveWeek;
import com.gof.infra.EntityManagerUtil;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;



/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class IrCurveHisShockDao {
	private static EntityManager em = EntityManagerUtil.getEntityManger();
	
    //TODO:
	public static List<IrCurveHis> getIrCurveListTermStructureForShock(String bssd, String stBssd, String irCurveNm){
		
		String query =" select a from IrCurveHis a " 
					+ "where a.irCurveNm =:irCurveNm "			
					+ "and a.baseDate >= :stBssd "
					+ "and a.baseDate <= :bssd "
					//+ "and a.matCd in (:matCdList)"
					+ "order by a.baseDate, a.matCd "
					;
		
		return em.createQuery(query, IrCurveHis.class)
				.setParameter("irCurveNm", irCurveNm)
				.setParameter("stBssd", stBssd)
				.setParameter("bssd", DateUtil.toEndOfMonth(bssd))
//				.setParameterList("matCdList", EBaseMatCdShock.names())
				.getResultList()
//				.collect(Collectors.groupingBy(s ->s.getBaseDate(), TreeMap::new, Collectors.toList()))
//				.collect(Collectors.groupingBy(s ->s.getMatCd(), TreeMap::new, Collectors.toList()))
				;
	}	
	
    //TODO:
	public static List<IrCurveHis> getIrCurveListTermStructureForShock(String bssd, String stBssd, String irCurveNm, List<String> tenorList){
		
		String query =" select a from IrCurveHis a " 
					+ "where a.irCurveNm =:irCurveNm "			
					+ "and a.baseDate >= :stBssd "
					+ "and a.baseDate <= :bssd "
					+ "and a.matCd in (:matCdList)"
					+ "order by a.baseDate, a.matCd "
					;
		
		return em.createQuery(query, IrCurveHis.class)
				.setParameter("irCurveNm", irCurveNm)
				.setParameter("stBssd", stBssd)
				.setParameter("bssd", DateUtil.toEndOfMonth(bssd))
				.setParameter("matCdList", tenorList)
				.getResultList()
				;
	}
	
	public static List<IrCurveWeek> getIrCurveWeekListTermStructureForShock(String bssd, String stBssd, String irCurveNm, List<String> tenorList){
		
		String query =" select a from IrCurveWeek a " 
					+ "where a.irCurveNm =:irCurveNm "			
					+ "and a.baseDate >= :stBssd "
					+ "and a.baseDate <= :bssd "
					+ "and a.matCd in (:matCdList)"
					+ "order by a.baseDate, a.matCd "
					;
		
		return em.createQuery(query, IrCurveWeek.class)
				.setParameter("irCurveNm", irCurveNm)
				.setParameter("stBssd", stBssd)
				.setParameter("bssd", DateUtil.toEndOfMonth(bssd))
				.setParameter("matCdList", tenorList)
				.getResultList()
				;
	}
	
	public static String getMaxBaseDate (String bssd, String irCurveNm) {
		String query = "select max(a.baseDate) "
					 + "from IrCurveHis a "
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
	
	public static List<IrCurveHis> getIrCurveHis(String bssd, String irCurveNm, List<String> tenorList){
		String query = "select a from IrCurveHis a "
					 + "where 1=1 "
					 + "and a.irCurveNm =:irCurveNm "
					 + "and a.baseDate  = :bssd	"
					 + "and a.matCd in (:matCdList)"
					 + "order by a.matCd"
					 ;
		
		List<IrCurveHis> curveRst =  em.createQuery(query, IrCurveHis.class)
				.setParameter("irCurveNm", irCurveNm)
				.setParameter("bssd", getMaxBaseDate(bssd, irCurveNm))
				.setParameter("matCdList", tenorList)
				.getResultList();		

		return curveRst;
	}	
}
