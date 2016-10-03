package administrator.page.form;

import com.exponentus.scripting._AppEntourage;
import com.exponentus.scripting._Exception;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoForm;

import administrator.dao.LanguageDAO;

public class LoginForm extends _DoForm {
	@Override
	public void doGET(_Session session, _WebFormData formData) throws _Exception {
		_AppEntourage ent = session.getAppEntourage();
		addValue("serverversion", ent.getServerVersion());
		addValue("build", ent.getBuildTime());
		addContent(new LanguageDAO(session).findAll());
	}
}
