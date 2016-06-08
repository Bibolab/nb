package administrator.page.view;

import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;

import administrator.dao.ApplicationDAO;

public class ApplicationView extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		ApplicationDAO dao = new ApplicationDAO(session);
		String keyword = formData.getValueSilently("keyword");
		addContent(dao.findAll(), 0, dao.getCount(), 0, keyword);
	}
}
