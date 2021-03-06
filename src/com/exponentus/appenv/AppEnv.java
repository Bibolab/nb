package com.exponentus.appenv;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

import com.exponentus.caching.PageCacheAdapter;
import com.exponentus.dataengine.IDatabase;
import com.exponentus.dataengine.system.IMonitoringDAO;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.localization.Localizator;
import com.exponentus.localization.TemplatesSet;
import com.exponentus.localization.Vocabulary;
import com.exponentus.log.ILogger;
import com.exponentus.rule.RuleProvider;
import com.exponentus.scripting._Session;
import com.exponentus.server.Server;
import com.exponentus.user.SuperUser;

import groovy.lang.GroovyClassLoader;

public class AppEnv extends PageCacheAdapter {
	public boolean isValid;
	public String appName;
	public RuleProvider ruleProvider;
	public HashMap<String, File> xsltFileMap = new HashMap<>();
	public boolean isWorkspace;
	public Vocabulary vocabulary;
	public TemplatesSet templates;
	public static ILogger logger = Server.logger;
	private IDatabase dataBase;
	private String rulePath = "rule";
	private static final String[] extensions = { "groovy" };
	private String defaultPage;

	public AppEnv(String n, IDatabase db) {
		appName = n;
		this.dataBase = db;

		if (Environment.isDevMode()) {
			if (EnvConst.ADMINISTRATOR_APP_NAME.equals(appName)) {
				rulePath = Environment.getKernelDir() + "rule";
			} else if (ArrayUtils.contains(EnvConst.OFFICEFRAME_APPS, appName)) {
				rulePath = Environment.getOfficeFrameDir() + "rule";
				Server.logger.debugLogEntry("Server using  \"" + appName + "\" as external module (path=" + Environment.getOfficeFrameDir() + ")");
			}
		}

		if (appName.equals(EnvConst.WORKSPACE_NAME)) {
			isWorkspace = true;
		}

		if (appName.equals(EnvConst.MONITORING_NAME)) {
			try {
				Class<?> clazz = Class.forName(EnvConst.MONITORING_DAO_CLASS);
				@SuppressWarnings("rawtypes")
				Class[] args = new Class[] { _Session.class };
				Constructor<?> contructor = clazz.getConstructor(args);
				_Session ses = new _Session(this, new SuperUser());
				Environment.setMonitoringDAO((IMonitoringDAO) contructor.newInstance(new Object[] { ses }));
				Server.logger.infoLogEntry("Monitoring has been attached");
			} catch (ClassNotFoundException e) {
				Server.logger.warningLogEntry("Monitoring DAO has not been initialized (" + EnvConst.MONITORING_DAO_CLASS + ")");
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
			        | SecurityException e) {
				Server.logger.errorLogEntry(e);
			}
		}

		rulePath += File.separator + appName;

		try {
			ruleProvider = new RuleProvider(this);
			isValid = true;
		} catch (Exception e) {
			Server.logger.errorLogEntry(e);
		}

		loadVocabulary();
		loadTemplateSet();
		compileScenarios();
	}

	public IDatabase getDataBase() {
		return dataBase;
	}

	@Override
	public String toString() {
		return "[ ]" + Server.serverTitle + "-" + appName;
	}

	public void reloadVocabulary() {
		loadVocabulary();
	}

	public String getRulePath() {
		return rulePath;
	}

	public String getURL() {
		return Environment.getFullHostName() + "/" + appName;
	}

	private void loadVocabulary() {
		Localizator l = new Localizator();
		String vocabuarFilePath = getRulePath() + File.separator + "Resources" + File.separator + "vocabulary.xml";
		vocabulary = l.populate(appName, vocabuarFilePath);
	}

	private void loadTemplateSet() {
		String templatesFilePath = getRulePath() + File.separator + "Resources" + File.separator + "template";
		templates = new TemplatesSet(templatesFilePath);
	}

	private void compileScenarios() {
		ClassLoader parent = getClass().getClassLoader();
		CompilerConfiguration compiler = new CompilerConfiguration();
		String scriptDirPath = rulePath + File.separator + "Resources" + File.separator + "scripts";
		if (Environment.isDevMode()) {
			compiler.setTargetDirectory("bin");
		} else {
			compiler.setTargetDirectory(scriptDirPath);
		}
		GroovyClassLoader loader = new GroovyClassLoader(parent, compiler);

		File cur = new File(scriptDirPath);

		try {
			if (cur.exists() && cur.isDirectory()) {
				@SuppressWarnings("unchecked")
				Collection<File> scipts = FileUtils.listFiles(cur, extensions, true);
				for (File groovyFile : scipts) {
					try {
						Server.logger.debugLogEntry("Recompile " + groovyFile.getAbsolutePath() + "...");
						loader.parseClass(groovyFile);
					} catch (CompilationFailedException e) {
						AppEnv.logger.errorLogEntry(e);
					} catch (IOException e) {
						AppEnv.logger.errorLogEntry(e);
					}
				}
			}
		} finally {
			try {
				loader.close();
			} catch (IOException e) {
				Server.logger.errorLogEntry(e);
			}
		}
	}

	public String getDefaultPage() {
		return defaultPage;
	}

	public void setDefaultPage(String defaultPage) {
		this.defaultPage = defaultPage;
	}

}
