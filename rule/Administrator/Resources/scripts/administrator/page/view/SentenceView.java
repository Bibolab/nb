package administrator.page.view;

import java.util.List;

import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting._POJOListWrapper;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;

import administrator.dao.SentenceDAO;

public class SentenceView extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		SentenceDAO dao = new SentenceDAO(session);
		String keyword = formData.getValueSilently("keyword");
		List<? extends IPOJOObject> list = dao.findAll();
		addContent(new _POJOListWrapper(list, 0, dao.getCount(), 0, session, keyword));
	}
}
