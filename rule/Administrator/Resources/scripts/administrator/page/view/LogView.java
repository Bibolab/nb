package administrator.page.view;

import com.exponentus.dataengine.RuntimeObjUtil;
import com.exponentus.dataengine.jpa.ViewPage;
import com.exponentus.log.LogFiles;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.actions._Action;
import com.exponentus.scripting.actions._ActionBar;
import com.exponentus.scripting.actions._ActionType;
import com.exponentus.scripting.event._DoPage;

public class LogView extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
        _ActionBar actionBar = new _ActionBar(session);
        actionBar.addAction(new _Action("Delete", "Delete log", _ActionType.DELETE_DOCUMENT));
		String dir = formData.getValueSilently("category");
		int pageNum = formData.getNumberValueSilently("page", 1);
		int pageSize = session.pageSize;

		LogFiles logs = new LogFiles(dir);
		long count = logs.getCount();
		int maxPage = RuntimeObjUtil.countMaxPage(count, pageSize);
		if (pageNum == 0) {
			pageNum = maxPage;
		}
		if (!dir.isEmpty()) {
			addValue("request_param", "category=" + dir);
		}
		ViewPage vp = new ViewPage(logs.getLogFiles(), count, maxPage, pageNum);
        addContent(actionBar);
		addContent(vp.getResult(), vp.getMaxPage(), vp.getCount(), vp.getPageNum());
	}
}
