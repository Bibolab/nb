package com.exponentus.common.page.view;

import java.util.UUID;

import com.exponentus.dataengine.jpa.AppEntity;
import com.exponentus.dataengine.jpa.ViewPage;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._POJOListWrapper;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.actions._Action;
import com.exponentus.scripting.actions._ActionBar;
import com.exponentus.scripting.event._DoPage;

import kz.flabs.dataengine.IDatabase;
import kz.flabs.dataengine.IFTIndexEngine;

/**
 * @author Kayra created 26-03-2016
 */

public class FTSearch extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		LanguageCode lang = session.getLang();
		String keyWord = formData.getValueSilently("keyword");
		if (keyWord.isEmpty()) {
			setBadRequest();
			return;
		}
		int pageNum = formData.getNumberValueSilently("page", 1);
		int pageSize = session.pageSize;

		IDatabase db = session.getDatabase();
		IFTIndexEngine ftEngine = db.getFTSearchEngine();
		ViewPage<?> result = ftEngine.search(keyWord, session, pageNum, pageSize);

		addContent(new _ActionBar(session)
		        .addAction(new _Action(getLocalizedWord("back_to_doc_list", lang), getLocalizedWord("back", lang), "reset_search")));
		if (result != null) {
			ViewPage<AppEntity<UUID>> res = (ViewPage<AppEntity<UUID>>) result;
			addContent(new _POJOListWrapper<>(res.getResult(), res.getMaxPage(), res.getCount(), res.getPageNum(), session, keyWord));
		} else {
			addContent(new _POJOListWrapper(getLocalizedWord("ft_search_resturn_null", lang) + ": '" + keyWord + "'", keyWord));
		}
		addValue("request_param", "keyword=" + keyWord);
	}
}
