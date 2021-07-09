package com.gof.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gof.dao.IrCurveHisDao;
import com.gof.entity.IrCurveHis;
import com.gof.entity.LiqPremHis;
import com.gof.enums.ELiqModel;
import com.gof.enums.ERunSettings;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> 유동성 프리미엄 산출 모형          
 *  <p> BottomUp 기준의 할인율 산출시 보험부채의 비유동성 특성을 반영하여 무위험 금리에 가산한 유동성 스프레드를 산출함.
 *  <p>    1. 보험부채의 비유동성 특성을 반영할 Proxy 대상 선정 (산금채) 
 *  <p>    2. Proxy 상품(산금채)와 무위험 금리의 과거 스프레드 추출 
 *  <p>	     2.1  시장에서 관측된 스프레드는 유동성 프리미엄 뿐만 아니라 채권의 개별리스크  및 시장의 Noise 가 포함되어 있음.  
 *  <p>	     2.2  개별요인으로 인한 스프레드는 장기적으로 0 으로 수렴하므로 시장 관측된 스프레드의 장기 평균을 적용함.
 *  <p>    3. Proxy 상품의 스프레드의 36개월 이동 평균을 이용하여 만기별 유동성 프리미엄 이력을 산출함. 
 *  <p>    4. 유동성 프리미엄의 기본특성 ( 미관측된 기간 즉, LLP 이후 기간의 유동성 프리미엄은 0이어야 하고, 최단 만기에서 ( 이론적인 초단기임)도 유동성 프리미엄은 0 임) 을 이용하여 
 *  <p>    4.1  관측된 과거 유동성 프리미엄을 Curve Fitting 함.
 *  <p>    5. 관측된 유동성 프리미엄을 Curve Fitting 으로 보정한 최종적인 유동성 프리미엄 산출
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job15_LiquidPremium {

	public static List<LiqPremHis> createLiquidPremiumFrom(String bssd, String liqModelNm, double volAdj) {
		List<LiqPremHis> liqPremList = new ArrayList<LiqPremHis>();
		List<String> tenorList = ERunSettings.TENOR_LIST.getStringList();
		LiqPremHis temp ;
		for(String tenor : tenorList) {
			temp = new LiqPremHis();
			temp.setBaseYymm(bssd);
			temp.setLiqModelNm(liqModelNm);
			temp.setMatCd(tenor);
			temp.setLiqPrem(volAdj);
			
			liqPremList.add(temp);
		}
		return liqPremList;
	}
	
	public static List<LiqPremHis> createLiquidPremium(String bssd, String liqModelNm) {
		List<LiqPremHis> liqPremList = new ArrayList<LiqPremHis>();
		List<IrCurveHis> ratioSpreadList = new ArrayList<IrCurveHis>();
		IrCurveHis spreadTemp ;
		
		int avgMonNum 		  = ERunSettings.LIQ_AVG_NUM.getIntValue();
		String lqKtbIrCruveId = ERunSettings.LIQ_KTB_CURVE_NM.getStringValue();
		String lqKdbIrCurveId = ERunSettings.LIQ_KDB_CURVE_NM.getStringValue();
		
		String stBssd = DateUtil.addMonthToString(bssd, avgMonNum);
		
		List<IrCurveHis> ktbList = IrCurveHisDao.getCurveHisBetween(bssd, stBssd, lqKtbIrCruveId);
		List<IrCurveHis> kdbList = IrCurveHisDao.getCurveHisBetween(bssd, stBssd, lqKdbIrCurveId);
		
		if(ktbList.size()==0 || kdbList.size()==0) {
			return new ArrayList<LiqPremHis>();
		}
		
		Map<String, Double> ktbMap = ktbList.stream()
//											.filter(s-> ELiqPremiumMatCd.contains(s.getMatCd()))
											.filter(s-> ERunSettings.TENOR_LIST.getStringList().contains(s.getMatCd()))	
											.collect(Collectors.toMap(s -> s.getBaseDate() + "#" +s.getMatCd() , s ->s.getIntRate()));
		Map<String, Double> kdbMap = kdbList.stream()
//											.filter(s-> ELiqPremiumMatCd.contains(s.getMatCd()))							
											.filter(s-> ERunSettings.TENOR_LIST.getStringList().contains(s.getMatCd()))								
											.collect(Collectors.toMap(s -> s.getBaseDate() + "#" +s.getMatCd() , s ->s.getIntRate()));

		double ktbRate =1.0;
		double kdbRate =1.0;
		
		List<LiqPremHis> tempLiqRatioList = new ArrayList<LiqPremHis>();
		
		for(Map.Entry<String, Double> entry: ktbMap.entrySet()) {
			if(kdbMap.containsKey(entry.getKey())) {
				ktbRate = entry.getValue();
				kdbRate = kdbMap.get(entry.getKey());
				spreadTemp = new IrCurveHis(entry.getKey().split("#")[0], entry.getKey().split("#")[1], ktbRate==0? 1: kdbRate/ktbRate );
				ratioSpreadList.add(spreadTemp);
			}
		}
		
		Map<String, List<IrCurveHis>> spreadMap  = ratioSpreadList.stream().collect(Collectors.groupingBy(s ->s.getMatCd(), Collectors.toList()));
		
		int cnt =0;
		double sumRate =0.0;
		double curRate =0.0;
		String maxBssd = "";
		
		for(Map.Entry<String, List<IrCurveHis>> entry : spreadMap.entrySet()) {
			sumRate=0.0;
			cnt = 0 ;
			
			for(IrCurveHis aa : entry.getValue()) {
				cnt = cnt+1;
				sumRate= sumRate + aa.getIntRate();		
				if(aa.getBaseDate().compareTo(maxBssd) > 0) {
					maxBssd = aa.getBaseDate();
				}
			}	
			curRate = ktbMap.getOrDefault( maxBssd + "#" +entry.getKey() , 1.0);
			
			liqPremList.add(new LiqPremHis(bssd, liqModelNm, entry.getKey(), curRate * (sumRate/cnt -1) ));
		}
		
//		logger.info("liq : {}", liqCurveList.size());
		liqPremList.forEach(s -> log.info("Average Liquidity Premium of {} during past {} month  : {}" , s.getMatCd(), -1.0* avgMonNum, s.getIntRate()));


		
		log.info("Job15(Liquid Premium Calculation) creates  {} results.  They are inserted into EAS_LIQ_PREM Table", liqPremList.size());
		liqPremList.stream().forEach(s->log.debug("Liquidity Premium Result : {}", s.toString()));
		return liqPremList;
	}
	
	
	public static List<LiqPremHis> createLiquidPremiumEom(String bssd, String liqModelNm) {
		List<LiqPremHis> liqPremList = new ArrayList<LiqPremHis>();
		LiqPremHis spreadTemp ;
		
		String lqKtbIrCruveId = ERunSettings.LIQ_KTB_CURVE_NM.getStringValue();
		String lqKdbIrCurveId = ERunSettings.LIQ_KDB_CURVE_NM.getStringValue();
		
		
		String eomDate = IrCurveHisDao.getEomDate(bssd, lqKtbIrCruveId);
		
		List<IrCurveHis> ktbList = IrCurveHisDao.getIrCurveHis(eomDate, lqKtbIrCruveId);
		List<IrCurveHis> kdbList = IrCurveHisDao.getIrCurveHis(eomDate, lqKdbIrCurveId);
		
		
		if(ktbList.size()==0 || kdbList.size()==0) {
			return new ArrayList<LiqPremHis>();
		}
		
		Map<String, Double> ktbMap = ktbList.stream()
											.filter(s-> ERunSettings.TENOR_LIST.getStringList().contains(s.getMatCd()))
											.collect(Collectors.toMap(s -> s.getBaseDate() + "#" +s.getMatCd() , s ->s.getIntRate()));
		Map<String, Double> kdbMap = kdbList.stream()
											.filter(s-> ERunSettings.TENOR_LIST.getStringList().contains(s.getMatCd()))							
											.collect(Collectors.toMap(s -> s.getBaseDate() + "#" +s.getMatCd() , s ->s.getIntRate()));

		double ktbRate =1.0;
		double kdbRate =1.0;

		
		for(Map.Entry<String, Double> entry: ktbMap.entrySet()) {
			if(kdbMap.containsKey(entry.getKey())) {
				ktbRate = entry.getValue();
				kdbRate = kdbMap.get(entry.getKey());
				spreadTemp = new LiqPremHis(bssd, liqModelNm,  entry.getKey().split("#")[1], kdbRate - ktbRate );
				liqPremList.add(spreadTemp);
			}
		}
		
		
		log.info("Job15(Liquid Premium Calculation) creates  {} results.  They are inserted into EAS_LIQ_PREM Table", liqPremList.size());
		liqPremList.stream().forEach(s->log.debug("Liquidity Premium Result : {}", s.toString()));
		return liqPremList;
	}
	
	public static List<LiqPremHis> createLiquidPremiumEom(String bssd, ELiqModel liqModel) {
		List<LiqPremHis> liqPremList = new ArrayList<LiqPremHis>();
		LiqPremHis spreadTemp ;
		
		String lqKtbIrCruveId = ERunSettings.LIQ_KTB_CURVE_NM.getStringValue();
		String lqKdbIrCurveId = ERunSettings.LIQ_KDB_CURVE_NM.getStringValue();
		
		
		String eomDate = IrCurveHisDao.getEomDate(bssd, lqKtbIrCruveId);
		
		List<IrCurveHis> ktbList = IrCurveHisDao.getIrCurveHis(eomDate, lqKtbIrCruveId);
		List<IrCurveHis> kdbList = IrCurveHisDao.getIrCurveHis(eomDate, lqKdbIrCurveId);
		
		
		if(ktbList.size()==0 || kdbList.size()==0) {
			return new ArrayList<LiqPremHis>();
		}
		
		Map<String, Double> ktbMap = ktbList.stream()
											.filter(s-> ERunSettings.TENOR_LIST.getStringList().contains(s.getMatCd()))
											.collect(Collectors.toMap(s -> s.getBaseDate() + "#" +s.getMatCd() , s ->s.getIntRate()));
		Map<String, Double> kdbMap = kdbList.stream()
											.filter(s-> ERunSettings.TENOR_LIST.getStringList().contains(s.getMatCd()))							
											.collect(Collectors.toMap(s -> s.getBaseDate() + "#" +s.getMatCd() , s ->s.getIntRate()));

		double ktbRate =1.0;
		double kdbRate =1.0;

		
		for(Map.Entry<String, Double> entry: ktbMap.entrySet()) {
			if(kdbMap.containsKey(entry.getKey())) {
				ktbRate = entry.getValue();
				kdbRate = kdbMap.get(entry.getKey());
				
				spreadTemp = new LiqPremHis(bssd, liqModel.name(),  entry.getKey().split("#")[1], liqModel.getSpreadOper().apply(kdbRate, ktbRate) );
				liqPremList.add(spreadTemp);
			}
		}
		
		log.info("Job15(Liquid Premium Calculation) creates  {} results.  They are inserted into EAS_LIQ_PREM Table", liqPremList.size());
		liqPremList.stream().forEach(s->log.debug("Liquidity Premium Result : {}", s.toString()));
		return liqPremList;
	}
}
