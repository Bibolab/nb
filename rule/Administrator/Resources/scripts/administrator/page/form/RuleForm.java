package administrator.page.form;

import java.util.UUID;

import com.exponentus.appenv.AppEnv;
import com.exponentus.env.Environment;
import com.exponentus.exception.RuleException;
import com.exponentus.localization.LanguageCode;
import com.exponentus.rule.page.PageRule;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._Validation;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.actions._Action;
import com.exponentus.scripting.actions._ActionBar;
import com.exponentus.scripting.actions._ActionType;
import com.exponentus.scripting.event._DoForm;

import administrator.dao.LanguageDAO;
import administrator.model.Language;

public class RuleForm extends _DoForm {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		String id = formData.getValueSilently("docid");
		String application = formData.getValueSilently("application");
		AppEnv appEnv = Environment.getApplication(application);
		try {
			PageRule rule = appEnv.ruleProvider.getRule(id);
			addContent(rule);
			_ActionBar actionBar = new _ActionBar(session);
			actionBar.addAction(new _Action("Save &amp; close", "Save and close form", _ActionType.SAVE_AND_CLOSE));
			actionBar.addAction(new _Action("Close", "Just close the form", _ActionType.CLOSE));
			addContent(actionBar);
		} catch (RuleException e) {
			logError(e);
			setBadRequest();
		}
	}

	@Override
	public void doPOST(_Session session, _WebFormData formData) {
		_Validation ve = validate(formData, session.getLang());
		if (ve.hasError()) {
			setBadRequest();
			setValidation(ve);
			return;
		}

		try {
			boolean isNew = false;
			String id = formData.getValueSilently("docid");
			LanguageDAO dao = new LanguageDAO(session);
			Language entity;

			if (id.isEmpty()) {
				isNew = true;
				entity = new Language();
			} else {
				entity = dao.findById(UUID.fromString(id));
			}

			if (isNew) {
				dao.add(entity);
			} else {
				dao.update(entity);
			}
		} catch (Exception e) {
			setBadRequest();
			logError(e);
		}
	}

	protected _Validation validate(_WebFormData formData, LanguageCode lang) {
		_Validation ve = new _Validation();

		if (formData.getValueSilently("name").isEmpty()) {
			ve.addError("name", "required", getLocalizedWord("required", lang));
		}

		return ve;
	}
}
