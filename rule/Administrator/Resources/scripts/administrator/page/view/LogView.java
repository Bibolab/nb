package administrator.page.view;

import com.exponentus.dataengine.jpa.ViewPage;
import com.exponentus.log.LogFiles;
import com.exponentus.scripting._POJOListWrapper;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;

import kz.flabs.runtimeobj.RuntimeObjUtil;

public class LogView extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		int pageNum = formData.getNumberValueSilently("page", 1);
		int pageSize = session.pageSize;

		LogFiles logs = new LogFiles();
		long count = logs.getCount();
		int maxPage = RuntimeObjUtil.countMaxPage(count, pageSize);
		if (pageNum == 0) {
			pageNum = maxPage;
		}
		ViewPage vp = new ViewPage(logs.getLogFiles(), count, maxPage, pageNum);
		addContent(new _POJOListWrapper(vp.getResult(), vp.getMaxPage(), vp.getCount(), vp.getPageNum(), session));
	}
}
