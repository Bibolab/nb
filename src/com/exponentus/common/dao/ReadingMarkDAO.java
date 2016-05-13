package com.exponentus.common.dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.eclipse.persistence.exceptions.DatabaseException;

import com.exponentus.common.model.ReadingMark;
import com.exponentus.dataengine.IDatabase;
import com.exponentus.dataengine.RuntimeObjUtil;
import com.exponentus.dataengine.jpa.ViewPage;
import com.exponentus.env.Environment;
import com.exponentus.server.Server;
import com.exponentus.user.IUser;

public class ReadingMarkDAO {
	private EntityManagerFactory emf;

	public ReadingMarkDAO() {
		IDatabase db = Environment.adminApplication.getDataBase();
		emf = db.getEntityManagerFactory();
	}

	public boolean isRead(UUID id, IUser<Long> user) {
		ReadingMark rm = findById(id, user);
		if (rm == null) {
			return false;
		}
		return true;

	}

	public boolean markAsRead(UUID id, IUser<Long> user) {
		ReadingMark rm = findById(id, user);
		if (rm == null) {
			ReadingMark mark = new ReadingMark();
			mark.setDocId(id);
			mark.setUser(user.getId());
			mark.setMarkDate(new Date());
			add(mark);
		}
		return true;

	}

	public ReadingMark findById(UUID id, IUser<Long> user) {
		EntityManager em = emf.createEntityManager();
		try {
			String jpql = "SELECT m FROM ReadingMark AS m WHERE m.docId = :id AND m.user = :user";
			TypedQuery<ReadingMark> q = em.createQuery(jpql, ReadingMark.class);
			q.setParameter("id", id);
			q.setParameter("user", user.getId());
			return q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			Server.logger.errorLogEntry(e);
			return null;
		} finally {
			em.close();
		}
	}

	public ViewPage<ReadingMark> findAllWhoRead(UUID id) {
		return findAllWhoRead(id, 0, 0);
	}

	public ViewPage<ReadingMark> findAllWhoRead(UUID id, int pageNum, int pageSize) {
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		try {
			CriteriaQuery<ReadingMark> cq = cb.createQuery(ReadingMark.class);
			CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
			Root<ReadingMark> c = cq.from(ReadingMark.class);
			cq.select(c);
			countCq.select(cb.count(c));

			Predicate condition = cb.equal(c.get("docId"), id);
			cq.where(condition);
			countCq.where(condition);

			TypedQuery<ReadingMark> typedQuery = em.createQuery(cq);
			Query query = em.createQuery(countCq);
			long count = (long) query.getSingleResult();
			int maxPage = 1;
			if (pageNum != 0 || pageSize != 0) {
				maxPage = RuntimeObjUtil.countMaxPage(count, pageSize);
				if (pageNum == 0) {
					pageNum = maxPage;
				}
				int firstRec = RuntimeObjUtil.calcStartEntry(pageNum, pageSize);
				typedQuery.setFirstResult(firstRec);
				typedQuery.setMaxResults(pageSize);
			}
			List<ReadingMark> result = typedQuery.getResultList();
			return new ViewPage<ReadingMark>(result, count, maxPage, pageNum);
		} finally {
			em.close();
		}
	}

	public ReadingMark add(ReadingMark entity) throws DatabaseException {
		EntityManager em = emf.createEntityManager();
		try {
			EntityTransaction t = em.getTransaction();
			try {
				t.begin();
				em.persist(entity);
				t.commit();
				return entity;
			} finally {
				if (t.isActive()) {
					t.rollback();
				}
			}
		} finally {
			em.close();

		}

	}
}
