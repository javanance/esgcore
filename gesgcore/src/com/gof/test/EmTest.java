package com.gof.test;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.gof.entity.EsgMeta;
import com.gof.entity.IrCurveHis;
import com.gof.entity.SwParamHis;
import com.gof.entity.SwaptionVol;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class EmTest {

	private static EntityManager em = Persistence.createEntityManagerFactory("ociarazor").createEntityManager();
	
	public static void main(String[] args) {
		
		String bssd ="201812";
		
        TypedQuery<EsgMeta> q = em.createQuery("select a from EsgMeta a", EsgMeta.class);
        List<EsgMeta> rstList = q.getResultList();
        rstList.forEach(s -> log.info("zzzz : {}", s.toString()));
        
        TypedQuery<IrCurveHis> q2 = em.createQuery("select a from IrCurveHis a", IrCurveHis.class);
        List<IrCurveHis> rstList2 = q2.getResultList();
        rstList2.forEach(s -> log.info("zzzz : {}", s.toString()));
        
        
//        TypedQuery<IrCurveHis2> q = em.createQuery("select a from IrCurveHis2 a", IrCurveHis2.class);
//        List<IrCurveHis2> rstList = q.getResultList();
//        rstList.forEach(s -> log.info("zzzz : {},{}", s.getIrCurve(), s.toString()));
        
        TypedQuery<SwParamHis> query3 = em.createQuery("select a from SwParamHis a", SwParamHis.class);
        List<SwParamHis> rstList3 = query3.getResultList();
        rstList3.forEach(s -> log.info("zzzz : {}", s.toString()));
        
        TypedQuery<SwaptionVol> query4 = em.createQuery("select a from SwaptionVol a", SwaptionVol.class);
        List<SwaptionVol> rstList4 = query4.getResultList();
        rstList4.forEach(s -> log.info("zzzz : {}", s.toString()));
        
		log.info("end");
		
	}

}
