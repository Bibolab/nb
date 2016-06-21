package com.exponentus.dataengine.jpa;

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

import com.exponentus.dataengine.RuntimeObjUtil;
import com.exponentus.exception.SecureException;
import com.exponentus.scripting._Session;
import com.exponentus.user.IUser;
import com.exponentus.user.SuperUser;

public abstract class DAO<T extends IAppEntity, K> implements IDAO<T, K> {
	public IUser<Long> user;
	protected final Class<T> entityClass;
	private EntityManagerFactory emf;
	protected _Session ses;

	public DAO(Class<T> entityClass, _Session session) {
		this.entityClass = entityClass;
		ses = session;
		emf = session.getDatabase().getEntityManagerFactory();
		user = session.getUser();
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findById(String id) {
		try {
			return findById((K) UUID.fromString(id));
		} catch (IllegalArgumentException e) {
			// Server.logger.errorLogEntry(e.toString());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findById(K id) {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		boolean isSecureEntity = false;
		try {
			CriteriaQuery<T> cq = cb.createQuery(entityClass);
			Root<T> c = cq.from(entityClass);
			cq.select(c);
			Predicate condition = c.get("id").in(id);
			cq.where(condition);
			Query query = em.createQuery(cq);
			if (user.getId() != SuperUser.ID && SecureAppEntity.class.isAssignableFrom(getEntityClass())) {
				condition = cb.and(c.get("readers").in(user.getId()), condition);
				isSecureEntity = true;
			}
			T entity = (T) query.getSingleResult();
			if (isSecureEntity) {
				if (!((SecureAppEntity<UUID>) entity).getEditors().contains(user.getId())) {
					entity.setEditable(false);
				}
			}
			return entity;
		} catch (NoResultException e) {
			return null;
		} finally {
			em.close();
		}
	}

	@Override
	public ViewPage<T> findAllByIds(List<K> value, int pageNum, int pageSize) {
		return findAllin("id", value, pageNum, pageSize);
	}

	@Override
	public List<T> findAll(int firstRec, int pageSize) {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		try {
			TypedQuery<T> q = em.createNamedQuery(getQueryNameForAll(), entityClass);
			q.setFirstResult(firstRec);
			q.setMaxResults(pageSize);
			return q.getResultList();
		} finally {
			em.close();
		}
	}

	@Override
	public List<T> findAll() {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		try {
			TypedQuery<T> q = em.createNamedQuery(getQueryNameForAll(), entityClass);
			return q.getResultList();
		} finally {
			em.close();
		}
	}

	@Override
	public T add(T entity) throws SecureException {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		try {
			EntityTransaction t = em.getTransaction();
			try {
				t.begin();
				entity.setAuthor(user.getId());
				entity.setForm(entity.getDefaultFormName());
				em.persist(entity);
				t.commit();
				update(entity);
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

	@SuppressWarnings("unchecked")
	@Override
	public T update(T entity) throws SecureException {
		EntityManager em = getEntityManagerFactory().createEntityManager();

		try {
			if (user.getId() != SuperUser.ID && SecureAppEntity.class.isAssignableFrom(getEntityClass())) {
				if (!((SecureAppEntity<UUID>) entity).getEditors().contains(user.getId())) {
					throw new SecureException(ses.getAppEnv().appName, "editing_is_restricted", ses.getLang());
				}
			}
			EntityTransaction t = em.getTransaction();
			try {
				t.begin();
				em.merge(entity);
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

	@SuppressWarnings("unchecked")
	@Override
	public void delete(T entity) throws SecureException {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		try {
			if (user.getId() != SuperUser.ID && SecureAppEntity.class.isAssignableFrom(getEntityClass())) {
				if (!((SecureAppEntity<UUID>) entity).getEditors().contains(user.getId())) {
					throw new SecureException(ses.getAppEnv().appName, "deleting_is_restricted", ses.getLang());
				}
			}
			EntityTransaction t = em.getTransaction();
			try {
				t.begin();
				entity = em.merge(entity);
				em.remove(entity);
				t.commit();
			} finally {
				if (t.isActive()) {
					t.rollback();
				}
			}
		} finally {
			em.close();
		}
	}

	@Override
	public Long getCount() {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		try {
			Query q = em.createQuery("SELECT count(m) FROM " + entityClass.getName() + " AS m");
			return (Long) q.getSingleResult();
		} finally {
			em.close();
		}
	}

	public ViewPage<T> findAllequal(String fieldName, String value, int pageNum, int pageSize) {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		try {
			CriteriaQuery<T> cq = cb.createQuery(entityClass);
			CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
			Root<T> c = cq.from(entityClass);
			cq.select(c);
			countCq.select(cb.count(c));
			Predicate condition = cb.equal(c.get(fieldName), value);
			if (user.getId() != SuperUser.ID && SecureAppEntity.class.isAssignableFrom(getEntityClass())) {
				condition = cb.and(c.get("readers").in(user.getId()), condition);
			}
			cq.orderBy(cb.asc(c.get("regDate")));
			cq.where(condition);
			countCq.where(condition);
			TypedQuery<T> typedQuery = em.createQuery(cq);
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
			List<T> result = typedQuery.getResultList();

			ViewPage<T> r = new ViewPage<T>(result, count, maxPage, pageNum);
			return r;
		} finally {
			em.close();
		}
	}

	public ViewPage<T> findAllequal(String fieldName, Enum<?> value, int pageNum, int pageSize) {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		try {
			CriteriaQuery<T> cq = cb.createQuery(entityClass);
			CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
			Root<T> c = cq.from(entityClass);
			cq.select(c);
			countCq.select(cb.count(c));
			Predicate condition = cb.equal(c.get(fieldName), value);
			if (user.getId() != SuperUser.ID && SecureAppEntity.class.isAssignableFrom(getEntityClass())) {
				condition = cb.and(c.get("readers").in(user.getId()), condition);
			}
			cq.orderBy(cb.asc(c.get("regDate")));
			cq.where(condition);
			countCq.where(condition);
			TypedQuery<T> typedQuery = em.createQuery(cq);
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
			List<T> result = typedQuery.getResultList();

			ViewPage<T> r = new ViewPage<T>(result, count, maxPage, pageNum);
			return r;
		} finally {
			em.close();
		}
	}

	public ViewPage<T> findAllin(String fieldName, List<?> value, int pageNum, int pageSize) {
		EntityManager em = getEntityManagerFactory().createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		try {
			CriteriaQuery<T> cq = cb.createQuery(entityClass);
			CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
			Root<T> c = cq.from(entityClass);
			cq.select(c);
			countCq.select(cb.count(c));
			Predicate condition = c.get(fieldName).in(value);
			if (user.getId() != SuperUser.ID && SecureAppEntity.class.isAssignableFrom(getEntityClass())) {
				condition = cb.and(c.get("readers").in(user.getId()), condition);
			}
			cq.orderBy(cb.asc(c.get("regDate")));
			cq.where(condition);
			countCq.where(condition);
			TypedQuery<T> typedQuery = em.createQuery(cq);
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
			List<T> result = typedQuery.getResultList();
			ViewPage<T> r = new ViewPage<T>(result, count, maxPage, pageNum);
			return r;
		} finally {
			em.close();
		}
	}

	public String getQueryNameForAll() {
		String queryName = entityClass.getSimpleName() + ".findAll";
		return queryName;
	}

	public _Session getSession() {
		return ses;
	}
}