package com.exponentus.dataengine;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

public interface IDatabase {

	IDBConnectionPool getConnectionPool();

	EntityManagerFactory getEntityManagerFactory();

	IFTIndexEngine getFTSearchEngine();

	String getInfo();

	Map<String, Long> getCountsOfRec();

}
