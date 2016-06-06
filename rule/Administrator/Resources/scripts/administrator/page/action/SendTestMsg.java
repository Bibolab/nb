package administrator.page.action;

import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;

public class SendTestMsg extends _DoPage {

	@Override
	public void doPOST(_Session session, _WebFormData formData) {
		String id = formData.getValueSilently("type");
		String address = formData.getValueSilently("addr");

	}

}
