package com.exponentus.scriptprocessor.page;

import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;

public interface IPageScript {
	void setSession(_Session ses);

	void setOutcome(PageOutcome outcome);

	void setFormData(_WebFormData formData);

	PageOutcome processCode(String method);

}
