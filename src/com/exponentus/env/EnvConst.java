package com.exponentus.env;

import java.nio.file.Paths;

import com.exponentus.localization.LanguageCode;

/**
 *
 *
 * @author Kayra created 23-01-2016
 */

// TODO need to secure this class
public class EnvConst {
	public static final String FRAMEWORK_NAME = "nb";
	public static final String SUPPOSED_CODE_PAGE = "utf-8";
	public static final String DEFAULT_XML_ENC = "utf-8";
	public static final String CFG_FILE = "cfg.xml";
	public static final String APP_ATTR = "app";
	public static final String DEFAULT_PAGE = "index";
	public final static String SESSION_ATTR = "usersession";
	public static final String LANG_COOKIE_NAME = "lang";
	public static final String CALLING_PAGE_COOKIE_NAME = "cp";
	public static final String PAGE_SIZE_COOKIE_NAME = "pagesize";
	public static final String ADMINISTRATOR_APP_NAME = "Administrator";
	public final static String SHARED_RESOURCES_APP_NAME = "SharedResources";
	public static final String ERROR_XSLT = "error.xsl";
	public static final String FSID_FIELD_NAME = "fsid";
	public static final String TIME_FIELD_NAME = "time";
	public static final String JDBC_DRIVER = "org.postgresql.Driver";
	public static final int DEFAULT_HTTP_PORT = 38700;
	public static final String ADMINISTRATOR_SERVICE_CLASS = "staff.services.UserServices";
	public static final String OFFICEFRAME = "officeframe";
	public static final String WORKSPACE_NAME = "Workspace";
	public static final String STAFF_NAME = "Staff";
	public static final String MONITORING_NAME = "Monitoring";
	public static final String MONITORING_DAO_CLASS = "monitoring.dao.UserActivityDAO";
	public static final String[] OFFICEFRAME_APPS = { STAFF_NAME, "Reference", WORKSPACE_NAME, "Integration", "DataExport", MONITORING_NAME };

	public static String CLI = "ON";
	public static String AUTH_COOKIE_NAME = "nb3ses";
	public static String DUMMY_USER = "admin";
	public static String DUMMY_PASSWORD = "secret";
	public static String JPA_LOG_LEVEL = "OFF";
	public static String DEFAULT_LANG = LanguageCode.ENG.name();
	public static String DEFAULT_APPLICATION = WORKSPACE_NAME;
	public static int DEFAULT_PAGE_SIZE = 20;
	public static String DEFAULT_DATE_FORMAT = "dd.MM.yyyy";
	public static String DEFAULT_DATETIME_FORMAT = "dd.MM.yyyy kk:mm";
	public static String DEFAULT_TIME_FORMAT = "kk:mm";
	public static String DEFAULT_COUNTRY_OF_NUMBER_FORMAT = "ru";
	public static String DB_TYPE = "postgresql";
	public static String DB_USER = "postgres";
	public static String DB_PWD = "smartdoc";
	public static String APP_ID = Paths.get(System.getProperty("user.dir")).getFileName().toString();
	public static String DATABASE_NAME = APP_ID;
	public static String DATABASE_HOST = "127.0.0.1";
	public static String CONN_PORT = "5432";
	public static String CAPTCHA_CODE = "";
	public static String STAFF_APP_NAME = STAFF_NAME;
	public static String STAFF_DAO_CLASS = "staff.dao.EmployeeDAO";
	public static String RESOURCES_DIR = "resources";
	public static String OLD_STRUCTDB_USER = DB_USER;
	public static String OLD_STRUCTDB_PWD = "";
	public static String OLD_STRUCTDB_URL = "";
}
