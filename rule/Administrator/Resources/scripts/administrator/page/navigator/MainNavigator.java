package administrator.page.navigator;

import administrator.dao.ApplicationDAO;
import administrator.model.Application;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;
import com.exponentus.scriptprocessor.page.IOutcomeObject;
import kz.nextbase.script.outline._Outline;
import kz.nextbase.script.outline._OutlineEntry;

import java.util.ArrayList;
import java.util.List;


public class MainNavigator extends _DoPage {

    @Override
    public void doGET(_Session session, _WebFormData formData) {
        LanguageCode lang = session.getLang();
        List<IOutcomeObject> list = new ArrayList<IOutcomeObject>();

        _Outline common_outline = new _Outline(getLocalizedWord("Server settings", lang), "server");
        common_outline.addEntry(new _OutlineEntry("Server parameters", "General server parameters", "server", "p?id=server-form"));
        common_outline.addEntry(new _OutlineEntry("User", "user-view"));
        common_outline.addEntry(new _OutlineEntry("Applications", "application-view"));
        common_outline.addEntry(new _OutlineEntry("Languages", "language-view"));
        // common_outline.addEntry(new _OutlineEntry("Sentences",
        // "sentence-view"));
        common_outline.addEntry(new _OutlineEntry("Log", "log-view"));

        _Outline rules_outline = new _Outline(getLocalizedWord("Rules of the applications", lang), "rules");

        for (Application app : new ApplicationDAO(session).findAll()) {
            rules_outline.addEntry(new _OutlineEntry(app.getName(), "", "rule-view" + app.getId(),
                    "p?id=rule-view&application=" + app.getName()));
        }

        _Outline st_outline = new _Outline(getLocalizedWord("Scheduler", lang), "schedulers");

        st_outline.addEntry(new _OutlineEntry("Tasks", "scheduledtask-view"));
        st_outline.addEntry(new _OutlineEntry("Queue", "queue-view"));

        list.add(common_outline);
        list.add(rules_outline);
        list.add(st_outline);

        String app = formData.getValueSilently("application");
        if (!app.isEmpty()) {
            addValue("request_param", "application=" + app);
        }
        addValue("outline_current", formData.getValueSilently("id").replace("-form", "-view"));
        addContent(list);
    }
}
