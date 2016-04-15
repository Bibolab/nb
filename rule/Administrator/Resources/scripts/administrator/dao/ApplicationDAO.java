package administrator.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.exponentus.dataengine.jpa.DAO;
import com.exponentus.env.Environment;
import com.exponentus.scripting._Session;
import com.exponentus.user.SuperUser;

import administrator.model.Application;

public class ApplicationDAO extends DAO<Application, UUID> {

	public ApplicationDAO() {
		super(Application.class, new _Session(Environment.adminApplication, new SuperUser()));
	}

	public ApplicationDAO(_Session session) {
		super(Application.class, session);
	}

	public Application findByName(String name) {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		try {
			String jpql = "SELECT m FROM Application AS m WHERE m.name = :name";
			TypedQuery<Application> q = em.createQuery(jpql, Application.class);
			q.setParameter("name", name);
			List<Application> res = q.getResultList();
			return res.get(0);
		} catch (IndexOutOfBoundsException e) {
			return null;
		} finally {
			em.close();
		}

	}

}
