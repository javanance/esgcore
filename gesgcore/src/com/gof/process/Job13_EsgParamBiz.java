package com.gof.process;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.dao.EsgParamDao;
import com.gof.entity.EsgParamBiz;
import com.gof.entity.EsgMst;
import com.gof.entity.EsgParamHis;
import com.gof.entity.EsgParamUd;
import com.gof.enums.ERunSettings;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
public class Job13_EsgParamBiz {
	
	private final static Logger logger = LoggerFactory.getLogger(Job13_EsgParamBiz.class);
	
	/**
	 *  @param bssd 
	 *  @return  
	*/
		
	public static List<EsgParamBiz> createBizAppliedParameter(String bssd, String bizDv, String irModelId ) {
		List<EsgParamBiz> bizApplied = new ArrayList<EsgParamBiz>();
		List<EsgParamUd> userParam = EsgParamDao.getEsgParamUd(bssd, irModelId);
		
		if(userParam.isEmpty()) {
			bizApplied =calculateBizAppliedParameter(bssd, bizDv, irModelId);
			logger.info("Job13 (Setting Biz Applied Parameter ) : Biz Applied parameter is used with calucated parameters");
		}
		else {
			bizApplied = userParam.stream().map(s -> s.convertToBizEsgParam(bssd, bizDv)).collect(Collectors.toList());
			logger.info("Job13 (Setting Biz Applied Parameter ) : Biz Applied parameter is used with user Defined parameters");
		}

		logger.info("Job13 (Setting Biz Applied Parameter ) creates {} resutls. They are inserted into EAS_BIZ_APLY_PARAM Table", bizApplied.size());
		
		return bizApplied;
	}
	
	private static List<EsgParamBiz> calculateBizAppliedParameter(String bssd , String bizDv, String irModelId) {
		List<EsgParamBiz> bizApplyRst = new ArrayList<EsgParamBiz>();
		EsgParamBiz temp;
		
			
			List<EsgParamHis> paramHisRst = EsgParamDao.getEsgParamHis(bssd,irModelId);
			
			logger.debug("applied : {}", paramHisRst.size());
			String matCd ;
			
			for(EsgParamHis bb : paramHisRst) {
				if(bb.getParamTypCd().equals("ALPHA") && bb.getMatCd().equals("M0240")) {
					continue;
				}
				else if(bb.getParamTypCd().equals("ALPHA") && bb.getMatCd().equals("M0120")) {
					matCd = "M0240";
				}
				else {
					matCd =bb.getMatCd();
				}
				
				temp = new EsgParamBiz();
				temp.setBaseYymm(bssd);
				temp.setApplBizDv(bizDv);
				temp.setIrModelId(irModelId);
				temp.setParamTypCd(bb.getParamTypCd());
				temp.setMatCd(matCd);
				temp.setApplParamVal(bb.getParamVal());
				
				
				bizApplyRst.add(temp);
			}
			
			int alphaOuterAvgNum 	= ERunSettings.ALPHA_OUTER_AVG_NUM.getIntValue();
			String alphaOuterMatCd  = ERunSettings.ALPHA_OUTER_MAT_CD.getStringValue();
			
			int sigmaOuterAvgNum 	= ERunSettings.SIGMA_OUTER_AVG_NUM.getIntValue();
			String sigmaOuterMatCd  = ERunSettings.SIGMA_OUTER_MAT_CD.getStringValue();
					 
			bizApplyRst.addAll(createBizAppliedParameterOuter(bssd, alphaOuterAvgNum, "ALPHA", alphaOuterMatCd, bizDv, irModelId))	;
			bizApplyRst.addAll(createBizAppliedParameterOuter(bssd, sigmaOuterAvgNum, "SIGMA", sigmaOuterMatCd, bizDv, irModelId))	;
		
		return bizApplyRst;
	}
	
	
	
	/**
	 *  @param bssd 
	 *  @param monthNum 
	 *  @param paramType 
	 *  @param matCd 
	 *  @return 
	*/
	
	private static List<EsgParamBiz> createBizAppliedParameterOuter(String bssd , int monthNum, String paramType, String matCd, String bizDv, String irMdoelId) {
		List<EsgParamBiz> bizApplyRst = new ArrayList<EsgParamBiz>();
		EsgParamBiz temp;
		
		
		
		List<EsgParamHis> paramHisRst = EsgParamDao.getSigmaLocalParamHis(bssd, monthNum, paramType, matCd, irMdoelId);


		logger.info("applied Outer: {}", paramHisRst.size());
		
		temp = new EsgParamBiz();
		temp.setBaseYymm(bssd);
		temp.setApplBizDv(bizDv);
		temp.setIrModelId(irMdoelId);
		temp.setParamTypCd(paramType);
		temp.setMatCd("M1200");
		temp.setApplParamVal(paramHisRst.stream().collect(Collectors.averagingDouble(s ->s.getParamVal())));
		bizApplyRst.add(temp);

		return bizApplyRst;
	}

}