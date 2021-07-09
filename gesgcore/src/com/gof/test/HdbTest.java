package com.gof.test;


import java.sql.Connection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hibernate.Session;

import com.gof.entity.IrCurve;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class HdbTest {

	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("ociarazor");
	
	public static void main(String[] args) {
		
		
        EntityManager em = emf.createEntityManager();
        
        Session session = (Session)em;
        
        Connection conn = session.disconnect();
        log.info("connecttion :  {}", conn);
        
        
        // read the existing entries and write to console
        Query q = em.createQuery("select t from IrCurve t");
        List<IrCurve> todoList = q.getResultList();
        todoList.forEach(s-> log.info("zzz : {},{}", s.toString()));
        
        
		String bssd ="201812";
//		log.info("aaaa : {},{}", getMstCalc());
//		getMstCalc().forEach(s-> log.info("zzz : {},{}", s.getCalcId()));
//		getMstRollFwd().forEach(s->log.info("aa : {}", s.getLossStep()));
		log.info("end");
		
	}

}
