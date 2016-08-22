package com.exponentus.dataengine;

import java.util.List;

import javax.persistence.EntityManagerFactory;

public interface IDatabase {

	IDBConnectionPool getConnectionPool();

	EntityManagerFactory getEntityManagerFactory();

	IFTIndexEngine getFTSearchEngine();

	String getInfo();

	List<String[]> getCountsOfRec();

	long getCount();

	int getRegNum(String key);

	int postRegNum(int num, String key);

}
