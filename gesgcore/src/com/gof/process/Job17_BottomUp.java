package com.gof.process;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.dao.IrCurveHisDao;
import com.gof.dao.LiqPremDao;
import com.gof.entity.DcntRateBu;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.entity.LiqPremBiz;
import com.gof.enums.ERunSettings;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> IFRS 17 의 BottomUp 방법론에 의한 할인율 산출 모형의 실행         
 *  <p> 시장에서 관측되는 무위험 금리를 기반으로 보험 부채의 비유동성 측면을 반영하여 보험부채에 적용할 할인율 산출함.
 *  <p>    1. 기산출된 무위험 금리 및 유동성 프리미엄 추출   
 *  <p>    2. 기준월의 무위험 시장금리 + 유동성 스프레를 적용하여 기간구조 생성
 *  <p>    3. Smith-Wilson 방법론( {@link SmithWilsonModel} 으로 보간/ 보외를 적용하여 전체 구간의 할인율 산출함.
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job17_BottomUp {

//	private static Map<String, Double> lpMap = new HashMap<String, Double>();
	
	public static Stream<DcntRateBu> createBottomUpAddLiqPremium(String bssd, IrCurve curveMst, String liqModelNm) {
		List<DcntRateBu> rst = new ArrayList<DcntRateBu>();

		Map<String, Double> lpMap = LiqPremDao.getBizLiqPremium(bssd, curveMst.getApplBizDv()).stream()
											.collect(toMap(LiqPremBiz::getMatCd, LiqPremBiz::getLiqPrem));
		
		
		List<IrCurveHis> curveHisList = IrCurveHisDao.getIrCurveHis(bssd, curveMst.getRefCurveId().getIrCurveNm()).stream()
													.filter(s->ERunSettings.TENOR_LIST.getStringList().contains(s.getMatCd()))
													.collect(toList())
													;
		
		log.info("aaa : {},{}", lpMap.size(), curveHisList.size());
		//무위험 금리가 존재하지 않으면 Error 임  
		if(curveHisList.isEmpty()) {
			log.error("Risk Adjust Int Rate Error :  Historical Data of {} is not found at {} ", curveMst.getRefCurveId(), bssd );
			System.exit(0);
		}
		
		return curveHisList.stream().map(s-> addLiqPremium(bssd, curveMst.getIrCurveNm(), s, liqModelNm, lpMap));
	}
	
	private static DcntRateBu addLiqPremium(String bssd, String irCurveNm, IrCurveHis curveHis, String liqModelNm, Map<String, Double> lpMap) {
		double liqPremium = lpMap.getOrDefault(curveHis.getMatCd(), 0.0	);
		log.info("IR_CURVE_NM, LIQ_PREM: {},{},{}", irCurveNm, curveHis, liqPremium);
		DcntRateBu temp;
		
		temp = new DcntRateBu();
		temp.setBaseYymm(bssd);
		temp.setIrCurveNm(irCurveNm);
		temp.setSceNo("0");
		temp.setMatCd(curveHis.getMatCd());
		temp.setRfRate(curveHis.getIntRate());
		temp.setLiqPrem(liqPremium);
		temp.setLiqModelNm(liqModelNm);
		temp.setRiskAdjRfRate(curveHis.getIntRate() + liqPremium);
		temp.setRiskAdjRfFwdRate(0.0);
		
		
		return temp;
	}

//	private static Map<String, Double> getAppliedLiqMap(String bssd, IrCurve curveMst, String modelId) {
//		if(lpMap.isEmpty()) {
//			List<LiqPremium> liqPremList    = LiqPremiumDao.getLiqPremium(bssd, modelId);
//			List<BizLiqPremiumUd> lpUserRst = LiqPremiumDao.getLiqPremiumUd(bssd);
//			
//			if(lpUserRst.isEmpty()) {
//				lpMap = liqPremList.stream().collect(toMap(LiqPremium::getMatCd, LiqPremium::getLiqPrem));
//			}
//			else{
//				lpMap =lpUserRst.stream().collect(toMap(BizLiqPremiumUd::getMatCd, BizLiqPremiumUd::getApplyLiqPrem));
//			}
//			
//			List<String> tenorList = EsgConstant.getTenorList();
//			Collections.reverse(tenorList);
//			
//			double prevLiq =0.0;
//			
//			for(String aa : tenorList) {
//				if(lpMap.containsKey(aa)) {
//					prevLiq = lpMap.get(aa);
//				}
//				else {
//					lpMap.put(aa, prevLiq);
//				}
//			}
//		}
//		return lpMap;
//	}
	
	
//	private static Map<String, Double> getBizLiqPrem(String bssd, String bizDv) {
//		if(lpMap.isEmpty()) {
//			lpMap = LiqPremiumDao.getBizLiqPremium(bssd, bizDv).stream().collect(toMap(BizLiqPremium::getMatCd, BizLiqPremium::getApplyLiqPrem));
//		}
//		return lpMap;
//	}
	
	
	
}
