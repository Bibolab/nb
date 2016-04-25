package com.exponentus.common.page.form;

import com.exponentus.common.dao.DAOFactory;
import com.exponentus.dataengine.jpa.DAO;
import com.exponentus.dataengine.jpa.IAppEntity;
import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;
import com.exponentus.user.IUser;

public class DefaultForm extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		IUser<Long> user = session.getUser();
		IAppEntity entity;
		String form = formData.getValueSilently("id");
		String id = formData.getValueSilently("docid");
		if (!form.isEmpty() && !id.isEmpty()) {
			DAO dao = DAOFactory.get(form);
			if (dao != null) {
				entity = dao.findById(id);
				addContent((IPOJOObject) entity);
			}
		}
	}

}
