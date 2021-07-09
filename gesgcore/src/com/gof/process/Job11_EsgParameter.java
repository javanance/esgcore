package com.gof.process;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.gof.entity.EsgMst;
import com.gof.entity.EsgParamHis;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrCurveYtmHis;
import com.gof.entity.SwaptionVol;
import com.gof.model.hw.Hw1fCalibrationKics;
import com.gof.model.hw.SmithWilsonKicsBts;
import com.gof.model.hw.SwpnVolInfo;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job11_EsgParameter {	
	
//	SmithWilsonKicsBts swBts = new SmithWilsonKicsBts(DateUtil.convertFrom(bssd), ytmRst);		
//	swBts.getSpotBtsRslt().forEach(s -> log.info("{}, {}", s.getMatCd(), s.getIntRate()));		

	
//	public static List<EsgParamHis> createHwParamCalcHisAsync(String bssd, List<IrCurveHis> curveRst , List<SwaptionVol> volRst, double ufr, double ufrt, double errorTolerance) {
//		log.info("ESG Parameter for HW :  Thread Name: {}", Thread.currentThread().getName());
//		List<EsgParamHis> rst ;
//		HullWhiteParameter hullWhiteParameter = new HullWhiteParameter(curveRst, volRst, ufr, ufrt);
//		rst = hullWhiteParameter.getParamCalcHis(bssd, "4", errorTolerance);
//		
//		log.info("Job11 (Historical Hull White Parameter) creates {} results. They are inserted into EAS_PARAM_CALC_HIS Table", rst.size());
//		
//		return rst;
//	}

//	ADD : 20210629
	public static List<EsgParamHis> createHwKicsParamCalcHisAsyncFromYtm(String bssd, List<IrCurveYtmHis> ytmRst , List<SwaptionVol> swapVolRst, double ufr, double ufrt, double errorTolerance) {
		log.info("ESG Parameter for HW :  Thread Name: {}", Thread.currentThread().getName());

		int projYear = 100;
		String irModelType = "4";
		String paramCalcCd = "SIGMA_LOCAL_CALIB";

		int[]    alphaPiece = new int[] {10} ;
		int[]    sigmaPiece = new int[] {1, 2, 3, 5, 7, 10};
		double[] initParas  = new double[] {0.03, 0.06, 0.007, 0.006, 0.005, 0.004, 0.005, 0.006};		
		
		List<SwpnVolInfo> volRst  = swapVolRst.stream().map(s-> SwpnVolInfo.convertFrom(s)).collect(toList());
		
		SmithWilsonKicsBts swBts = new SmithWilsonKicsBts(DateUtil.convertFrom(bssd), ytmRst);		
		swBts.getSpotBtsRslt().forEach(s -> log.info("{}, {}", s.getMatCd(), s.getIntRate()));		
		
		Hw1fCalibrationKics calib = new Hw1fCalibrationKics(bssd, swBts.getSpotBtsRslt(), volRst, alphaPiece, sigmaPiece, initParas, projYear, errorTolerance);		
		List<EsgParamHis> rst = calib.getHw1fCalibrationResultList().stream().map(s->s.convert(irModelType, paramCalcCd))
																			  .flatMap(s-> s.stream())
																			  .collect(toList());

		rst.forEach(s-> log.info("aa :  {}", s.toString()));
		
		log.info("Job11 (Historical Hull White Parameter for Kics) creates {} results. They are inserted into EAS_PARAM_CALC_HIS Table", rst.size());
		
		return rst;
	}	

//	ADD : 20210621
	public static List<EsgParamHis> createHwKicsParamCalcHisAsync(String bssd, List<IrCurveHis> curveRst , List<SwaptionVol> swapVolRst, double ufr, double ufrt, double errorTolerance, EsgMst mst) {
		log.info("ESG Parameter for HW :  Thread Name: {}", Thread.currentThread().getName());

		int projYear = 100;
//		String irModelId = mst.getIrModelId();
//		String irModelType = mst.getIrModelTyp();
//		String paramCalcCd = mst.getParamApplCd();
//		String paramCalcCd = "SIGMA_LOCAL_CALIB";

		int[]    alphaPiece = new int[] {10} ;
		int[]    sigmaPiece = new int[] {1, 2, 3, 5, 7, 10};
		double[] initParas  = new double[] {0.03, 0.06, 0.007, 0.006, 0.005, 0.004, 0.005, 0.006};		
		
		List<SwpnVolInfo> volRst  = swapVolRst.stream().map(s-> SwpnVolInfo.convertFrom(s)).collect(toList());
		
		
		Hw1fCalibrationKics calib = new Hw1fCalibrationKics(bssd, curveRst, volRst, alphaPiece, sigmaPiece, initParas, projYear, errorTolerance);		
		List<EsgParamHis> rst = calib.getHw1fCalibrationResultList().stream().map(s->s.convert(mst))
																			  .flatMap(s-> s.stream())
																			  .collect(toList());

		rst.forEach(s-> log.info("aa :  {}", s.toString()));
		
		log.info("Job11 (Historical Hull White Parameter for Kics) creates {} results. They are inserted into EAS_PARAM_CALC_HIS Table", rst.size());
		
		return rst;
	}	
	

}