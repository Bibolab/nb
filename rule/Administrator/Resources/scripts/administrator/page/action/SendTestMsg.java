package administrator.page.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

			Map<String, String> vars = new HashMap<String, String>();
			Memo memo = new Memo(testMsg, testMsg + " " + Util.convertDataTimeToString(new Date()), vars);

			try {
				if (!ma.sendMеssageSync(memo, recipients)) {
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
			sa.sendMessage(address, testMsg);

		}

	}
}