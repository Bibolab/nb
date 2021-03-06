package com.exponentus.dataengine.jpadatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.postgresql.util.PSQLException;

import com.exponentus.appenv.AppEnv;
import com.exponentus.dataengine.DatabaseUtil;
import com.exponentus.dataengine.IDBConnectionPool;
import com.exponentus.dataengine.IDatabase;
import com.exponentus.dataengine.IFTIndexEngine;
import com.exponentus.dataengine.exception.DatabasePoolException;
import com.exponentus.dataengine.h2.DBConnectionPool;
import com.exponentus.dataengine.jpadatabase.ftengine.FTSearchEngine;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.env.Site;
import com.exponentus.exception.SecureException;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.server.Server;
import com.exponentus.user.SuperUser;

import administrator.dao.ApplicationDAO;
import administrator.dao.LanguageDAO;
import administrator.dao.UserDAO;
import administrator.init.ServerConst;
import administrator.model.Application;
import administrator.model.Language;
import administrator.model.User;

public class Database implements IDatabase {
	protected static String connectionURL = "";
	protected IDBConnectionPool dbPool;
	protected EntityManagerFactory factory;

	private FTSearchEngine ftEngine;
	private boolean isNascence;
	private Properties props = new Properties();

	public Database() {
		props.setProperty("user", EnvConst.DB_USER);
		props.setProperty("password", EnvConst.DB_PWD);
		String sysDbURL = "jdbc:postgresql://" + EnvConst.DATABASE_HOST + ":" + EnvConst.CONN_PORT + "/postgres";

		try {
			if (!hasDatabase(EnvConst.DATABASE_NAME, sysDbURL, props)) {
				Server.logger.infoLogEntry("creating database \"" + EnvConst.DATABASE_NAME + "\"...");
				// registerUser(dbUser, dbPwd, sysDbURL, props);
				if (createDatabase(EnvConst.DATABASE_NAME, EnvConst.DB_USER, sysDbURL, props) == 0) {
					Server.logger.infoLogEntry("the database has been created");
					isNascence = true;
				}
			}
		} catch (SQLException e) {
			Server.logger.errorLogEntry(e);
		}

		connectionURL = "jdbc:postgresql://" + EnvConst.DATABASE_HOST + ":" + EnvConst.CONN_PORT + "/" + EnvConst.DATABASE_NAME;

		dbPool = new DBConnectionPool();
		try {
			dbPool.initConnectionPool(EnvConst.JDBC_DRIVER, connectionURL, EnvConst.DB_USER, EnvConst.DB_PWD);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | DatabasePoolException e) {
			Server.logger.errorLogEntry(e);
		}

		Map<String, String> properties = getProp();
		PersistenceProvider pp = new PersistenceProvider();
		factory = pp.createEntityManagerFactory(EnvConst.ADMINISTRATOR_APP_NAME, properties);
		if (factory == null) {
			Server.logger.errorLogEntry("the entity manager of \"" + EnvConst.ADMINISTRATOR_APP_NAME + "\" has not been initialized");
		} else {

			if (isNascence) {
				Server.logger.infoLogEntry("Loading primary data...");
				_Session ses = new _Session(new AppEnv(EnvConst.ADMINISTRATOR_APP_NAME, this), new SuperUser());
				Server.logger.infoLogEntry("setup localization environment...");
				LanguageDAO dao = new LanguageDAO(ses);
				for (LanguageCode lc : Environment.langs) {
					Language entity = ServerConst.getLanguage(lc);
					try {
						dao.add(entity);
						Server.logger.infoLogEntry("add " + entity.getCode() + " language");
					} catch (SecureException e) {
						Server.logger.errorLogEntry(e);
					}
				}

				Server.logger.infoLogEntry("setup applications...");
				ApplicationDAO aDao = new ApplicationDAO(ses);
				for (Site site : Environment.webAppToStart.values()) {
					Application entity = ServerConst.getApplication(site);
					try {
						aDao.add(entity);
						Server.logger.infoLogEntry("register \"" + entity.getName() + "\" application");
					} catch (SecureException e) {
						Server.logger.errorLogEntry(e);
					}
				}

				theFirst(ses);
			}
		}
	}

	public Database(String appName) throws DatabasePoolException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		dbPool = new DBConnectionPool();
		dbPool.initConnectionPool(EnvConst.JDBC_DRIVER, connectionURL, EnvConst.DB_USER, EnvConst.DB_PWD);
		Map<String, String> properties = getProp();
		PersistenceProvider pp = new PersistenceProvider();
		// System.out.println(properties);
		factory = pp.createEntityManagerFactory(appName, properties);
		if (factory == null) {
			Server.logger.errorLogEntry("the entity manager of \"" + appName + "\" has not been initialized");

		}
		ftEngine = new FTSearchEngine(this);
	}

	public int registerUser(String dbUser, String dbPwd, String dbURL, Properties props) throws SQLException {

		Connection conn = DriverManager.getConnection(dbURL, props);
		try {
			Statement st = conn.createStatement();
			try {
				st.executeUpdate("CREATE USER  " + dbUser + " WITH password '" + dbPwd + "'");
				return 0;
			} catch (PSQLException sqle) {
				// Server.logger.warningLogEntry("database user \"" + dbUser +
				// "\" already exists");
				return 1;
			} catch (Exception e) {
				Server.logger.errorLogEntry(e.getMessage());
				return 1;
			}
		} catch (Throwable e) {
			DatabaseUtil.debugErrorPrint(e);
			return -1;
		}

	}

	public int createDatabase(String dbName, String dbUser, String dbURL, Properties prop) throws SQLException {
		if (!hasDatabase(dbName, dbURL, prop)) {
			Connection conn = DriverManager.getConnection(dbURL, prop);
			try {
				Statement st = conn.createStatement();
				String sql = "CREATE DATABASE \"" + dbName + "\" WITH OWNER = " + dbUser + " ENCODING = 'UTF8'";
				st.executeUpdate(sql);
				st.executeUpdate("GRANT ALL privileges ON DATABASE \"" + dbName + "\" TO " + dbUser);
				st.close();
				return 0;
			} catch (Throwable e) {
				DatabaseUtil.debugErrorPrint(e);
				return -1;
			}
		} else {
			return 1;
		}
	}

	@Override
	public String toString() {
		return "version NB3";
	}

	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		return factory;
	}

	@Override
	public IFTIndexEngine getFTSearchEngine() {
		return ftEngine;
	}

	private boolean hasDatabase(String dbName, String dbURL, Properties prop) throws SQLException {
		Connection conn = DriverManager.getConnection(dbURL, prop);
		try {
			conn.setAutoCommit(false);
			Statement s = conn.createStatement();
			String sql = "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'";
			ResultSet rs = s.executeQuery(sql);
			if (rs.next()) {
				return true;
			}
			s.close();
			conn.commit();
			return false;
		} catch (Throwable e) {
			return false;
		}
	}

	@Override
	public int getRegNum(String key) {
		int lastNum = 1;
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
	@Override
	public int postRegNum(int num, String key) {
		int lastNum = 0;
		Connection conn = dbPool.getConnection();
		try {
			conn.setAutoCommit(false);
			String sql = "select *from COUNTERS where KEYS='" + key + "'";
			PreparedStatement pst = conn.prepareStatement(sql);
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

	private int theFirst(_Session ses) {
		String userName = EnvConst.DUMMY_USER;
		String pwd = EnvConst.DUMMY_PASSWORD;
		User entity = new User();
		entity.setSuperUser(true);
		entity.setLogin(userName);
		entity.setPwd(pwd);
		entity.setDefaultLang(LanguageCode.valueOf(EnvConst.DEFAULT_LANG));
		ApplicationDAO aDao = new ApplicationDAO(ses);
		entity.setAllowedApps(aDao.findAll());
		UserDAO uDao = new UserDAO(this);
		uDao.add(entity);
		return 0;
	}

	@Override
	public IDBConnectionPool getConnectionPool() {
		return dbPool;
	}

	@Override
	public String getInfo() {
		return "url=" + connectionURL;
	}

	@Override
	public List<String[]> getCountsOfRec() {
		List<String[]> result = new ArrayList<String[]>();

		try {
			Connection conn = DriverManager.getConnection(connectionURL, props);
			conn.setAutoCommit(false);
			Statement s = conn.createStatement();
			String sql = "SELECT relname,n_live_tup FROM pg_stat_user_tables ORDER BY relname ASC;";
			ResultSet rs = s.executeQuery(sql);
			while (rs.next()) {
				String[] a = { rs.getString(1), Long.toString(rs.getLong(2)) };
				result.add(a);
			}
			s.close();
			conn.commit();
		} catch (Throwable e) {
			DatabaseUtil.debugErrorPrint(e);
		}
		return result;
	}

	@Override
	public long getCount() {
		int countOfRecords = 0;

		try {
			Connection conn = DriverManager.getConnection(connectionURL, props);
			conn.setAutoCommit(false);
			Statement s = conn.createStatement();
			String sql = "SELECT sum(n_live_tup) FROM pg_stat_user_tables";
			ResultSet rs = s.executeQuery(sql);
			if (rs.next()) {
				countOfRecords = rs.getInt(1);
			}
			s.close();
			conn.commit();
		} catch (Throwable e) {
			DatabaseUtil.debugErrorPrint(e);
		}
		return countOfRecords;
	}

	private Map<String, String> getProp() {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(PersistenceUnitProperties.JDBC_DRIVER, EnvConst.JDBC_DRIVER);
		properties.put(PersistenceUnitProperties.JDBC_USER, EnvConst.DB_USER);
		properties.put(PersistenceUnitProperties.JDBC_PASSWORD, EnvConst.DB_PWD);
		properties.put(PersistenceUnitProperties.JDBC_URL, connectionURL);

		// INFO,
		// OFF,
		// ALL,
		// CONFIG (developing)
		properties.put(PersistenceUnitProperties.LOGGING_LEVEL, EnvConst.JPA_LOG_LEVEL);
		properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_OR_EXTEND);
		properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION,
		        PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
		properties.put(PersistenceUnitProperties.LOGGING_LEVEL, EnvConst.JPA_LOG_LEVEL);
		// properties.put(PersistenceUnitProperties.DDL_GENERATION_MODE,
		// PersistenceUnitProperties.DDL_BOTH_GENERATION);
		// properties.put(PersistenceUnitProperties.CREATE_JDBC_DDL_FILE,
		// "create.sql");
		return properties;
	}
}
