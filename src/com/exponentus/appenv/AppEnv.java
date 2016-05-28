package com.exponentus.appenv;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

import com.exponentus.caching.PageCacheAdapter;
import com.exponentus.dataengine.IDatabase;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.localization.Localizator;
import com.exponentus.localization.Vocabulary;
import com.exponentus.log.ILogger;
import com.exponentus.rule.RuleProvider;
import com.exponentus.server.Server;

import groovy.lang.GroovyClassLoader;

public class AppEnv extends PageCacheAdapter {
	public boolean isValid;
	public String appName;
	public RuleProvider ruleProvider;
	public HashMap<String, File> xsltFileMap = new HashMap<String, File>();
	public boolean isWorkspace;
	public Vocabulary vocabulary;
	public static ILogger logger = Server.logger;
	private IDatabase dataBase;
	private String rulePath = "rule";
	private static final String[] extensions = { "groovy" };

	public AppEnv(String n, IDatabase db) {
		this.appName = n;
		this.dataBase = db;

		if (Environment.isDevMode()) {
			if (EnvConst.ADMINISTRATOR_APP_NAME.equals(appName)) {
				rulePath = Environment.getKernelDir() + "rule";
			} else if (ArrayUtils.contains(EnvConst.OFFICEFRAME_APPS, appName)) {
				rulePath = Environment.getOfficeFrameDir() + "rule";
				Server.logger
				        .debugLogEntry("server going to use \"" + appName + "\" as external module (path=" + Environment.getOfficeFrameDir() + ")");
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
		Server.logger.infoLogEntry("Dictionary is reloading (" + appName + ")...");
		loadVocabulary();
	}

	public String getRulePath() {
		return rulePath;
	}

	private void loadVocabulary() {
		Localizator l = new Localizator();
		String vocabuarFilePath = getRulePath() + File.separator + "Resources" + File.separator + "vocabulary.xml";
		vocabulary = l.populate(appName, vocabuarFilePath);
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
						Server.logger.debugLogEntry("recompile " + groovyFile.getAbsolutePath() + "...");
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

}
