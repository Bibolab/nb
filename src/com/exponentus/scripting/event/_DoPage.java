package com.exponentus.scripting.event;

import com.exponentus.scripting._Exception;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scriptprocessor.page.AbstractPage;

public abstract class _DoPage extends AbstractPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) throws _Exception {
	}

	@Override
	public void doPUT(_Session session, _WebFormData formData) throws _Exception {
	}

	@Override
	public void doPOST(_Session session, _WebFormData formData) throws _Exception {
	}

	@Override
	public void doDELETE(_Session session, _WebFormData formData) throws _Exception {
	}
}
