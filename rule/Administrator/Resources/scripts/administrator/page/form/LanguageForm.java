package administrator.page.form;

import java.util.UUID;

import org.eclipse.persistence.exceptions.DatabaseException;

import com.exponentus.exception.SecureException;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._Validation;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.actions._Action;
import com.exponentus.scripting.actions._ActionBar;
import com.exponentus.scripting.actions._ActionType;
import com.exponentus.scripting.event._DoForm;

import administrator.dao.LanguageDAO;
import administrator.model.Language;

public class LanguageForm extends _DoForm {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		String id = formData.getValueSilently("docid");
		Language entity;
		if (!id.isEmpty()) {
			LanguageDAO dao = new LanguageDAO(session);
			entity = dao.findById(UUID.fromString(id));
		} else {
			entity = new Language();
		}
		addContent(entity);
		_ActionBar actionBar = new _ActionBar(session);
		actionBar.addAction(new _Action("Save &amp; Compile &amp; Close", "Recompile the class and save", _ActionType.SAVE_AND_CLOSE));
		actionBar.addAction(new _Action("Close", "Just close the form", _ActionType.CLOSE));
		addContent(actionBar);
	}

	@Override
	public void doPOST(_Session session, _WebFormData formData) {
		_Validation ve = validate(formData, session.getLang());
		if (ve.hasError()) {
			setBadRequest();
			setValidation(ve);
			return;
		}

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

		try {
			if (isNew) {
				dao.add(entity);
			} else {
				dao.update(entity);
			}
		} catch (DatabaseException | SecureException e) {

			setError(e);
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
