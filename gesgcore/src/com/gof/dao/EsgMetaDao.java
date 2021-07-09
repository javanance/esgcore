package com.gof.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import com.gof.entity.EsgMeta;
import com.gof.enums.EBoolean;
import com.gof.infra.EntityManagerUtil;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class EsgMetaDao {
//	private static EntityManager em = Persistence.createEntityManagerFactory("ociarazor").createEntityManager();
	private static EntityManager em = EntityManagerUtil.getEntityManger();
	
	
	public static List<EsgMeta> getEsgMeta() {
		String q = "select a from EsgMeta a "	;
		return em.createQuery(q, EsgMeta.class).getResultList();
	}

	public static List<EsgMeta> getEsgMeta(String groupId) {
		String q = " select a from EsgMeta a "
				+ "   where 1=1 "
				+ "	  and a.groupId = :groupId	" 
				+ "   and a.useYn = :param"
				+ "   order by a.paramKey"
				;
		
		return em.createQuery(q, EsgMeta.class)
				.setParameter("groupId", groupId)
				.setParameter("param", EBoolean.Y)
				.getResultList();
	}
}
