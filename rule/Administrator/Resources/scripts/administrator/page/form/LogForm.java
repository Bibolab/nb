package administrator.page.form;

import java.io.File;

import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;

public class LogForm extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		String dir = formData.getValueSilently("category");
		String docid = formData.getValueSilently("docid");

		String filePath = "." + File.separator + "logs" + File.separator + dir + File.separator + docid;
		showFile(filePath, docid);

	}

}
