package administrator.page.navigator;

import java.util.ArrayList;
import java.util.List;

import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;
import com.exponentus.scriptprocessor.page.IOutcomeObject;

import administrator.dao.ApplicationDAO;
import administrator.model.Application;
import kz.nextbase.script.outline._Outline;
import kz.nextbase.script.outline._OutlineEntry;

public class MainNavigator extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		LanguageCode lang = session.getLang();
		List<IOutcomeObject> list = new ArrayList<IOutcomeObject>();

		_Outline common_outline = new _Outline(getLocalizedWord("Server settings", lang), "common");
		common_outline.addEntry(new _OutlineEntry("Server", "Server settings", "server", "p?id=server-form"));
		common_outline.addEntry(new _OutlineEntry("User", "user-view"));
		common_outline.addEntry(new _OutlineEntry("Applications", "application-view"));
		common_outline.addEntry(new _OutlineEntry("Languages", "language-view"));
		// common_outline.addEntry(new _OutlineEntry("Sentences",
		// "sentence-view"));
		common_outline.addEntry(new _OutlineEntry("Log", "log-view"));

		_Outline rules_outline = new _Outline(getLocalizedWord("Rules of the applications", lang), "common");

		for (Application app : new ApplicationDAO(session).findAll()) {
			rules_outline.addEntry(new _OutlineEntry(app.getName(), app.getLocalizedName().toString(), "rule-view" + app.getId(),
			        "p?id=rule-view&application=" + app.getName()));
		}

		_Outline st_outline = new _Outline(getLocalizedWord("Scheduler", lang), "common");

		st_outline.addEntry(new _OutlineEntry("Tasks", "scheduledtask-view"));
		st_outline.addEntry(new _OutlineEntry("Queue", "queue-view"));

		list.add(common_outline);
		list.add(rules_outline);
		list.add(st_outline);

		addContent("outline_current", formData.getValueSilently("id").replace("-form", "-view"));
		addContent(list);
	}
}
