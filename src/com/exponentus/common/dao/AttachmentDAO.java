package com.exponentus.common.dao;

import com.exponentus.common.model.Attachment;
import com.exponentus.dataengine.jpa.DAO;
import com.exponentus.scripting._Session;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

public class AttachmentDAO extends DAO<Attachment, UUID> {

    public AttachmentDAO(_Session session) {
        super(Attachment.class, session);
    }

    public List<Attachment> findAllWithoutThumbnailByExtension(String... extensions) {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        try {
            CriteriaQuery<Attachment> cq = cb.createQuery(Attachment.class);
            Root<Attachment> c = cq.from(Attachment.class);

            Predicate condition = cb.equal(c.get("hasThumbnail"), false);
            condition = cb.and(c.get("extension").in(extensions), condition);
            cq.select(c);
            cq.where(condition);

            TypedQuery<Attachment> typedQuery = em.createQuery(cq);
            return typedQuery.getResultList();
        } finally {
            em.close();
        }
    }
}
