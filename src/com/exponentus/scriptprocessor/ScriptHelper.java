package com.exponentus.scriptprocessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.exponentus.dataengine.RuntimeObjUtil;
import com.exponentus.dataengine.jpa.DAO;
import com.exponentus.dataengine.jpa.SecureAppEntity;
import com.exponentus.env.Environment;
import com.exponentus.localization.LanguageCode;
import com.exponentus.localization.Vocabulary;
import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting._POJOListWrapper;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.actions._Action;
import com.exponentus.scripting.actions._ActionBar;
import com.exponentus.scripting.actions._ActionType;
import com.exponentus.server.Server;

import administrator.dao.LanguageDAO;
import administrator.model.Language;

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

	protected Map<LanguageCode, String> getLocalizedNames(_Session session, _WebFormData formData) {
		Map<LanguageCode, String> localizedNames = new HashMap<LanguageCode, String>();
		List<Language> langs = new LanguageDAO(session).findAll();
		for (Language l : langs) {
			String ln = formData.getValueSilently(l.getCode().name().toLowerCase() + "localizedname");
			if (!ln.isEmpty()) {
				localizedNames.put(l.getCode(), ln);
			} else {
				localizedNames.put(l.getCode(), formData.getValueSilently("name"));
			}
		}
		return localizedNames;
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

	protected IPOJOObject getACL(_Session ses, SecureAppEntity<UUID> entity) {
		return entity.getACL(ses);
	}

	protected _ActionBar getSimpleActionBar(_Session session, String type, LanguageCode lang) {
		_ActionBar actionBar = new _ActionBar(session);
		_Action newDocAction = new _Action(getLocalizedWord("new_", lang), "", "new_" + type);
		newDocAction.setURL("p?id=" + type);
		actionBar.addAction(newDocAction);
		actionBar.addAction(new _Action(getLocalizedWord("del_document", lang), "", _ActionType.DELETE_DOCUMENT));
		return actionBar;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected _POJOListWrapper<? extends IPOJOObject> getViewPage(DAO<? extends IPOJOObject, UUID> dao, _WebFormData formData) {
		int pageNum = 1;
		int pageSize = dao.getSession().pageSize;
		if (formData.containsField("page")) {
			pageNum = formData.getNumberValueSilently("page", pageNum);
		}
		long count = dao.getCount();
		int maxPage = RuntimeObjUtil.countMaxPage(count, pageSize);
		if (pageNum == 0) {
			pageNum = maxPage;
		}
		int startRec = RuntimeObjUtil.calcStartEntry(pageNum, pageSize);
		List<? extends IPOJOObject> list = dao.findAll(startRec, pageSize);
		return new _POJOListWrapper(list, maxPage, count, pageNum, getSes());
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

	public static void logError(Exception e) {
		Server.logger.errorLogEntry(e);
	}

}
