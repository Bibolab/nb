package com.exponentus.server;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Date;

import org.apache.catalina.LifecycleException;

import com.exponentus.dataengine.IDatabase;
import com.exponentus.env.Environment;
import com.exponentus.env.Site;
import com.exponentus.log.ILogger;
import com.exponentus.log.Log4jLogger;
import com.exponentus.scheduler.PeriodicalServices;
import com.exponentus.webserver.WebServer;

public class Server {
	public static ILogger logger;
	public static final String serverVersion = "3.0.9.1";
	public static String compilationTime = "";
	public static final String serverTitle = "NextBase " + serverVersion;
	public static Date startTime = new Date();
	public static IDatabase dataBase;
	public static WebServer webServerInst;

	public static void start() throws MalformedURLException, LifecycleException, URISyntaxException {
		logger = new Log4jLogger("Server");
		logger.infoLogEntry(":-)");
		logger.infoLogEntry(serverTitle + " start");
		if (Environment.isDevMode()) {
			Environment.verboseLogging = true;
			logger.warningLogEntry("debug logging is turned on");

		}
		compilationTime = ((Log4jLogger) logger).getBuildDateTime();

		Environment.init();

		webServerInst = new WebServer();
		if (webServerInst.init(Environment.hostName)) {

			for (Site webApp : Environment.webAppToStart.values()) {
				// System.out.println(webApp.name);
				webServerInst.addApplication(webApp);
			}

			String info = webServerInst.initConnectors();
			Server.logger.debugLogEntry("web server started (" + info + ")");
			webServerInst.startContainer();

			Environment.periodicalServices = new PeriodicalServices();

			Thread thread = new Thread(new Console());
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		} else {
			shutdown();
		}
	}

	public static void main(String[] arg) {
		try {
			for (int i = 0; i < arg.length; i++) {
				if (arg[i].equals("developing")) {
					Environment.setDevMode(true);
				}
			}
			Server.start();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (LifecycleException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Throwable ex) {
			System.err.println("Uncaught exception - " + ex.getMessage());
			ex.printStackTrace(System.err);
		}
	}

	public static void shutdown() {
		logger.infoLogEntry("server is stopping ... ");

		Environment.shutdown();
		if (webServerInst != null) {
			webServerInst.stopContainer();
		}
		logger.infoLogEntry("bye, bye... ");
		System.exit(0);
	}
}
