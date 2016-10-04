package administrator.dao;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.exponentus.dataengine.jpa.DAO;
import com.exponentus.env.Environment;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.user.SuperUser;

import administrator.model.Language;

public class LanguageDAO extends DAO<Language, UUID> {

	public LanguageDAO() {
		super(Language.class, new _Session(Environment.adminApplication, new SuperUser()));
	}

	public LanguageDAO(_Session session) {
		super(Language.class, session);
	}

	public Language findByCode(LanguageCode code) {
		EntityManager em = emf.createEntityManager();
		try {
			String jpql = "SELECT m FROM Language AS m WHERE m.code = :code";
			TypedQuery<Language> q = em.createQuery(jpql, Language.class);
			q.setParameter("code", code);
			return q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} finally {
			em.close();
		}

	}

}
