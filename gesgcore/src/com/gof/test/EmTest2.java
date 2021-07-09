package com.gof.test;


import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.gof.dao.EsgMetaDao;
import com.gof.entity.EsgMeta;
import com.gof.entity.SwaptionVol;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class EmTest2 {

	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("ociarazor");
	
	public static void main(String[] args) {
		
		
        EntityManager em = emf.createEntityManager();
        
        EsgMetaDao.getEsgMeta("BASE").forEach(s-> log.info("aaaa : {}", s));
        
        
        TypedQuery<SwaptionVol> q = em.createQuery("select a from SwaptionVol a", SwaptionVol.class);
        List<SwaptionVol> todoList = q.getResultList();
//        todoList.forEach(s-> log.info("zzz : {},{}", s.toString()));

        List<SwaptionVol> todoList1 = em.createQuery("select a from SwaptionVol a", SwaptionVol.class).getResultList();
//        todoList1.forEach(s-> log.info("zzz : {},{}", s.toString()));
        
        
		String bssd ="201812";
//		log.info("aaaa : {},{}", getMstCalc());
//		getMstCalc().forEach(s-> log.info("zzz : {},{}", s.getCalcId()));
//		getMstRollFwd().forEach(s->log.info("aa : {}", s.getLossStep()));
		log.info("end");
		
	}

}
