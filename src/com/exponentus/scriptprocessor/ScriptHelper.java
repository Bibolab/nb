package com.exponentus.scriptprocessor;

import com.exponentus.env.Environment;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.server.Server;

import kz.flabs.localization.Vocabulary;
import kz.flabs.scriptprocessor.ScriptProcessor;

public class ScriptHelper {
	protected Vocabulary vocabulary;
	protected String redirectURL = "";
	private _Session session;

	public String getTmpDirPath() {
		return Environment.tmpDir;
	}

	public _Session getSes() {
		return session;
	}

	public void setSes(_Session ses) {
		this.session = ses;
		vocabulary = ses.getAppEnv().vocabulary;
	}

	public String getWord(String word, Vocabulary vocabulary, String lang) {
		try {
			return vocabulary.getSentenceCaption(word, lang).word;
		} catch (Exception e) {
			return word.toString();
		}
	}

	public String getLocalizedWord(String word, LanguageCode lang) {
		return getWord(word, vocabulary, lang.name());
	}

	public void devPrint(Object text) {
		if (Environment.isDevMode()) {
			System.out.println(text.toString());
		}
	}

	public void println(Object text) {
		System.out.println(text.toString());
	}

	public void log(String text) {
		Server.logger.infoLogEntry(text);
	}

	public static void error(Exception e) {
		ScriptProcessor.logger.errorLogEntry(e);
	}

}
