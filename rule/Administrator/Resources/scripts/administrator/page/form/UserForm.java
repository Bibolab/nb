package administrator.page.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.exponentus.env.EnvConst;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scheduler._EnumWrapper;
import com.exponentus.scripting._Exception;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._Validation;
import com.exponentus.scripting._Validator;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.actions._Action;
import com.exponentus.scripting.actions._ActionBar;
import com.exponentus.scripting.actions._ActionType;
import com.exponentus.scripting.event._DoPage;
import com.exponentus.user.IUser;

import administrator.dao.ApplicationDAO;
import administrator.dao.UserDAO;
import administrator.model.Application;
import administrator.model.User;

/**
 * @author Kayra created 05-03-2016
 */

public class UserForm extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		String id = formData.getValueSilently("docid");
		IUser<Long> entity;
		if (!id.isEmpty()) {
			UserDAO dao = new UserDAO(session);
			entity = dao.findById(Long.parseLong(id));
		} else {
			entity = new User();
			entity.setRegDate(new Date());
			entity.setLogin("");
			entity.setDefaultLang(LanguageCode.valueOf(EnvConst.DEFAULT_LANG));
			entity.setEditable(true);
		}
		addContent((User) entity);
		addContent(new _EnumWrapper<>(LanguageCode.class.getEnumConstants()));
		addContent(new ApplicationDAO(session).findAll());
		_ActionBar actionBar = new _ActionBar(session);
		actionBar.addAction(new _Action("Save &amp; Close", "Recompile the class and save", _ActionType.SAVE_AND_CLOSE));
		actionBar.addAction(new _Action("Close", "Just close the form", _ActionType.CLOSE));
		addContent(actionBar);
	}

	@Override
	public void doPOST(_Session session, _WebFormData formData) {
		devPrint(formData);
		try {
			_Validation ve = validate(formData, session.getLang());
			if (ve.hasError()) {
				setBadRequest();
				setValidation(ve);
				return;
			}

			int id = formData.getNumberValueSilently("docid", -1);
			UserDAO dao = new UserDAO(session);
			User entity;

			boolean isNew = id == -1;
			if (isNew) {
				entity = new User();
			} else {
				entity = (User) dao.findById(id);
			}

			entity.setLogin(formData.getValue("login"));
			entity.setEmail(formData.getValue("email"));
			entity.setDefaultLang(LanguageCode.valueOf(formData.getValue("defaultlang")));
			entity.setXmpp(formData.getValue("xmpp"));
			entity.setSlack(formData.getValue("slack"));
			entity.setPwd(formData.getValue("pwd"));
			List<Application> apps = new ArrayList<Application>();
			ApplicationDAO aDao = new ApplicationDAO(session);
			for (String appId : formData.getListOfValuesSilently("app")) {
				if (!appId.isEmpty()) {
					Application application = aDao.findById(appId);
					if (application != null) {
						apps.add(application);
					}
				}
			}
			entity.setAllowedApps(apps);

			if (isNew) {
				dao.add(entity);
			} else {
				dao.update(entity);
			}

			setRedirect("p?id=user-view");
		} catch (_Exception e) {
			logError(e);
		}
	}

	private _Validation validate(_WebFormData formData, LanguageCode lang) {
		_Validation ve = new _Validation();

		if (formData.getValueSilently("login").isEmpty()) {
			ve.addError("login", "required", getLocalizedWord("required", lang));
		}
		if (formData.getValueSilently("email").isEmpty() || !_Validator.checkEmail(formData.getValueSilently("email"))) {
			ve.addError("email", "email", getLocalizedWord("email_invalid", lang));
		}
		if (!formData.getValueSilently("pwd").isEmpty()) {
			if (formData.getValueSilently("pwd_confirm").isEmpty()) {
				ve.addError("pwd_confirm", "required", getLocalizedWord("required", lang));
			} else if (!formData.getValueSilently("pwd").equals(formData.getValueSilently("pwd_confirm"))) {
				ve.addError("pwd_confirm", "required", getLocalizedWord("password_confirm_not_equals", lang));
			}
		}

		return ve;
	}
}
