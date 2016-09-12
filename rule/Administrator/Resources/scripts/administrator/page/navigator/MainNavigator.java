package administrator.page.navigator;

import java.util.LinkedList;

import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;
import com.exponentus.scripting.outline._Outline;
import com.exponentus.scripting.outline._OutlineEntry;
import com.exponentus.scriptprocessor.page.IOutcomeObject;

import administrator.dao.ApplicationDAO;
import administrator.model.Application;

public class MainNavigator extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		LanguageCode lang = session.getLang();
		LinkedList<IOutcomeObject> list = new LinkedList<IOutcomeObject>();

		_Outline common_outline = new _Outline(getLocalizedWord("Server settings", lang), "server");
		common_outline.addEntry(new _OutlineEntry("Server parameters", "General server parameters", "server", "p?id=server-form"));
		common_outline.addEntry(new _OutlineEntry("User", "user-view"));
		common_outline.addEntry(new _OutlineEntry("Applications", "application-view"));
		common_outline.addEntry(new _OutlineEntry("Languages", "language-view"));
		// common_outline.addEntry(new _OutlineEntry("Sentences",
		// "sentence-view"));

		_Outline logsOutline = new _Outline("Log", "log");
		logsOutline.addEntry(new _OutlineEntry("Server", "common server log", "server_log-view", "p?id=log-view&category=server"));
		logsOutline.addEntry(new _OutlineEntry("Web", "web requests log", "web_log-view", "p?id=log-view&category=web"));
		logsOutline.addEntry(new _OutlineEntry("Messaging", "messaging log", "messaging_log-view", "p?id=log-view&category=message"));
		logsOutline.addEntry(new _OutlineEntry("Scheduler", "scheduled tasks log", "scheduler_log-view", "p?id=log-view&category=scheduler"));
		logsOutline.addEntry(new _OutlineEntry("Localization", "localizator logs", "localization_log-view", "p?id=log-view&category=localization"));
		logsOutline.addEntry(new _OutlineEntry("Report", "report logs", "report_log-view", "p?id=log-view&category=report"));

		_Outline rules_outline = new _Outline(getLocalizedWord("Rules of the applications", lang), "rules");

		for (Application app : new ApplicationDAO(session).findAll()) {
			rules_outline.addEntry(new _OutlineEntry(app.getName(), "", "rule-view-" + app.getName(), "p?id=rule-view&application=" + app.getName()));
		}

		_Outline st_outline = new _Outline(getLocalizedWord("Scheduler", lang), "schedulers");

		st_outline.addEntry(new _OutlineEntry("Tasks", "scheduledtask-view"));
		st_outline.addEntry(new _OutlineEntry("Queue", "queue-view"));

		list.add(common_outline);
		list.add(logsOutline);
		list.add(rules_outline);
		list.add(st_outline);

		String app = formData.getValueSilently("application");
		String category = formData.getValueSilently("category");
		String outline_current = formData.getValueSilently("id").replace("-form", "-view");

		if (!app.isEmpty()) {
			addValue("request_param", "application=" + app);
			outline_current = outline_current+"-"+ app;
		}
		if (!category.isEmpty()) {
			outline_current = category+"_"+ outline_current;
		}
		addValue("outline_current", outline_current);
		addContent(list);
	}
}
