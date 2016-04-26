package administrator.page.form;

import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.actions._Action;
import com.exponentus.scripting.actions._ActionBar;
import com.exponentus.scripting.actions._ActionType;
import com.exponentus.scripting.event._DoPage;

public class CodeForm extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		String clazz = formData.getValueSilently("class");

		_ActionBar actionBar = new _ActionBar(session);
		actionBar.addAction(new _Action("Compile &amp; Close", "Recompile the class", _ActionType.SAVE_AND_CLOSE));
		actionBar.addAction(new _Action("Compile", "Recompile the class", _ActionType.CUSTOM_ACTION));
		actionBar.addAction(new _Action("Close", "Close", _ActionType.CLOSE));
		addContent(actionBar);

	}

	@Override
	public void doPOST(_Session session, _WebFormData formData) {

	}

}
