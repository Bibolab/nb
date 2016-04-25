package administrator.page.view;

import java.io.IOException;
import java.util.Map;

import com.exponentus.dataengine.RuntimeObjUtil;
import com.exponentus.dataengine.jpa.ViewPage;
import com.exponentus.env.Environment;
import com.exponentus.rule.RuleFiles;
import com.exponentus.scheduler.SchedulerHelper;
import com.exponentus.scripting._POJOListWrapper;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;
import com.exponentus.scriptprocessor.scheduled.IScheduledScript;

public class ScheduledTaskView extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		int pageNum = formData.getNumberValueSilently("page", 1);
		int pageSize = session.pageSize;

		String appName = formData.getValueSilently("application");
		SchedulerHelper helper = new SchedulerHelper();
		try {
			Map<String, IScheduledScript> tasks = helper.getAllScheduledTasks(false);

			RuleFiles rules = new RuleFiles(Environment.getApplication(appName));
			long count = rules.getCount();
			int maxPage = RuntimeObjUtil.countMaxPage(count, pageSize);
			if (pageNum == 0) {
				pageNum = maxPage;
			}
			ViewPage vp = new ViewPage(rules.getLogFiles(), count, maxPage, pageNum);
			addContent(new _POJOListWrapper(vp.getResult(), vp.getMaxPage(), vp.getCount(), vp.getPageNum(), session));
		} catch (IOException e) {
			error(e);
			setBadRequest();
		}
	}
}
