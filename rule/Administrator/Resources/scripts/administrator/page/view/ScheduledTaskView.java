package administrator.page.view;

import java.io.IOException;
import java.util.List;

import org.quartz.SchedulerException;

import com.exponentus.dataengine.RuntimeObjUtil;
import com.exponentus.dataengine.jpa.ViewPage;
import com.exponentus.scheduler.SchedulerHelper;
import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting._POJOListWrapper;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;

public class ScheduledTaskView extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		int pageNum = formData.getNumberValueSilently("page", 1);
		int pageSize = session.pageSize;

		SchedulerHelper helper = new SchedulerHelper();
		try {
			List<IPOJOObject> tasks = helper.getQueue(false);

			long count = tasks.size();
			int maxPage = RuntimeObjUtil.countMaxPage(count, pageSize);
			if (pageNum == 0) {
				pageNum = maxPage;
			}
			ViewPage vp = new ViewPage(tasks, count, maxPage, pageNum);
			addContent(new _POJOListWrapper(vp.getResult(), vp.getMaxPage(), vp.getCount(), vp.getPageNum(), session));
		} catch (IOException | SchedulerException e) {
			logError(e);
			setBadRequest();
		}
	}
}
