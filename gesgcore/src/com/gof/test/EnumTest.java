package com.gof.test;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.gof.entity.EsgMeta;
import com.gof.entity.IrCurveHis;
import com.gof.entity.SwParamHis;
import com.gof.entity.SwaptionVol;
import com.gof.enums.ETest;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class EnumTest {

	
	public static void main(String[] args) {
		String entry ="M0030";
		log.info("aaa : {}", Double.valueOf(entry.split("M")[1]));
	
		
		for(ETest aa : ETest.values()) {
			int ori = aa.getJobNo();
			 aa.setJobNo(10);
			 log.info("aaa : {},{},{}", ori, aa.getJobName(), aa.getJobNo());
		}
		
        
		log.info("end : {}", ETest.ESG1.getJobNo());
		
	}

}
