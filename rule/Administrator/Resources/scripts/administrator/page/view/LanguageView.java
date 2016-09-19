package administrator.page.view;

import java.util.List;

import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting._POJOListWrapper;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.actions._Action;
import com.exponentus.scripting.actions._ActionBar;
import com.exponentus.scripting.actions._ActionType;
import com.exponentus.scripting.event._DoPage;

import administrator.dao.LanguageDAO;

public class LanguageView extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {

		LanguageDAO dao = new LanguageDAO(session);
		String keyword = formData.getValueSilently("keyword");
		List<? extends IPOJOObject> list = dao.findAll();
		_ActionBar actionBar = new _ActionBar(session);
		_Action newDocAction = new _Action("New language", "Registration of the new language", "new_lang");
		newDocAction.setURL("Provider?id=language-form");
		actionBar.addAction(newDocAction);
		actionBar.addAction(new _Action("Delete", "Delete language", _ActionType.DELETE_DOCUMENT));
		addContent(actionBar);
		addContent(new _POJOListWrapper(list, 0, dao.getCount(), 0, session, keyword));
	}
}
