package com.gof.infra;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import com.gof.entity.EsgMeta;
import com.gof.enums.EBoolean;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class EntityManagerUtil {
	private static EntityManager em = Persistence.createEntityManagerFactory("ociarazor").createEntityManager();
	
	public static EntityManager getEntityManger() {
		return em;
	}
	
	
}
