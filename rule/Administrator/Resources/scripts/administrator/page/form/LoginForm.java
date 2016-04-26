package administrator.page.form;

import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._AppEntourage;
import com.exponentus.scripting._Exception;
import com.exponentus.scripting._POJOListWrapper;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;

import administrator.dao.LanguageDAO;

public class LoginForm extends _DoPage {
	@Override
	public void doGET(_Session session, _WebFormData formData) throws _Exception {
		_AppEntourage ent = session.getAppEntourage();
		addValue("serverversion", ent.getServerVersion());
		addValue("build", ent.getBuildTime());

		String lang = formData.getValueSilently("lang");
		if (!lang.isEmpty()) {
			session.setLang(LanguageCode.valueOf(lang));
		}

		addContent(new _POJOListWrapper(new LanguageDAO(session).findAll(), session));
	}
}
