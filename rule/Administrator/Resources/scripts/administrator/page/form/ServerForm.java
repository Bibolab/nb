package administrator.page.form;

import com.exponentus.common.page.form.Form;
import com.exponentus.env.Environment;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;

import kz.flabs.util.Util;

public class ServerForm extends Form {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		addValue("hostname", Environment.hostName);
		addValue("port", Environment.httpPort);
		addValue("tmpdir", Environment.tmpDir);
		addValue("orgname", Environment.orgName);
		addValue("database", Environment.dataBase.getInfo());
		addValue("devmode", Environment.isDevMode());
		addValue("officeframe", Environment.getOfficeFrameDir());
		addValue("kernel", Environment.getKernelDir());
		addValue("starttime", Util.convertDataTimeToString(Environment.startTime));
	}

	@Override
	public void doPOST(_Session session, _WebFormData formData) {
		devPrint(formData);

	}

}
