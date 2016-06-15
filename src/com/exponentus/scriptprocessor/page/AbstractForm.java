package com.exponentus.scriptprocessor.page;

import java.util.List;

import com.exponentus.common.model.Attachment;
import com.exponentus.common.model.EntityFile;
import com.exponentus.env.EnvConst;
import com.exponentus.scripting._FormAttachments;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.util.Util;

public abstract class AbstractForm extends AbstractPage {

	@Override
	public PageOutcome processCode(String method) {
		try {
			if (method.equalsIgnoreCase("POST")) {
				doPOST(getSes(), formData);
			} else if (method.equalsIgnoreCase("PUT")) {
				doPUT(getSes(), formData);
			} else if (method.equalsIgnoreCase("DELETE")) {
				doDELETE(getSes(), formData);
			} else {
				doGET(getSes(), formData);
				String fsId = formData.getValueSilently(EnvConst.FSID_FIELD_NAME);
				if (fsId.isEmpty()) {
					fsId = Util.generateRandomAsText();
				}
				addValue("formsesid", fsId);
				getSes().setAttribute(fsId + "_referrer", formData.getReferrer());
			}
		} catch (Exception e) {
			result.setException(e);
			result.setInfoMessageType(InfoMessageType.SERVER_ERROR);
			result.setVeryBadRequest();
			error(e);
		}
		return result;
	}

	protected List<Attachment> getActualAttachments(String fieldName, List<Attachment> atts) {
		String fsId = formData.getValueSilently(EnvConst.FSID_FIELD_NAME);
		_FormAttachments formFiles = getSes().getFormAttachments(fsId);
		String[] fileNames = formData.getListOfValuesSilently(fieldName);
		if (fileNames.length > 0) {
			for (String fn : fileNames) {
				EntityFile ef = formFiles.getFile(fn);
				atts.add((Attachment) ef);
			}
		}

		List<EntityFile> toDelete = formFiles.getDeletedFiles();
		if (toDelete.size() > 0) {
			for (EntityFile fn : toDelete) {
				atts.remove(fn);
			}
		}
		return atts;
	}

	@Override
	public abstract void doGET(_Session session, _WebFormData formData) throws Exception;

	@Override
	public abstract void doPUT(_Session session, _WebFormData formData) throws Exception;

	@Override
	public abstract void doPOST(_Session session, _WebFormData formData) throws Exception;

	@Override
	public abstract void doDELETE(_Session session, _WebFormData formData) throws Exception;
}
