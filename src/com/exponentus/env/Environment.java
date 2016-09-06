package com.exponentus.env;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jdom.input.SAXHandler;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.exponentus.appenv.AppEnv;
import com.exponentus.caching.ICache;
import com.exponentus.dataengine.IDatabase;
import com.exponentus.dataengine.IDatabaseDeployer;
import com.exponentus.dataengine.jpadatabase.Database;
import com.exponentus.dataengine.jpadatabase.DatabaseDeployer;
import com.exponentus.dataengine.system.IExtUserDAO;
import com.exponentus.exception.RuleException;
import com.exponentus.localization.LanguageCode;
import com.exponentus.localization.Localizator;
import com.exponentus.localization.Vocabulary;
import com.exponentus.rest.RestType;
import com.exponentus.rule.constans.RunMode;
import com.exponentus.runtimeobj.Page;
import com.exponentus.scheduler.PeriodicalServices;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scriptprocessor.page.PageOutcome;
import com.exponentus.server.Server;
import com.exponentus.user.AnonymousUser;
import com.exponentus.util.XMLUtil;
import com.exponentus.webserver.WebServer;

import net.sf.saxon.s9api.SaxonApiException;

public class Environment implements ICache {

	public static boolean verboseLogging;
	public static Date startTime;
	public static String orgName;
	public static String hostName;
	public static int httpPort = EnvConst.DEFAULT_HTTP_PORT;
	public static AppEnv adminApplication;
	public static HashMap<String, String> mimeHash = new HashMap<String, String>();
	public static HashMap<String, Site> webAppToStart = new HashMap<String, Site>();
	public static String tmpDir;
	public static String trash;
	public static ArrayList<String> fileToDelete = new ArrayList<String>();

	public static Boolean isTLSEnable = false;
	public static int secureHttpPort;
	public static String certFile = "";
	public static String certKeyFile = "";

	public static List<LanguageCode> langs = new ArrayList<LanguageCode>();

	public static Boolean mailEnable = false;
	public static String smtpPort = "25";
	public static boolean smtpAuth;
	public static String SMTPHost;
	public static String smtpUser;
	public static String smtpPassword;
	public static String smtpUserName;

	public static Boolean slackEnable = false;
	public static String slackToken;

	public static boolean XMPPServerEnable;
	public static String XMPPServer;
	public static ProxyInfo XMPPServerPort;
	public static String XMPPLogin;
	public static String XMPPPwd;
	public static ChatManager chatmanager;

	public static Vocabulary vocabulary;
	public static AuthMethodType authMethod = AuthMethodType.WORKSPACE_LOGIN_PAGE;
	public static PeriodicalServices periodicalServices;
	public static final String vocabuarFilePath = EnvConst.RESOURCES_DIR + File.separator + "vocabulary.xml";

	private static HashMap<String, AppEnv> applications = new HashMap<String, AppEnv>();
	private static ConcurrentHashMap<String, AppEnv> allApplications = new ConcurrentHashMap<String, AppEnv>();

	private static HashMap<String, Object> cache = new HashMap<String, Object>();
	private static ArrayList<_Session> sess = new ArrayList<_Session>();
	private static boolean isDevMode;

	private static String officeFrameDir = "";
	private static String kernelDir = "";

	private static IExtUserDAO eDao;

	public static void init() {
		startTime = new Date();
		loadProperties();
		initProcess();
		try {
			IDatabase db = new Database();
			IDatabaseDeployer dd = new DatabaseDeployer(db);
			dd.deploy();
			adminApplication = new AppEnv(EnvConst.ADMINISTRATOR_APP_NAME, db);

		} catch (Exception e) {
			Server.logger.errorLogEntry(e);
			Server.shutdown();
		}
		eDao = initExtUserDAO();
		if (eDao != null) {
			Server.logger.debugLogEntry("Initialized extended users support (" + eDao.getClass().getSimpleName() + ")");
		} else {
			authMethod = AuthMethodType.LOGIN_PAGE;
		}
	}

	private static void initProcess() {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(true);
			SAXParser saxParser = factory.newSAXParser();
			SAXHandler cfgXMLhandler = new SAXHandler();
			File file = new File(EnvConst.CFG_FILE);
			if (!file.exists()) {
				Server.logger.fatalLogEntry(EnvConst.CFG_FILE + " has not been found (" + file.getAbsolutePath() + ")");
				Server.shutdown();
			}
			saxParser.parse(file, cfgXMLhandler);
			Document xmlDocument = getDocument();

			Server.logger.infoLogEntry("Initialize runtime environment");
			initMimeTypes();

			orgName = XMLUtil.getTextContent(xmlDocument, "/nextbase/orgname");
			if (orgName.isEmpty()) {
				orgName = EnvConst.APP_ID;
			}

			hostName = XMLUtil.getTextContent(xmlDocument, "/nextbase/hostname");
			if (hostName.isEmpty()) {
				hostName = getHostName();
			}

			String portAsText = XMLUtil.getTextContent(xmlDocument, "/nextbase/port");
			try {
				httpPort = Integer.parseInt(portAsText);
			} catch (NumberFormatException nfe) {

			}

			NodeList nodeList = XMLUtil.getNodeList(xmlDocument, "/nextbase/applications");
			if (nodeList.getLength() > 0) {
				org.w3c.dom.Element root = xmlDocument.getDocumentElement();
				NodeList nodes = root.getElementsByTagName("app");
				for (int i = 0; i < nodes.getLength(); i++) {
					Node appNode = nodes.item(i);
					if (XMLUtil.getTextContent(appNode, "name/@mode", false).equals("on")) {
						String appName = XMLUtil.getTextContent(appNode, "name", false);
						Site site = new Site();
						site.name = appName;
						site.siteName = XMLUtil.getTextContent(appNode, "name/@sitename", false);
						String restMode = XMLUtil.getTextContent(appNode, "rest/@mode");
						if (restMode.equalsIgnoreCase("on")) {
							site.setRestIsOn(RunMode.ON);
							site.setRestUrlMapping(XMLUtil.getTextContent(appNode, "rest/urlmapping"));
							site.setAllowCORS(XMLUtil.getTextContent(appNode, "rest/allowcors"));
							site.setRestType(RestType.JERSEY);
							List<String> restServices = new ArrayList<String>();
							NodeList servicesList = XMLUtil.getNodeList(appNode, "rest/services");
							for (int i1 = 0; i1 < servicesList.getLength(); i1++) {
								String serviceName = XMLUtil.getTextContent(servicesList.item(i1), "class", false);
								if (!serviceName.isEmpty()) {
									restServices.add(serviceName);
								}
							}
							site.setRestServices(restServices);
						}
						webAppToStart.put(appName, site);
					}
				}
			}

			NodeList l = XMLUtil.getNodeList(xmlDocument, "/nextbase/langs/entry");
			for (int i = 0; i < l.getLength(); i++) {
				String val = XMLUtil.getTextContent(l.item(i), ".", false);
				try {
					langs.add(LanguageCode.valueOf(val));
				} catch (IllegalArgumentException e) {
					Server.logger.errorLogEntry("Language constant \"" + val + "\" is not correct");
				}

			}

			try {
				isTLSEnable = XMLUtil.getTextContent(xmlDocument, "/nextbase/tls/@mode").equalsIgnoreCase("on");
				if (isTLSEnable) {
					String tlsPort = XMLUtil.getTextContent(xmlDocument, "/nextbase/tls/port");
					try {
						secureHttpPort = Integer.parseInt(tlsPort);
					} catch (NumberFormatException nfe) {
						secureHttpPort = EnvConst.DEFAULT_HTTP_PORT;
					}
					certFile = XMLUtil.getTextContent(xmlDocument, "/nextbase/tls/certfile");
					certKeyFile = XMLUtil.getTextContent(xmlDocument, "/nextbase/tls/certkeyfile");

					Server.logger.infoLogEntry("TLS is enabled");
					httpPort = secureHttpPort;
				}
			} catch (Exception ex) {
				Server.logger.infoLogEntry("TLS configuration error");
				isTLSEnable = false;
				certFile = "";
				certKeyFile = "";
			}

			try {
				mailEnable = XMLUtil.getTextContent(xmlDocument, "/nextbase/mail/@mode").equalsIgnoreCase("on") ? true : false;
				if (mailEnable) {
					SMTPHost = XMLUtil.getTextContent(xmlDocument, "/nextbase/mail/smtphost");
					Server.logger.warningLogEntry("SMTP host is not set");
					smtpAuth = Boolean.valueOf(XMLUtil.getTextContent(xmlDocument, "/nextbase/mail/smtpauth"));
					smtpUser = XMLUtil.getTextContent(xmlDocument, "/nextbase/mail/smtpuser");
					smtpPassword = XMLUtil.getTextContent(xmlDocument, "/nextbase/mail/smtppassword");
					smtpUserName = XMLUtil.getTextContent(xmlDocument, "/nextbase/mail/smtpusername", EnvConst.APP_ID + ", bot");
					smtpPort = XMLUtil.getTextContent(xmlDocument, "/nextbase/mail/smtpport");
					Server.logger.infoLogEntry("MailAgent is going to redirect some messages to host: " + SMTPHost);
				} else {
					Server.logger.infoLogEntry("MailAgent is switch off");
				}
			} catch (NumberFormatException nfe) {
				Server.logger.infoLogEntry("MailAgent is not set");
			}

			try {
				slackEnable = XMLUtil.getTextContent(xmlDocument, "/nextbase/slack/@mode").equalsIgnoreCase("on") ? true : false;
				if (slackEnable) {
					slackToken = XMLUtil.getTextContent(xmlDocument, "/nextbase/slack/token");
					if (slackToken.isEmpty()) {
						slackEnable = false;
						Server.logger.warningLogEntry("Slack token has not been provided");
					}
				} else {
					// Server.logger.infoLogEntry("mailAgent is switch off");
				}
			} catch (NumberFormatException nfe) {
				Server.logger.infoLogEntry("Slack setting is not correct");
			}

			File tmp = new File("tmp");
			if (!tmp.exists()) {
				tmp.mkdir();
			}

			tmpDir = tmp.getAbsolutePath();

			File jrDir = new File(tmpDir + File.separator + "trash");
			if (!jrDir.exists()) {
				jrDir.mkdir();
			}

			trash = jrDir.getAbsolutePath();

			Localizator lz = new Localizator();
			vocabulary = lz.populate();
			if (vocabulary == null) {
				vocabulary = new Vocabulary("system");
			}

		} catch (SAXException se) {
			Server.logger.errorLogEntry(se);
		} catch (ParserConfigurationException pce) {
			Server.logger.errorLogEntry(pce);
		} catch (IOException ioe) {
			Server.logger.errorLogEntry(ioe);
		}
	}

	public static void addApplication(AppEnv env) {
		applications.put(env.appName, env);
		allApplications.put(env.appName, env);
		allApplications.put(env.appName.toLowerCase(), env);
	}

	public static AppEnv getAppEnv(String appID) {
		return allApplications.get(appID);
	}

	public static AppEnv getApplication(String appID) {
		return applications.get(appID);
	}

	public static Collection<AppEnv> getApplications() {
		return new HashSet<AppEnv>(applications.values());
	}

	public static String getFullHostName() {
		String port = "";
		if (Environment.httpPort != 80) {
			port = ":" + Environment.httpPort;
		}
		return WebServer.httpSchema + "://" + Environment.hostName + port;
	}

	public static String getWorkspaceURL() {
		return "Workspace";
	}

	private static void initMimeTypes() {
		mimeHash.put("pdf", "application/pdf");
		mimeHash.put("doc", "application/msword");
		mimeHash.put("xls", "application/vnd.ms-excel");
		mimeHash.put("tif", "image/tiff");
		mimeHash.put("rtf", "application/msword");
		mimeHash.put("gif", "image/gif");
		mimeHash.put("jpg", "image/jpeg");
		mimeHash.put("html", "text/html");
		mimeHash.put("zip", "application/zip");
		mimeHash.put("rar", "application/x-rar-compressed");
	}

	private static Document getDocument() {
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;

			builder = domFactory.newDocumentBuilder();
			return builder.parse("cfg.xml");
		} catch (SAXException e) {
			Server.logger.errorLogEntry(e);
		} catch (IOException e) {
			Server.logger.errorLogEntry(e);
		} catch (ParserConfigurationException e) {
			Server.logger.errorLogEntry(e);
		}
		return null;
	}

	private static String getHostName() {
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			Server.logger.errorLogEntry(e);
		}
		return addr.getHostName();
	}

	@Override
	public void flush() {
		cache.clear();
	}

	public static void flushSessionsCach() {
		for (_Session ses : sess) {
			ses.flush();
		}
	}

	public static List<String> getSessionCachesInfo() {
		List<String> cachesList = new ArrayList<String>();
		for (_Session ses : sess) {
			String ci = ses.getCacheInfo();
			if (ci.equals("")) {
				ci = "cache is empty";
			}
			cachesList.add(ses.getUser().getUserID() + ":" + ci);
		}
		return cachesList;
	}

	public static List<String> getAppsCachesInfo() {
		List<String> cachesList = new ArrayList<String>();
		for (AppEnv env : applications.values()) {
			String ci = env.getCacheInfo();
			cachesList.add(env.appName + ":" + ci);
		}
		return cachesList;
	}

	public static String getCacheInfo() {
		String ci = "";
		for (String c : cache.keySet()) {
			// ci = ci + "," + c + "\n" + cache.get(c);
			ci = ci + "," + c;
		}
		if (ci.equals("")) {
			ci = "cache is empty";
		}
		return ci;
	}

	public static void shutdown() {
		// if (XMPPServerEnable) Environment.connection.disconnect();
	}

	public static IExtUserDAO getExtUserDAO() {
		return eDao;
	}

	private static void loadProperties() {
		Properties prop = new Properties();
		InputStream input = null;
		// TODO probably it need to improve
		// EnvConst.AUTH_COOKIE_NAME =
		// Util.generateRandomAsText("!'*-._qwertyuiopasdfghjklzxcvbnm1234567890",
		// NumberUtil.getRandomNumber(10, 20));
		try {

			input = new FileInputStream("resources" + File.separator + "config.properties");

			prop.load(input);
			Field[] declaredFields = EnvConst.class.getDeclaredFields();
			for (Field field : declaredFields) {
				if (Modifier.isStatic(field.getModifiers())) {
					String value = prop.getProperty(field.getName());
					if (value != null) {
						field.set(String.class, prop.getProperty(field.getName()));
					}
				}
			}
		} catch (FileNotFoundException e) {

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private static IExtUserDAO initExtUserDAO() {
		try {
			Class<?> clazz = Class.forName(EnvConst.STAFF_DAO_CLASS);
			@SuppressWarnings("rawtypes")
			Class[] args = new Class[] { _Session.class };
			Constructor<?> contructor = clazz.getConstructor(args);
			_Session ses = new _Session(adminApplication, new AnonymousUser());
			return (IExtUserDAO) contructor.newInstance(new Object[] { ses });
		} catch (ClassNotFoundException e) {
			Server.logger.warningLogEntry("Extended user's support DAO has not been initialized (" + EnvConst.STAFF_DAO_CLASS + ")");
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
		        | SecurityException e) {
			Server.logger.errorLogEntry(e);
		}
		return null;
	}

	@Override
	public PageOutcome getCachedPage(PageOutcome outcome, Page page, _WebFormData formData)
	        throws ClassNotFoundException, RuleException, IOException, SaxonApiException {
		String cacheKey = page.getCacheID();
		Object obj = cache.get(cacheKey);
		String cacheParam[] = formData.getFormData().get("cache");
		if (cacheParam == null) {
			PageOutcome buffer = page.getPageContent(outcome, formData, "GET");
			cache.put(cacheKey, buffer.getValue());
			return buffer;
		} else if (cacheParam[0].equalsIgnoreCase("reload")) {
			PageOutcome buffer = page.getPageContent(outcome, formData, "GET");
			cache.put(cacheKey, buffer.getValue());
			return buffer;
		} else {
			return (PageOutcome) obj;
		}

	}

	public static boolean isDevMode() {
		return isDevMode;
	}

	public static void setDevMode(boolean isDevMode) {
		Environment.isDevMode = isDevMode;
		Path parent = Paths.get(System.getProperty("user.dir")).getParent();
		officeFrameDir = parent + File.separator + EnvConst.OFFICEFRAME + File.separator;
		kernelDir = parent + File.separator + EnvConst.FRAMEWORK_NAME + File.separator;
	}

	public static String getOfficeFrameDir() {
		return officeFrameDir;
	}

	public static String getKernelDir() {
		return kernelDir;
	}

	public static String getXSLDir() {
		return "xsl" + File.separator;
	}

	public static File getServerXSLT(String xslt) {
		File xsltFile = new File(xslt);
		if (!xsltFile.exists()) {
			xsltFile = new File(kernelDir + "xsl" + File.separator + xslt);
		}

		return xsltFile;
	}

	public static String getDefaultRedirectURL() {
		return "/Workspace/p?id=workspace";
	}

}
