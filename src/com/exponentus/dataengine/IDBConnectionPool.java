package com.exponentus.dataengine;

import java.sql.Connection;

import com.exponentus.dataengine.exception.DatabasePoolException;

public interface IDBConnectionPool {
	void initConnectionPool(String driver, String dbURL, String userName, String password)
	        throws DatabasePoolException, InstantiationException, IllegalAccessException, ClassNotFoundException;

	void initConnectionPool(String driver, String dbURL)
	        throws DatabasePoolException, InstantiationException, IllegalAccessException, ClassNotFoundException;

	Connection getConnection();

	void returnConnection(Connection con);

	int getNumActive();

	public String toXML();

	String getDatabaseVersion();

	void closeAll();

	void close(Connection conn);

}
