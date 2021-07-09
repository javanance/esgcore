package com.gof.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.enums.EBoolean;
import com.gof.infra.EntityManagerUtil;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;


/**
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class IrCurveHisDao {
//	private static EntityManager em = Persistence.createEntityManagerFactory("ociarazor").createEntityManager();
	private static EntityManager em = EntityManagerUtil.getEntityManger();

	public static IrCurve getIrCurve(String curveId) {
		String query = "select a from IrCurve a where a.irCurveNm =:id ";
		return   em.createQuery(query, IrCurve.class)
						.setParameter("id",curveId )			
						.getSingleResult();
	}
	
	public static List<IrCurve> getRiskFreeIrCurve(){
		String query = "select a from IrCurve a "
					 + "where 1=1 "
					 + "and a.creditGrate = :crdGrdCd "
					 + "and a.useYn = :useYn "
					 ;
		
		return   em.createQuery(query, IrCurve.class)
						.setParameter("crdGrdCd","RF" )				
						.setParameter("useYn", EBoolean.Y )			
						.getResultList();
	}

	public static List<IrCurve> getIrCurveByCrdGrdCd(String crdCrdCd){
		String query = "select a from IrCurve a "
					 + "where 1=1 "
					 + "and a.creditGrate = :crdGrdCd "
					 + "and a.useYn		  = :useYn "
					 + "and a.applMethDv  <> '6' "					
					 + "and a.refCurveId  is null "
					 ;
		
		return   em.createQuery(query, IrCurve.class)
								 .setParameter("crdGrdCd",crdCrdCd )				
								 .setParameter("useYn", EBoolean.Y )				
								 .getResultList();
								 
	}
	
	public static List<IrCurve> getBottomUpIrCurve(){
		return getIrCurveByGenMethod("4");
	}
	
	public static List<IrCurve> getIrCurveByGenMethod(String applMethDv){
		String query = "select a from IrCurve a "
					 + "where 1=1 "
					 + "and a.applMethDv = :applMethDv "
					 + "and a.useYn = :useYn"
					 ;
		
		return   em.createQuery(query, IrCurve.class)
								 .setParameter("applMethDv",applMethDv)			// Bond Gen : 3, BottomUp : 4 , TopDown : 6, KICS : 5 SwapRate : 7
								 .setParameter("useYn", EBoolean.Y)				
								 .getResultList();
	}
	
	public static Map<String, String> getEomMap(String bssd, String irCurveNm){
		String query = "select substring(a.baseDate, 0,6), max(a.baseDate) "
					 + "from IrCurveHis a "
					 + "where 1=1 "
					 + "and a.irCurveNm = :irCurveNm "
					 + "and a.baseDate <= :bssd	"
					 + "group by substring(a.baseDate, 0,6)"
					 ;
		
		List<Object[]> maxDate =  em.createQuery(query)
				 				 .setParameter("irCurveNm", irCurveNm)			
								 .setParameter("bssd", DateUtil.toEndOfMonth(bssd))
								 .getResultList();
//		if(maxDate == null) {
//			log.warn("IR Curve History Data is not found {} at {}!!!" , irCurveNm, FinUtils.toEndOfMonth(bssd));
//			return new hashMap<String, String>;
//		}
		
		Map<String, String> rstMap = new HashMap<String, String>();
		for(Object[] aa : maxDate) {
			rstMap.put(aa[0].toString(), aa[1].toString());
		}
		return rstMap;
	}
	
	public static String getEomDate (String bssd, String irCurveNm) {
		String query = "select max(a.baseDate) "
				 + "from IrCurveHis a "
				 + "where 1=1 "
				 + "and a.irCurveNm = :irCurveNm "
				 + "and a.baseDate >= :bom	"
				 + "and a.baseDate <= :eom	"
				 ;
		Object maxDate =  em.createQuery(query)
				 				 .setParameter("irCurveNm", irCurveNm)			
				 				 .setParameter("bom", bssd)
								 .setParameter("eom", DateUtil.toEndOfMonth(bssd))
								 .getSingleResult();
		if(maxDate==null) {
			log.warn("IR Curve History Data is not found {} at {}!!!" , irCurveNm, bssd);
			return bssd;
		}
		return maxDate.toString();
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
	
	public static List<IrCurveHis> getIrCurveHis(String bssd, String irCurveNm){
		String query = "select a from IrCurveHis a "
					 + "where 1=1 "
					 + "and a.irCurveNm =:irCurveNm "
					 + "and a.baseDate  = :bssd	"
					 + "order by a.matCd"
					 ;
		
		List<IrCurveHis> curveRst =  em.createQuery(query, IrCurveHis.class)
				.setParameter("irCurveNm", irCurveNm)
				.setParameter("bssd", getMaxBaseDate(bssd, irCurveNm))
				.getResultList();
		
//		log.info("maxDate : {}, curveSize : {}", getMaxBaseDate(bssd, irCurveNm),curveRst.size());
		return curveRst;
	}
	
	/** 
	*  <p> 
	*  @param bssd 	 
	*  @param stBssd  
	*  @param curveId  
	*  @return		                     
	*/
	public static List<IrCurveHis> getCurveHisBetween(String bssd, String stBssd, String curveId){
		String query = "select a from IrCurveHis a "
				+ "where 1=1 "
				+ "and a.baseDate <= :bssd	"
				+ "and a.baseDate >= :stBssd "
				+ "and a.irCurveNm =:param1 "
//				+ "and a.matCd not in (:matCd1, :matCd2, :matCd3) "
				+ "order by a.baseDate"
				;
		
		List<IrCurveHis> curveRst =  em.createQuery(query, IrCurveHis.class)
				.setParameter("param1", curveId)
				.setParameter("bssd", DateUtil.addMonth(bssd, 1))
				.setParameter("stBssd", stBssd)
//				.setParameter("matCd1", "M0018")
//				.setParameter("matCd2", "M0030")
//				.setParameter("matCd3", "M0048")
				.getResultList();		
		
//		Map<String, Map<String, IrCurveHis>> curveMap = curveRst.stream().collect(Collectors.groupingBy(s -> s.getMatCd()
//				, Collectors.toMap(s-> s.getBaseYymm(), Function.identity(), (s,u)->u)));
//		curveMap.entrySet().forEach(s -> log.info("aaa : {},{},{}", s.getKey(), s.getValue()));
		return curveRst;
	}
	
	
	public static List<IrCurveHis> getShortRateBtw(String stBssd, String bssd, String curveId){
		String query = "select a from IrCurveHis a "
				+ "where 1=1 "
				+ "and a.baseDate <= :bssd	"
				+ "and a.baseDate >= :stBssd "
				+ "and a.irCurveNm =:param1 "
				+ "and a.matCd = :matCd "
				+ "order by a.baseDate desc"
				;
		
		List<IrCurveHis> curveRst =  em.createQuery(query, IrCurveHis.class)
				.setParameter("param1", curveId)
				.setParameter("stBssd", stBssd+"01")
				.setParameter("bssd", bssd+"31")
				.setParameter("matCd", "M0003")
				.getResultList();		
		
//		Map<String, Map<String, IrCurveHis>> curveMap = curveRst.stream().collect(Collectors.groupingBy(s -> s.getMatCd()
//				, Collectors.toMap(s-> s.getBaseYymm(), Function.identity(), (s,u)->u)));
//		curveMap.entrySet().forEach(s -> log.info("aaa : {},{},{}", s.getKey(), s.getValue()));
		return curveRst;
	}
	
	
	public static IrCurveHis getShortRateHis(String bssd, String curveId){
		String query = "select a from IrCurveHis a "
				+ "where 1=1 "
				+ "and a.baseDate = :bssd	"
				+ "and a.irCurveNm =:param1 "
				+ "and a.matCd = :matCd "
				+ "order by a.baseDate"
				;
		
		IrCurveHis curveRst =  em.createQuery(query, IrCurveHis.class)
				.setParameter("param1", curveId)
				.setParameter("bssd", getMaxBaseDate(bssd, curveId))
				.setParameter("matCd", "M0003")
				.getSingleResult()
				;		
		

		return curveRst;
	}
	
	public static List<IrCurveHis> getIrCurveHisByMaturityHis(String bssd, int monthNum, String irCurveNm,String matCd){
		String query = "select a from IrCurveHis a "
					 + "where 1=1 "
					 + "and a.irCurveNm =:param1 "
					 + "and a.baseDate >=:stBssd "
					 + "and a.baseDate <=:bssd "
					 + "and a.matCd =:param2 ";
					 ;
		
		return   em.createQuery(query, IrCurveHis.class)
				.setParameter("param1", irCurveNm)
				.setParameter("stBssd", DateUtil.addMonth(bssd, monthNum)+"01")
				.setParameter("bssd", bssd+"31")
				.setParameter("param2", matCd)				
				.getResultList();
	}
	
	/** 
	*  @param bssd 	 
	*  @param matCd1   
	*  @param matCd2   
	*  @return		                     
	*/
	public static List<IrCurveHis> getKTBMaturityHis(String bssd, String matCds){
//	public static List<IrCurveHis> getKTBMaturityHis(String bssd, String matCd1, String matCd2){
		String matCd1 = matCds.split(",")[0].trim();
		String matCd2 ="";
		if(matCds.split(",").length==2) {
			matCd2 =matCds.split(",")[1].trim();
		}
		
		String query = 	"select new com.gof.entity.IrCurveHis (substr(a.baseDate,1,6), a.matCd, avg(a.intRate)) "
					+ "from IrCurveHis a "
					+ "where 1=1 "
					+ "and a.baseDate <= :bssd	"
					+ "and a.irCurveNm =:param1 "
					+ "and a.matCd in (:param2, :param3) "
					+ "group by substr(a.baseDate,1,6), a.matCd "
					;
		
		List<IrCurveHis> curveRst =  em.createQuery(query, IrCurveHis.class)
				.setParameter("param1", "A100")
				.setParameter("param2", matCd1)
				.setParameter("param3", matCd2)
				.setParameter("bssd", DateUtil.addMonth(bssd, 1))
				.getResultList();		
		return curveRst;
	}
	
	public static List<IrCurveHis> getKTBMaturityHis(String bssd, String matCd1, String matCd2){
			
			String query = "select new com.gof.entity.IrCurveHis (substr(a.baseDate,1,6), a.matCd, avg(a.intRate)) "
						+ "from IrCurveHis a "
						+ "where 1=1 "
						+ "and a.baseDate <= :bssd	"
						+ "and a.irCurveNm =:param1 "
						+ "and a.matCd in (:param2, :param3) "
						+ "group by substr(a.baseDate,1,6), a.matCd "
						;
			
			List<IrCurveHis> curveRst =  em.createQuery(query, IrCurveHis.class)
					.setParameter("param1", "A100")
					.setParameter("param2", matCd1)
					.setParameter("param3", matCd2)
					.setParameter("bssd", DateUtil.addMonth(bssd, 1))
					.getResultList();		
			return curveRst;
	}
	
	
	/** 
	*  @param bssd 	 
	*  @param stBssd 
	*  @param irCurveNm
	*  @return		                     
	*/
//	public static Map<String, List<IrCurveHis>> getIrCurveListTermStructure(String bssd, String stBssd, String irCurveNm){
//		String query =" select a from IrCurveHis a " 
//					+ "where a.irCurveNm =:irCurveNm "			
//					+ "and a.baseDate >= :stBssd "
//					+ "and a.baseDate <= :bssd "
//					+ "and a.matCd in (:matCdList)"
//					+ "order by a.baseDate, a.matCd "
//					;
//		
//		return em.createQuery(query, IrCurveHis.class)
//				.setParameter("irCurveNm", irCurveNm)
//				.setParameter("stBssd", stBssd)
//				.setParameter("bssd", DateUtil.toEndOfMonth(bssd))
//				.setParameter("matCdList", EBaseMatCd.names())
//				.stream()
////				.collect(Collectors.groupingBy(s ->s.getBaseDate(), TreeMap::new, Collectors.toList()))
//				.collect(Collectors.groupingBy(s ->s.getMatCd(), TreeMap::new, Collectors.toList()))
//				;
//	}
	
//	public static List<BizIrCurveHis> getBizIrCurveHis(String bssd, String bizDv, String irCurveNm){
//		String query ="select a from BizIrCurveHis a " 
//					+ "where a.irCurveNm =:irCurveNm "			
//					+ "and a.baseYymm = :bssd "
//					+ "and a.applBizDv = :bizDv "
//					+ "order by a.matCd"
//				;
//		
//		return em.createQuery(query, BizIrCurveHis.class)
//				.setParameter("irCurveNm", irCurveNm)
//				.setParameter("bssd", bssd)
//				.setParameter("bizDv", bizDv)
//				.getResultList()
//				;
//	}
//
//	public static List<IrSce> getIrCurveSce(String bssd, String irCurveNm, String sceNo){
//		String query ="select a from IrSce a " 
//					+ "where a.irCurveNm =:irCurveNm "			
//					+ "and a.baseDate = :bssd "
//					+ "and a.sceNo = :sceNo "
//				;
//		
//		return em.createQuery(query, IrSce.class)
//				.setParameter("irCurveNm", irCurveNm)
//				.setParameter("bssd", bssd)
//				.setParameter("sceNo", sceNo)
//				.getResultList()
//				;
//	}
//
//	public static List<IrSce> getIrCurveSce(String bssd, String irCurveNm){
//		String query ="select a from IrSce a " 
//					+ "where a.irCurveNm =:irCurveNm "			
//					+ "and a.baseDate = :bssd "
//				;
//		
//		return em.createQuery(query, IrSce.class)
//				.setParameter("irCurveNm", irCurveNm)
//				.setParameter("bssd", bssd)
//				.setHint(QueryHints.HINT_READONLY, true)        //TODO : Check performance!!!!
//				.getResultList()
//				;
//	}
//
//	public static List<BizIrCurveSce> getBizIrCurveSce(String bssd, String bizDv, String irCurveNm, String sceNo){
//		String query ="select a from BizIrCurveSce a " 
//					+ "where a.irCurveNm =:irCurveNm "		
//					+ "and a.baseYymm = :bssd "
//					+ "and a.applBizDv = :bizDv "
//					+ "and a.sceNo = :sceNo "
//				;
//		
//		return em.createQuery(query, BizIrCurveSce.class)
//				.setParameter("irCurveNm", irCurveNm)
//				.setParameter("bssd", bssd)
//				.setParameter("bizDv", bizDv)
//				.setParameter("sceNo", sceNo)
//				.getResultList()
//				;
//	}	
//	
//
//	public static List<BizIrCurveSce> getBizIrCurveSce(String bssd, String bizDv, String irCurveNm){
//		String query ="select a from BizIrCurveSce a " 
//					+ "where 1=1 "
//					+ "and a.baseYymm = :bssd "
//					+ "and a.applBizDv = :bizDv "
//					+ "and a.irCurveNm =:irCurveNm"
//				;
//		
//		return em.createQuery(query, BizIrCurveSce.class)
//				.setParameter("bssd", bssd)
//				.setParameter("bizDv", bizDv)
//				.setParameter("irCurveNm", irCurveNm)
//				.getResultList()
//				;
//	}
//
//	public static List<IrCurveHis> getEomTimeSeries(String bssd, String irCurveNm, String matCd, int monNum){
//		Collection<String> eomList = getEomMap(bssd, irCurveNm).values(); 
//
//		String query = "select a from IrCurveHis a "
//				 	 + "where a.irCurveNm = :irCurveNm "
//				 	 + "and a.baseDate > :stBssd "
//				 	 + "and a.baseDate < :bssd "
//				 	 + "and a.baseDate in :eomList "
//				 	 + "and a.matCd = :matCd "
//				 	 + "order by a.baseDate desc "
//				 	 ;
//		
//		return   em.createQuery(query, IrCurveHis.class)
//				 .setParameter("bssd", DateUtil.toEndOfMonth(bssd))			
//				 .setParameter("stBssd", DateUtil.toEndOfMonth( DateUtil.addMonth(bssd, monNum)))				
//				 .setParameter("irCurveNm", irCurveNm)				
//				 .setParameter("matCd", matCd)				
//				 .setParameter("eomList", eomList)	
//				 .getResultList();
//	}
	
	
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
