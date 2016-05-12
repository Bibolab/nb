package com.exponentus.common.dao;

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
import com.exponentus.user.IUser;

public class ReadingMarkDAO {
	private EntityManagerFactory emf;

	public ReadingMarkDAO() {
		IDatabase db = Environment.adminApplication.getDataBase();
		emf = db.getEntityManagerFactory();
	}

	public boolean markAsRead(UUID id, IUser<Long> user) {
		ReadingMark rm = findById(id, user);
		if (rm == null) {
			ReadingMark mark = new ReadingMark();
			mark.setUser(user.getId());
			add(mark);
		}
		return true;

	}

	public ReadingMark findById(UUID id, IUser<Long> user) {
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		try {
			CriteriaQuery<ReadingMark> cq = cb.createQuery(ReadingMark.class);
			Root<ReadingMark> c = cq.from(ReadingMark.class);
			cq.select(c);
			Predicate condition = c.get("id").in(id);
			cq.where(condition);
			Query query = em.createQuery(cq);
			ReadingMark entity = (ReadingMark) query.getSingleResult();
			return entity;
		} catch (NoResultException e) {
			return null;
		} finally {
			em.close();
		}
	}

	public ViewPage<ReadingMark> findWhoRead(UUID id) {
		return findWhoRead(id, 0, 0);
	}

	public ViewPage<ReadingMark> findWhoRead(UUID id, int pageNum, int pageSize) {
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
