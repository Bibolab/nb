package administrator.page.navigator;

import java.util.ArrayList;
import java.util.List;

import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;
import com.exponentus.scriptprocessor.page.IOutcomeObject;

import kz.nextbase.script.outline._Outline;
import kz.nextbase.script.outline._OutlineEntry;

public class MainNavigator extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		LanguageCode lang = session.getLang();
		List<IOutcomeObject> list = new ArrayList<IOutcomeObject>();

		_Outline common_outline = new _Outline(getLocalizedWord("Administrator", lang), "common");
		common_outline.addEntry(new _OutlineEntry("Server", "", "server", "p?id=server-form"));
		common_outline.addEntry(new _OutlineEntry("User", "user-view"));
		common_outline.addEntry(new _OutlineEntry("Applications", "application-view"));
		common_outline.addEntry(new _OutlineEntry("Languages", "language-view"));
		// common_outline.addEntry(new _OutlineEntry("Sentences",
		// "sentence-view"));
		common_outline.addEntry(new _OutlineEntry("Log", "log-view"));

		list.add(common_outline);

		addContent("outline_current", formData.getValueSilently("id").replace("-form", "-view"));
		addContent(list);
	}
}
