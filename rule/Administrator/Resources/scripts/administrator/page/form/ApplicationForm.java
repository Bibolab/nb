package administrator.page.form;

import java.util.UUID;

import org.eclipse.persistence.exceptions.DatabaseException;

import com.exponentus.dataengine.jpa.constants.AppCode;
import com.exponentus.exception.SecureException;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scheduler._EnumWrapper;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._Validation;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.actions._Action;
import com.exponentus.scripting.actions._ActionBar;
import com.exponentus.scripting.actions._ActionType;
import com.exponentus.scripting.event._DoPage;
import com.exponentus.util.Util;

import administrator.dao.ApplicationDAO;
import administrator.model.Application;

public class ApplicationForm extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		String id = formData.getValueSilently("docid");
		Application entity;
		if (!id.isEmpty()) {
			ApplicationDAO dao = new ApplicationDAO(session);
			entity = dao.findById(UUID.fromString(id));
		} else {
			entity = new Application();
			entity.setName("");
		}
		addContent(entity);
		addContent(new _EnumWrapper<>(AppCode.class.getEnumConstants()));
		_ActionBar actionBar = new _ActionBar(session);
		actionBar.addAction(new _Action("Save &amp; Compile &amp; Close", "Recompile the class and save", _ActionType.SAVE_AND_CLOSE));
		actionBar.addAction(new _Action("Close", "Just close the form", _ActionType.CLOSE));
		addContent(actionBar);
		startSaveFormTransact(entity);
	}

	@Override
	public void doPOST(_Session session, _WebFormData formData) {
		devPrint(formData);
		_Validation ve = validate(formData, session.getLang());
		if (ve.hasError()) {
			setBadRequest();
			setValidation(ve);
			return;
		}

		boolean isNew = false;
		String id = formData.getValueSilently("docid");
		ApplicationDAO dao = new ApplicationDAO(session);
		Application entity;

		if (id.isEmpty()) {
			isNew = true;
			entity = new Application();
		} else {
			entity = dao.findById(UUID.fromString(id));
		}

		entity.setName(formData.getValueSilently("name"));
		entity.setPosition(Util.convertStringToInt(formData.getValueSilently("position"), 99));
		entity.setDefaultURL(formData.getValueSilently("defaulturl").replace("&", "&amp;"));
		entity.setLocalizedName(getLocalizedNames(session, formData));

		try {
			if (isNew) {
				dao.add(entity);
			} else {
				dao.update(entity);
			}
			setRedirect("Provider?id=application-view");
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

	@Override
	public void doPUT(_Session session, _WebFormData formData) {

	}

	@Override
	public void doDELETE(_Session session, _WebFormData formData) {

	}
}
