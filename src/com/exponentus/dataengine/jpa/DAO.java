package com.exponentus.dataengine.jpa;

import com.exponentus.dataengine.RuntimeObjUtil;
import com.exponentus.exception.SecureException;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._SortMap;
import com.exponentus.server.Server;
import com.exponentus.user.IUser;
import com.exponentus.user.SuperUser;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public abstract class DAO<T extends IAppEntity, K> implements IDAO<T, K> {
    public IUser<Long> user;
    protected final Class<T> entityClass;
    protected EntityManagerFactory emf;
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
        } catch (Exception e) {
            Server.logger.errorLogEntry(e.toString());
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
                entity.setAuthorId(user.getId());
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

    // TODO it is need to try to use "greedy" mode to avoid "while"
    @SuppressWarnings("unchecked")
    @Override
    public T update(T entity) throws SecureException {
        EntityManager em = getEntityManagerFactory().createEntityManager();

        try {
            if (SecureAppEntity.class.isAssignableFrom(getEntityClass())) {
                if (user.getId() != SuperUser.ID) {
                    boolean isEditor = false;
                    SecureAppEntity<UUID> se = (SecureAppEntity<UUID>) entity;
                    Iterator<Long> it = se.getEditors().iterator();
                    while (it.hasNext()) {
                        if (it.next() == user.getId()) {
                            isEditor = true;
                            break;
                        }
                    }

                    if (!isEditor) {
                        throw new SecureException(ses.getAppEnv().appName, "editing_is_restricted", ses.getLang());
                    }
                } else {
                    Server.logger.warningLogEntry("Update behalf SuperUser " + entity.toString());
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
            if (user.getId() != SuperUser.ID) {
                boolean isEditor = false;
                SecureAppEntity<UUID> se = (SecureAppEntity<UUID>) entity;
                Iterator<Long> it = se.getEditors().iterator();
                while (it.hasNext()) {
                    if (it.next() == user.getId()) {
                        isEditor = true;
                        break;
                    }
                }

                if (!isEditor) {
                    throw new SecureException(ses.getAppEnv().appName, "deleting_is_restricted", ses.getLang());
                }
            } else {
                Server.logger.warningLogEntry("Delete behalf SuperUser " + entity.toString());
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

            ViewPage<T> r = new ViewPage<>(result, count, maxPage, pageNum);
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

            ViewPage<T> r = new ViewPage<>(result, count, maxPage, pageNum);
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
            ViewPage<T> r = new ViewPage<>(result, count, maxPage, pageNum);
            return r;
        } finally {
            em.close();
        }
    }

    public List<T> findAll(_SortMap sortMap, int pageNum, int pageSize) {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
            Root<T> root = cq.from(entityClass);
            cq.select(root);
            countCq.select(cb.count(root));
            Predicate condition = null;
            if (user.getId() != SuperUser.ID && SecureAppEntity.class.isAssignableFrom(getEntityClass())) {
                condition = cb.and(root.get("readers").in(user.getId()));
            }

            if (sortMap != null && !sortMap.isEmpty()) {
                List<Order> orderBy = new ArrayList<>();
                sortMap.values().forEach((fieldName, direction) -> {
                    if (direction.isAscending()) {
                        orderBy.add(cb.asc(root.get(fieldName)));
                    } else {
                        orderBy.add(cb.desc(root.get(fieldName)));
                    }
                });
                cq.orderBy(orderBy);
            }

            if (condition != null) {
                cq.where(condition);
                countCq.where(condition);
            }

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

            return typedQuery.getResultList();
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
