package administrator.page.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.exponentus.env.EnvConst;
import com.exponentus.messaging.email.MailAgent;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;
import com.exponentus.util.Util;

public class SendTestMsg extends _DoPage {

	@Override
	public void doPOST(_Session session, _WebFormData formData) {
		devPrint(formData);
		String type = formData.getValueSilently("type");
		String address = formData.getValueSilently("addr");
		if (type.equalsIgnoreCase("email")) {
			List<String> recipients = new ArrayList<String>();
			recipients.add(address);
			MailAgent ma = new MailAgent();
			if (!ma.sendMail(recipients, "this is test message from " + EnvConst.APP_ID,
			        "this is test message " + Util.convertDataTimeToString(new Date()), false)) {
				addValue("result", "ok");

			}
		} else if (type.equalsIgnoreCase("xmpp")) {

		} else if (type.equalsIgnoreCase("slack")) {

		}

	}

}
