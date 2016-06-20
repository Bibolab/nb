package com.exponentus.common.dao;

import com.exponentus.common.model.Attachment;
import com.exponentus.dataengine.jpa.DAO;
import com.exponentus.scripting._Session;

import java.util.UUID;

public class AttachmentDAO extends DAO<Attachment, UUID> {

    public AttachmentDAO(_Session session) {
        super(Attachment.class, session);
    }
}
