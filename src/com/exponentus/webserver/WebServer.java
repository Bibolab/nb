package com.exponentus.webserver;

import java.io.File;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletResponse;

import com.exponentus.webserver.valve.ContentEncoding;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.env.Site;
import com.exponentus.rest.ResourceLoader;
import com.exponentus.rule.constans.RunMode;
import com.exponentus.server.Server;
import com.exponentus.webserver.filter.CORSFilter;
import com.exponentus.webserver.servlet.Default;
import com.exponentus.webserver.valve.Logging;
import com.exponentus.webserver.valve.Secure;
import com.exponentus.webserver.valve.Unsecure;

import administrator.dao.LanguageDAO;
import administrator.model.Language;

public class WebServer {
	public static final String httpSchema = "http";
	public static final String httpSecureSchema = "https";

	private static Tomcat tomcat;
	private static Engine engine;

	// private static final String defaultWelcomeList[] = { "index.html",
	// "index.htm" };

	public boolean init(String defaultHostName) throws MalformedURLException, LifecycleException {
		Server.logger.debugLogEntry("Init webserver ...");

		tomcat = new Tomcat();
		tomcat.setPort(Environment.httpPort);
		tomcat.setHostname(defaultHostName);
		tomcat.setBaseDir("webserver");
		Host host = tomcat.getHost();
		host.setAutoDeploy(false);
		host.setDeployOnStartup(false);
		engine = tomcat.getEngine();

		StandardServer server = (StandardServer) tomcat.getServer();
		AprLifecycleListener listener = new AprLifecycleListener();
		server.addLifecycleListener(listener);

		if (!initSharedResources("/" + EnvConst.SHARED_RESOURCES_APP_NAME)) {
			Server.logger.fatalLogEntry("There is no \"" + EnvConst.SHARED_RESOURCES_APP_NAME + "\" resource");
			return false;
		}
		return true;

	}

	public boolean initSharedResources(String URLPath) throws LifecycleException, MalformedURLException {
		File docBase = null;
		if (Environment.isDevMode()) {
			docBase = new File(Environment.getKernelDir() + "webapps" + File.separator + EnvConst.SHARED_RESOURCES_APP_NAME);
		} else {
			docBase = new File("webapps" + File.separator + EnvConst.SHARED_RESOURCES_APP_NAME);
		}
		if (docBase.exists()) {
			String db = docBase.getAbsolutePath();
			Context sharedResContext = tomcat.addContext(URLPath, db);
			sharedResContext.setDisplayName(EnvConst.SHARED_RESOURCES_APP_NAME);

			Tomcat.addServlet(sharedResContext, "default", Default.class.getCanonicalName());
			sharedResContext.addServletMapping("/", "default");

			sharedResContext.addMimeMapping("css", "text/css");
			sharedResContext.addMimeMapping("js", "text/javascript");

			return true;
		} else {
			return false;
		}

	}

	public boolean initRestService(Site site, Context context) throws LifecycleException, MalformedURLException {
		if (site.getRestIsOn() == RunMode.ON) {
			ResourceConfig rc = new ResourceConfig(new ResourceLoader(site).getClasses());
			Wrapper w1 = Tomcat.addServlet(context, "REST", new ServletContainer(rc));
			w1.setLoadOnStartup(1);
			w1.addInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
			context.addServletMapping(site.getRestUrlMapping() + "/*", "REST");

			// TODO it needed to adding CORS mapping
			if (!site.getAllowCORS().isEmpty()) {
				rc.register(CORSFilter.class);
			}

		}
		return true;

	}

	public Host addApplication(Site site) throws LifecycleException, MalformedURLException {
		Context context = null;
		String docBase = site.name;
		String URLPath = "/" + docBase;
		String db = null;
		if (Environment.isDevMode()) {
			if (EnvConst.ADMINISTRATOR_APP_NAME.equals(docBase)) {
				db = new File(Environment.getKernelDir() + "webapps" + File.separator + docBase).getAbsolutePath();
			} else if (ArrayUtils.contains(EnvConst.OFFICEFRAME_APPS, docBase)) {
				db = new File(Environment.getOfficeFrameDir() + "webapps" + File.separator + docBase).getAbsolutePath();
			} else {
				db = new File("webapps" + File.separator + docBase).getAbsolutePath();
			}
		} else {
			db = new File("webapps" + File.separator + docBase).getAbsolutePath();
		}

		context = tomcat.addContext(URLPath, db);
		context.setDisplayName(URLPath.substring(1));
		context.addWelcomeFile("p");

		Tomcat.addServlet(context, "default", Default.class.getCanonicalName());
		context.addServletMapping("/", "default");

		Tomcat.addServlet(context, "Provider", "com.exponentus.webserver.servlet.Provider");
		context.addServletMapping("/Provider", "Provider");
		context.addServletMapping("/p", "Provider");

		Tomcat.addServlet(context, "Login", "com.exponentus.webserver.servlet.Login");
		context.addServletMapping("/Login", "Login");

		Tomcat.addServlet(context, "Logout", "com.exponentus.webserver.servlet.Logout");
		context.addServletMapping("/Logout", "Logout");

		Wrapper w = Tomcat.addServlet(context, "PortalInit", "com.exponentus.webserver.servlet.PortalInit");
		w.setLoadOnStartup(1);

		context.addServletMapping("/PortalInit", "PortalInit");

		Tomcat.addServlet(context, "UploadFile", "com.exponentus.webserver.servlet.UploadFile");
		context.addServletMapping("/UploadFile", "UploadFile");
		context.addServletMapping("/uf", "UploadFile");

		Tomcat.addServlet(context, "Error", "com.exponentus.webserver.servlet.Error");
		context.addServletMapping("/Error", "Error");
		context.addServletMapping("/e", "Error");

		context.addMimeMapping("css", "text/css");
		context.addMimeMapping("js", "text/javascript");
		context.addMimeMapping("html", "text/html");

		initErrorPages(context);

		initRestService(site, context);

		return null;
	}

	public void initDefaultURL() throws MalformedURLException, LifecycleException {
		Context defaultContext = null;
		if (!"".equals(EnvConst.WELCOME_APPLICATION)) {
			defaultContext = tomcat.addContext(tomcat.getHost(), "",
			        new File("webapps" + File.separator + EnvConst.WELCOME_APPLICATION).getAbsolutePath());

			Tomcat.addServlet(defaultContext, "Provider", "com.exponentus.webserver.servlet.Provider");
			defaultContext.addServletMapping("/Provider", "Provider");
			defaultContext.addServletMapping("/p", "Provider");

			Tomcat.addServlet(defaultContext, "lang", "com.exponentus.webserver.servlet.Lang");
			LanguageDAO lDao = new LanguageDAO();
			for (Language l : lDao.findAll()) {
				defaultContext.addServletMapping("/" + l.getCode().name().toLowerCase(), "lang");
				defaultContext.addServletMapping("/" + l.getCode().name(), "lang");
			}

			Wrapper w = Tomcat.addServlet(defaultContext, "PortalInit", "com.exponentus.webserver.servlet.PortalInit");
			w.setLoadOnStartup(1);

			defaultContext.setDisplayName("welcome");

			defaultContext.addWelcomeFile("p");

			initRestService(Environment.webAppToStart.get(EnvConst.WELCOME_APPLICATION), defaultContext);

		} else {
			defaultContext = tomcat.addContext(tomcat.getHost(), "", new File("webapps/ROOT").getAbsolutePath());
			defaultContext.addWelcomeFile("r");
			defaultContext.setDisplayName("root");

			Tomcat.addServlet(defaultContext, "Redirector", "com.exponentus.webserver.servlet.Redirector");
			defaultContext.addServletMapping("/Redirector", "Redirector");
			defaultContext.addServletMapping("/r", "Redirector");
		}

		engine.getPipeline().addValve(new Logging());
		engine.getPipeline().addValve(new Unsecure());
		engine.getPipeline().addValve(new Secure());
		engine.getPipeline().addValve(new ContentEncoding());

		Tomcat.addServlet(defaultContext, "Error", "com.exponentus.webserver.servlet.Error");
		defaultContext.addServletMapping("/Error", "Error");
		defaultContext.addServletMapping("/e", "Error");

		initErrorPages(defaultContext);

		Tomcat.addServlet(defaultContext, "default", "org.apache.catalina.servlets.DefaultServlet");
		defaultContext.addServletMapping("/", "default");
	}

	public String initConnectors() {
		String portInfo = "";
		if (Environment.isTLSEnable) {
			Connector secureConnector = null;
			Server.logger.infoLogEntry("TLS has been enabled");
			secureConnector = tomcat.getConnector();
			secureConnector.setPort(Environment.secureHttpPort);
			secureConnector.setScheme(httpSecureSchema);
			secureConnector.setProtocol("org.apache.coyote.http11.Http11AprProtocol");
			secureConnector.setSecure(true);
			secureConnector.setEnableLookups(false);
			secureConnector.setSecure(true);
			secureConnector.setProperty("SSLEnabled", "true");
			secureConnector.setProperty("SSLCertificateFile", Environment.certFile);
			secureConnector.setProperty("SSLCertificateKeyFile", Environment.certKeyFile);
			tomcat.setConnector(secureConnector);

			portInfo = httpSecureSchema + "://" + tomcat.getHost().getName() + ":" + Integer.toString(Environment.secureHttpPort);
		} else {
			portInfo = Environment.getFullHostName();
		}

		// gzip content
		tomcat.getConnector().setProperty("compression", "on");
		tomcat.getConnector().setProperty("compressionMinSize", "2048");
		tomcat.getConnector().setProperty("noCompressionUserAgents", "gozilla, traviata");
		tomcat.getConnector().setProperty("compressableMimeType", "text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json");

		return portInfo;

	}

	public void startContainer() {
		try {
			tomcat.start();
		} catch (UnsatisfiedLinkError e) {
			Server.logger.debugLogEntry("tcnative-1.dll has been not linked");
		} catch (LifecycleException e) {
			Server.logger.errorLogEntry(e);
		} catch (Exception e) {
			Server.logger.errorLogEntry(e);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				stopContainer();
			}
		});
	}

	public synchronized void stopContainer() {
		try {
			if (tomcat != null) {
				tomcat.stop();
			}
		} catch (LifecycleException exception) {
			Server.logger.errorLogEntry("cannot stop WebServer" + exception.getMessage());
		}

	}

	private void initErrorPages(Context context) {
		ErrorPage er = new ErrorPage();
		er.setErrorCode(HttpServletResponse.SC_NOT_FOUND);
		er.setLocation("/e");
		context.addErrorPage(er);
		ErrorPage er401 = new ErrorPage();
		er401.setErrorCode(HttpServletResponse.SC_UNAUTHORIZED);
		er401.setLocation("/e");
		context.addErrorPage(er401);
		ErrorPage er400 = new ErrorPage();
		er400.setErrorCode(HttpServletResponse.SC_BAD_REQUEST);
		er400.setLocation("/e");
		context.addErrorPage(er400);
		ErrorPage er500 = new ErrorPage();
		er500.setErrorCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		er500.setLocation("/e");
		context.addErrorPage(er500);
	}

}
