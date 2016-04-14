package com.exponentus.common.dao;

import java.util.UUID;

import com.exponentus.dataengine.jpa.DAO;
import com.exponentus.dataengine.jpa.SecureAppEntity;
import com.exponentus.scripting._Session;

public class SecureEntityDAO extends DAO<SecureAppEntity, UUID> {

	public SecureEntityDAO(_Session session) {
		super(SecureAppEntity.class, session);
	}
}
