package com.exponentus.dataengine.h2;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.exponentus.dataengine.DatabaseUtil;
import com.exponentus.dataengine.IDBConnectionPool;
import com.exponentus.dataengine.exception.DatabasePoolException;
import com.exponentus.dataengine.system.IEmployeeDAO;
import com.exponentus.legacy.User;
import com.exponentus.server.Server;

public class SystemDatabase implements ISystemDatabase {
	public static boolean isValid;
	public static String jdbcDriver = "org.h2.Driver";

	private IDBConnectionPool dbPool;
	private static String connectionURL = "jdbc:h2:system_data" + File.separator + "system_data;MVCC=TRUE;AUTO_SERVER=TRUE";

	private IEmployeeDAO eDao;

	public SystemDatabase() throws DatabasePoolException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		this(connectionURL);
	}

	public SystemDatabase(String connURL) throws DatabasePoolException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		dbPool = new com.exponentus.dataengine.h2.DBConnectionPool();
		dbPool.initConnectionPool(jdbcDriver, connURL);
		Connection conn = dbPool.getConnection();
		try {
			conn.setAutoCommit(false);

			isValid = true;
			conn.commit();
		} catch (Throwable e) {
			Server.logger.errorLogEntry(e.toString());
			e.printStackTrace();
			DatabaseUtil.debugErrorPrint(e);
		} finally {
			dbPool.returnConnection(conn);
		}
	}

	@Deprecated
	@Override
	public ArrayList<User> getAllUsers(String condition, int start, int end) {
		ArrayList<User> users = new ArrayList<User>();
		String wherePiece = "";
		Connection conn = dbPool.getConnection();
		try {
			conn.setAutoCommit(false);
			if (!condition.equals("")) {
				wherePiece = "WHERE " + condition;
			}
			Statement s = conn.createStatement();
			String sql = "select * from USERS " + wherePiece + " LIMIT " + end + " OFFSET " + start;
			ResultSet rs = s.executeQuery(sql);

			while (rs.next()) {
				User user = new User();
				user.fill(rs);

				users.add(user);
			}

			rs.close();
			s.close();
			conn.commit();
			return users;
		} catch (Throwable e) {
			DatabaseUtil.debugErrorPrint(e);
			return null;
		} finally {
			dbPool.returnConnection(conn);
		}
	}

	@Override
	public User getUser(long docID) {
		User user = new User();
		Connection conn = dbPool.getConnection();
		try {
			conn.setAutoCommit(false);
			Statement s = conn.createStatement();
			/*
			 * String sql =
			 * "select * from USERS, ENABLEDAPPS where USERS.DOCID=ENABLEDAPPS.DOCID and "
			 * + "USERS.DOCID=" + docID;
			 */
			String sql = "select * from USERS where USERS.DOCID=" + docID;
			ResultSet rs = s.executeQuery(sql);

			rs.close();
			s.close();
			conn.commit();
		} catch (Throwable e) {
			DatabaseUtil.debugErrorPrint(e);
		} finally {
			dbPool.returnConnection(conn);
		}
		return user;
	}
}
