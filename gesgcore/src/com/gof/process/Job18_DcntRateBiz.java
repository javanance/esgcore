package com.gof.process;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.gof.dao.DcntRateDao;
import com.gof.entity.DcntRateBiz;
import com.gof.entity.DcntRateBu;
import com.gof.entity.IrCurve;
import com.gof.enums.ERunSettings;
import com.gof.model.hw.SmithWilsonKics;
import com.gof.model.hw.SmithWilsonRslt;
import com.gof.util.DateUtil;

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
public class Job18_DcntRateBiz {

	public static Stream<DcntRateBiz> createBizBottomUpCurve(String bssd, IrCurve curveMst) {
		List<String> tenorList = ERunSettings.TENOR_LIST.getStringList();
		
		List<SmithWilsonRslt> swRf 		= new ArrayList<SmithWilsonRslt>();
		List<SmithWilsonRslt> swRfAdj 	= new ArrayList<SmithWilsonRslt>();
		
		
		Map<String, Double> curveRfMap = DcntRateDao.getTermStructure(bssd, curveMst.getIrCurveNm()).stream()
														.filter(s-> tenorList.contains(s.getMatCd()))
														.collect(toMap(DcntRateBu::getMatCd, DcntRateBu::getRfRate));
		
		swRf = createFullCurveBySw(bssd, "0", curveMst, curveRfMap);
		
		Map<String, Double> curveRfAdjMap = DcntRateDao.getTermStructure(bssd, curveMst.getIrCurveNm()).stream()
														.filter(s-> tenorList.contains(s.getMatCd()))
														.collect(toMap(DcntRateBu::getMatCd, DcntRateBu::getRiskAdjRfRate));
		
		swRfAdj = createFullCurveBySw(bssd, "0", curveMst, curveRfAdjMap);
		
		
		List<DcntRateBiz> rst = merge(bssd, curveMst, swRfAdj, swRf);
		log.info("Job18(Biz Bottom Up Ir Rate Calculation) creates {} results for {}. inserted into DCNT_RATE_BOTTOMUP", curveMst.getIrCurveNm(), rst.size());
		return rst.stream();
	}
	
	private static List<DcntRateBiz> merge(String bssd, IrCurve curveMst, List<SmithWilsonRslt> rfAdjCurve,List<SmithWilsonRslt> rfCurve) {
		List<DcntRateBiz>  rst = new ArrayList<DcntRateBiz>();
		DcntRateBiz temp ;
		String irCurveNm = curveMst.getIrCurveNm();
		String bizDv =curveMst.getApplBizDv();
		
		Map<String, Double> rfMap = rfCurve.stream().collect(toMap(SmithWilsonRslt::getMatCd, SmithWilsonRslt::getSpotDisc));
		
		for(SmithWilsonRslt aa : rfAdjCurve) {
			double rfRate = rfMap.get(aa.getMatCd());
			double liqPrem = aa.getSpotDisc() - rfRate;
			temp = new DcntRateBiz(bssd, bizDv, irCurveNm, aa.getMatCd(), rfRate, 0.0, liqPrem, aa.getSpotDisc(), aa.getFwdDisc());
			rst.add(temp);
		}
		return rst;
	}
	
	
	private static List<SmithWilsonRslt> createFullCurveBySw(String bssd, String sceNo, IrCurve curveMst, Map<String, Double> curveMap) {
		char  cmpdType		= 'D';
		int   prjInterval 	= 1;
		int   dayCountBasis = 9;
		
		LocalDate baseDate 	= DateUtil.convertFrom(bssd);
		double ufr 			= ERunSettings.UFR_MAP.getDoubleMap().get(curveMst.getCurCd());
		int    ufrt 		= ERunSettings.UFRT_MAP.getIntMap().get(curveMst.getCurCd());
		int    projYear 	= ERunSettings.PROJECTION_YEAR.getIntValue();
		
		Map<Double, Double> ts = new TreeMap<Double, Double>();
		for(Map.Entry<String, Double> entry : curveMap.entrySet()) {
			ts.put(Double.valueOf(entry.getKey().split("M")[1]), entry.getValue());
		}
		
		SmithWilsonKics sw  = new SmithWilsonKics(baseDate, ts, cmpdType, true, ufr, ufrt, projYear, prjInterval, 100, dayCountBasis);			
		return  sw.getSmithWilsonResultList();		
	}

}
