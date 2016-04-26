package com.exponentus.dataengine;

import javax.persistence.EntityManagerFactory;

import com.exponentus.dataengine.IFTIndexEngine;

public interface IDatabase {

	IDBConnectionPool getConnectionPool();

	EntityManagerFactory getEntityManagerFactory();

	IDatabase getBaseObject();

	IFTIndexEngine getFTSearchEngine();

	String getInfo();

}
