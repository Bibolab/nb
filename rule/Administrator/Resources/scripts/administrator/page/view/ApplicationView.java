package administrator.page.view;

import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.actions._Action;
import com.exponentus.scripting.actions._ActionBar;
import com.exponentus.scripting.actions._ActionType;
import com.exponentus.scripting.event._DoPage;

import administrator.dao.ApplicationDAO;

public class ApplicationView extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		ApplicationDAO dao = new ApplicationDAO(session);
		String keyword = formData.getValueSilently("keyword");
		_ActionBar actionBar = new _ActionBar(session);
		_Action newDocAction = new _Action("New application", "Registration of the new application", "new_app");
		newDocAction.setURL("Provider?id=application-form");
		actionBar.addAction(newDocAction);
		actionBar.addAction(new _Action("Delete", "Delete application", _ActionType.DELETE_DOCUMENT));
		addContent(actionBar);
		addContent(dao.findAll(), 0, dao.getCount(), 0, keyword);
	}
}
