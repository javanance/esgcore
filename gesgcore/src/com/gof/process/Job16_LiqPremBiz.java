package com.gof.process;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gof.dao.LiqPremDao;
import com.gof.entity.LiqPremHis;
import com.gof.entity.LiqPremBiz;
import com.gof.entity.LiqPremUd;
import com.gof.enums.ERunSettings;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job16_LiqPremBiz {
	
	public static List<LiqPremBiz> createBizLiqPremium(String bssd, String bizDv, String ifrsliqModelNm, int avgNum) {
		List<LiqPremBiz> rstList = new ArrayList<LiqPremBiz>();
		String stBssd = DateUtil.addMonthToString(bssd, avgNum);
		
		List<LiqPremHis> liqPremList   	= LiqPremDao.getLiqPremiumBtw(stBssd, bssd, ifrsliqModelNm);
		List<LiqPremUd> liqPremUdList 	= LiqPremDao.getLiqPremiumUd(bssd, ifrsliqModelNm);
	
		log.info("User Liq Prem for {} : {}", ifrsliqModelNm, liqPremUdList.size());
		
		
		Map<String, Double> lqMap = new HashMap<String, Double>();
		if(liqPremUdList.isEmpty()) {
			lqMap = liqPremList.stream().collect(groupingBy(LiqPremHis::getMatCd, Collectors.averagingDouble(LiqPremHis::getLiqPrem)));
		}
		else{
//			lqMap = liqPremUdList.stream().collect(toMap(LiqPremUd::getMatCd, LiqPremUd::getLiqPrem));
			lqMap = liqPremUdList.stream().collect(groupingBy(LiqPremUd::getMatCd, Collectors.averagingDouble(LiqPremUd::getLiqPrem)));
		}
		
		List<String> tenorList = ERunSettings.TENOR_LIST.getStringList();
		Collections.reverse(tenorList);
		
		double prevLiq =0.0;
		for(String aa : tenorList) {
			if(lqMap.containsKey(aa)) {
				prevLiq = lqMap.get(aa);
			}
			rstList.add(build(bssd, bizDv, aa, ifrsliqModelNm, prevLiq));
		}
		return rstList;
	}
	
	private static LiqPremBiz build(String bssd, String bizDv, String matCd, String ifrsliqModelNm, double liqPrem) {
		LiqPremBiz rst = new LiqPremBiz();
		rst.setBaseYymm(bssd);
		rst.setApplBizDv(bizDv);
		rst.setMatCd(matCd);
		rst.setLiqModelNm(ifrsliqModelNm);
		rst.setLiqPrem(liqPrem);
		
		return rst;
	}
	
}
