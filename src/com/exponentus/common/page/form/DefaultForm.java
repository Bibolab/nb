package com.exponentus.common.page.form;

import com.exponentus.common.dao.DAOFactory;
import com.exponentus.dataengine.jpa.DAO;
import com.exponentus.dataengine.jpa.IAppEntity;
import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.event._DoPage;

public class DefaultForm extends _DoPage {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		IAppEntity entity;
		String form = formData.getValueSilently("id");
		String id = formData.getValueSilently("docid");
		if (!form.isEmpty() && !id.isEmpty()) {
			@SuppressWarnings("unchecked")
			DAO<IAppEntity, ?> dao = DAOFactory.get(form);
			if (dao != null) {
				entity = dao.findById(id);
				addContent((IPOJOObject) entity);
			}
		}
	}

}
