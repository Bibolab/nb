package com.exponentus.common.page.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;

import administrator.dao.LanguageDAO;
import administrator.model.Language;

public abstract class Form extends _DoPage {

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

	@Override
	public abstract void doGET(_Session session, _WebFormData formData);

	@Override
	public abstract void doPOST(_Session session, _WebFormData formData);
}
