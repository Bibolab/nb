package administrator.page.other;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.exponentus.env.EnvConst;
import com.exponentus.exception.MsgException;
import com.exponentus.messaging.email.MailAgent;
import com.exponentus.messaging.email.Memo;
import com.exponentus.messaging.slack.SlackAgent;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;
import com.exponentus.util.Util;

public class SendTestMsg extends _DoPage {
	private static String testMsg = "this is test message from " + EnvConst.APP_ID;

	@Override
	public void doPOST(_Session session, _WebFormData formData) {
		devPrint(formData);
		String type = formData.getValueSilently("type");
		String address = formData.getValueSilently("addr");
		if (type.equalsIgnoreCase("email")) {
			List<String> recipients = new ArrayList<String>();
			recipients.add(address);
			MailAgent ma = new MailAgent();

			try {
				Memo memo = new Memo();
				if (ma.sendMеssageSync(recipients, testMsg, memo.getBody(testMsg + " " + Util.convertDataTimeToString(new Date())))) {
					addValue("The message has been sent succesfully");
				} else {
					addWarning("The message has not been sent");
				}
			} catch (MsgException e) {
				logError(e);
				setBadRequest();
			}

		} else if (type.equalsIgnoreCase("xmpp")) {

		} else if (type.equalsIgnoreCase("slack")) {
			SlackAgent sa = new SlackAgent();
			sa.sendMеssage(address, testMsg);

		}

	}
}