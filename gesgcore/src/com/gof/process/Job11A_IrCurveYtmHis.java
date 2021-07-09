package com.gof.process;

import java.time.LocalDateTime;
import java.util.List;

import com.gof.entity.IrCurveHis;
import com.gof.entity.IrCurveYtmHis;
import com.gof.model.hw.SmithWilsonKicsBts;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job11A_IrCurveYtmHis {
	
	public static List<IrCurveHis> createIrCurveHis(String bssd, String irCurveNm, List<IrCurveYtmHis> ytmRst, double alphaApplied, double llp, int freq, double liqPrem) {
		
//		SmithWilsonKicsBts swBts = new SmithWilsonKicsBts(DateUtil.convertFrom(bssd), ytmRst);		
		SmithWilsonKicsBts swBts = new SmithWilsonKicsBts(DateUtil.convertFrom(bssd), ytmRst, alphaApplied, llp, true, freq, liqPrem) ;
		
		List<IrCurveHis> rst = swBts.getSpotBtsRslt();
//		rst.forEach(s -> s.setIrCurveId(irCurveId));
//		rst.forEach(s -> s.setBaseDate(DateUtil.toEndOfMonth(bssd)));	
		
		for(IrCurveHis aa  : rst) {
//			aa.setBaseDate(DateUtil.toEndOfMonth(bssd));
			aa.setBaseDate(bssd);
			aa.setIrCurveNm(irCurveNm);
		}
		
		rst.forEach(s-> log.info("aa :  {}", s.toString()));
		
		log.info("Job11A (IrCurveHis from RF YTM) creates {} results. They are inserted into EAS_IR_CURVE_HIS Table", rst.size());
		
		return rst;
	}
	
	
	public static List<IrCurveHis> createIrCurveHis(String bssd, String irCurveNm, List<IrCurveYtmHis> ytmRst, boolean isRealNumber) {
		
		SmithWilsonKicsBts swBts = new SmithWilsonKicsBts(DateUtil.convertFrom(bssd), ytmRst, isRealNumber);		
		
		List<IrCurveHis> rst = swBts.getSpotBtsRslt();
//		rst.forEach(s -> s.setIrCurveId(irCurveId));
//		rst.forEach(s -> s.setBaseDate(DateUtil.toEndOfMonth(bssd)));	
		
		for(IrCurveHis aa  : rst) {
//			aa.setBaseDate(DateUtil.toEndOfMonth(bssd));
			aa.setBaseDate(bssd);
			aa.setIrCurveNm(irCurveNm);
		}
		
		rst.forEach(s-> log.info("aa :  {}", s.toString()));
		
		log.info("Job11A (IrCurveHis from RF YTM) creates {} results. They are inserted into EAS_IR_CURVE_HIS Table", rst.size());
		
		return rst;
	}
}

