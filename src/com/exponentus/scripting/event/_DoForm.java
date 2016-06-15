package com.exponentus.scripting.event;

import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scriptprocessor.page.AbstractForm;

public abstract class _DoForm extends AbstractForm {

	@Override
	public void doGET(_Session session, _WebFormData formData) throws Exception {
	}

	@Override
	public void doPUT(_Session session, _WebFormData formData) throws Exception {
	}

	@Override
	public void doPOST(_Session session, _WebFormData formData) throws Exception {
	}

	@Override
	public void doDELETE(_Session session, _WebFormData formData) throws Exception {
	}
}
