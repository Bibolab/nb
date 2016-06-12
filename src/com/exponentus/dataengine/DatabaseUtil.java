package com.exponentus.dataengine;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.exponentus.env.Environment;
import com.exponentus.server.Server;

public class DatabaseUtil {

	public int getRegNum(String key) {
		int lastNum = 1;
		IDBConnectionPool dbPool = Environment.adminApplication.getDataBase().getConnectionPool();
		Connection conn = dbPool.getConnection();
		try {
			conn.setAutoCommit(false);
			String sql = "select * from COUNTERS where KEYS='" + key + "'";
			PreparedStatement pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			String keyValue = "";
			if (rs.next()) {
				keyValue = rs.getString("KEYS");
				lastNum = rs.getInt("LASTNUM");
			}
			if (keyValue != "") {
				lastNum++;
			}
			rs.close();
			pst.close();
			conn.commit();
		} catch (SQLException e) {
			DatabaseUtil.errorPrint(e);
		} finally {
			dbPool.returnConnection(conn);
		}
		return lastNum;
	}

	@SuppressWarnings("resource")
	public int postRegNum(int num, String key) {
		int lastNum = 0;
		IDBConnectionPool dbPool = Environment.adminApplication.getDataBase().getConnectionPool();
		Connection conn = dbPool.getConnection();
		PreparedStatement pst = null;
		try {
			// conn.close();
			conn.setAutoCommit(false);
			String sql = "select *from COUNTERS where KEYS='" + key + "'";
			pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			String keyValue = "";
			if (rs.next()) {
				keyValue = rs.getString("KEYS");
				lastNum = rs.getInt("LASTNUM");
			}
			rs.close();
			String getNum = null;
			conn.setAutoCommit(false);
			if (keyValue.equals("")) {
				getNum = "insert into COUNTERS(KEYS, LASTNUM)values(?,?)";
				pst = conn.prepareStatement(getNum);
				pst.setString(1, key);
				pst.setInt(2, num);
			} else {
				getNum = "update COUNTERS set LASTNUM = ? where KEYS = ? ";
				pst = conn.prepareStatement(getNum);
				lastNum++;
				pst.setInt(1, num);
				pst.setString(2, key);
			}
			pst.executeUpdate();
			conn.commit();
			pst.close();
		} catch (SQLException e) {
			DatabaseUtil.errorPrint(e);
			lastNum = -1;
		} finally {
			dbPool.returnConnection(conn);
		}
		return lastNum;
	}

	public static void errorPrint(Throwable e) {
		if (e instanceof SQLException) {
			SQLException sqle = (SQLException) e;
			SQLExceptionPrint(sqle);
		} else {
			Server.logger.errorLogEntry(e.toString());
			e.printStackTrace();
		}
	}

	public static void debugErrorPrint(Throwable e) {
		if (e instanceof SQLException) {
			SQLException sqle = (SQLException) e;
			SQLExceptionPrintDebug(sqle);
		} else {
			Server.logger.errorLogEntry(e.toString());
			e.printStackTrace();
		}
	}

	public static void errorPrint(Throwable e, String sql) {
		Server.logger.errorLogEntry(sql);
		if (e instanceof SQLException) {
			SQLException sqle = (SQLException) e;
			SQLExceptionPrintDebug(sqle);
		} else {
			Server.logger.errorLogEntry(e.toString());
			e.printStackTrace();
		}
	}

	public static void SQLExceptionPrint(SQLException sqle) {
		while (sqle != null) {
			Server.logger.errorLogEntry("SQLState:   " + sqle.getSQLState());
			Server.logger.errorLogEntry("Severity: " + sqle.getErrorCode());
			Server.logger.errorLogEntry("Message:  " + sqle.getMessage());
			sqle = sqle.getNextException();
		}
	}

	public static void SQLExceptionPrintDebug(SQLException sqle) {
		while (sqle != null) {
			Server.logger.errorLogEntry("SQLState:   " + sqle.getSQLState());
			Server.logger.errorLogEntry("Severity: " + sqle.getErrorCode());
			Server.logger.errorLogEntry("Message:  " + sqle.getMessage());
			Server.logger.errorLogEntry(sqle);
			sqle.printStackTrace();
			sqle = sqle.getNextException();
		}
	}

	public static boolean hasTable(String tableName, Connection conn) throws SQLException {
		try {
			DatabaseMetaData metaData = null;
			metaData = conn.getMetaData();
			String[] tables = { "TABLE" };
			ResultSet rs = metaData.getTables(null, null, null, tables);
			while (rs.next()) {
				String table = rs.getString("TABLE_NAME");
				if (tableName.equalsIgnoreCase(table)) {
					return true;
				}
			}
			return false;
		} catch (Throwable e) {
			return false;
		}
	}

}
