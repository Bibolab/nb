package administrator.page.view;

import com.exponentus.dataengine.jpa.ViewPage;
import com.exponentus.scripting._POJOListWrapper;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.actions._Action;
import com.exponentus.scripting.actions._ActionBar;
import com.exponentus.scripting.actions._ActionType;
import com.exponentus.scripting.event._DoPage;

import administrator.dao.UserDAO;
import administrator.model.User;

/**
 * @author Kayra created 04-01-2016
 */

public class UserView extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		_ActionBar actionBar = new _ActionBar(session);
		_Action newDocAction = new _Action("New user", "Registration of the new user", "new_user");
		newDocAction.setURL("Provider?id=user-form");
		actionBar.addAction(newDocAction);
		actionBar.addAction(new _Action("Delete", "Delete user", _ActionType.DELETE_DOCUMENT));

		UserDAO dao = new UserDAO();
		int pageNum = formData.getNumberValueSilently("page", 1);
		int pageSize = session.pageSize;
		String keyword = formData.getValueSilently("keyword");
		addContent(actionBar);
		ViewPage<User> vp = dao.findAll(keyword, pageNum, pageSize);
		addContent(new _POJOListWrapper(vp.getResult(), vp.getMaxPage(), vp.getCount(), vp.getPageNum(), session, keyword));
	}

	@Override
	public void doDELETE(_Session session, _WebFormData formData) {
		UserDAO dao = new UserDAO();
		for (String id : formData.getListOfValuesSilently("docid")) {
			User m = (User) dao.findById(Long.parseLong(id));
			dao.delete(m);
		}
	}
}
