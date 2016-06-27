package com.exponentus.scriptprocessor.page;

import java.util.List;

import com.exponentus.common.model.Attachment;
import com.exponentus.env.EnvConst;
import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting._FormAttachments;
import com.exponentus.scripting._POJOObjectWrapper;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._Validation;
import com.exponentus.scripting._WebFormData;
import com.exponentus.util.Util;

public abstract class AbstractForm extends AbstractPage {

	@Override
	protected void addContent(IPOJOObject document) {
		_Session ses = getSes();
		List<Attachment> atts = document.getAttachments();
		for (Attachment file : ses.getFormAttachments(formData.getValueSilently(EnvConst.FSID_FIELD_NAME)).getFiles()) {
			atts.add(file);
		}
		_POJOObjectWrapper wrapped = new _POJOObjectWrapper(document, getSes());
		result.addObject(wrapped);
	}

	@Override
	public PageOutcome processCode(String method) {
		String fsId = formData.getAnyValueSilently(EnvConst.FSID_FIELD_NAME);
		try {
			if (method.equalsIgnoreCase("GET")) {
				doGET(getSes(), formData);
				if (fsId.isEmpty()) {
					fsId = Util.generateRandomAsText();
				}
				addValue(EnvConst.FSID_FIELD_NAME, fsId);
				getSes().setAttribute(fsId + "_referrer", formData.getReferrer());
			} else {
				_Validation ve = validate(fsId);
				if (ve.hasError()) {
					setBadRequest();
					setValidation(ve);
				} else {
					if (method.equalsIgnoreCase("POST")) {
						doPOST(getSes(), formData);
						result.setRedirectURL((String) getSes().getAttribute(fsId + "_referrer"));
						if (result.getInfoMessageType() != InfoMessageType.VALIDATION_ERROR
						        && result.getInfoMessageType() != InfoMessageType.SERVER_ERROR) {
							// result.setFlash(entity.getId().toString());
							result.setInfoMessageType(InfoMessageType.DOCUMENT_SAVED);
							getSes().removeAttribute(fsId);
						}

					} else if (method.equalsIgnoreCase("PUT")) {
						doPUT(getSes(), formData);
						if (!fsId.isEmpty()) {
							result.setRedirectURL((String) getSes().getAttribute(fsId + "_referrer"));
							getSes().removeAttribute(fsId);
						}
					} else if (method.equalsIgnoreCase("DELETE")) {
						doDELETE(getSes(), formData);
					}
				}
			}
		} catch (Exception e) {
			result.setException(e);
			result.setInfoMessageType(InfoMessageType.SERVER_ERROR);
			result.setVeryBadRequest();
			error(e);
		}
		return result;

	}

	protected List<Attachment> getActualAttachments(List<Attachment> atts) {
		String fsId = formData.getValueSilently(EnvConst.FSID_FIELD_NAME);
		_FormAttachments formFiles = getSes().getFormAttachments(fsId);

		for (Attachment newFile : formFiles.getFiles()) {
			atts.add(newFile);
		}

		List<Attachment> toDelete = formFiles.getDeletedFiles();
		if (toDelete.size() > 0) {
			for (Attachment fn : toDelete) {
				atts.remove(fn);
			}
		}

		return atts;
	}

	protected List<Attachment> getActualAttachments(String fieldName, List<Attachment> atts) {
		String fsId = formData.getValueSilently(EnvConst.FSID_FIELD_NAME);
		_FormAttachments formFiles = getSes().getFormAttachments(fsId);
		String[] fileNames = formData.getListOfValuesSilently(fieldName);
		if (fileNames.length > 0) {
			for (String fn : fileNames) {
				Attachment ef = formFiles.getFile(fieldName, fn);
				atts.add(ef);
			}
		}

		List<Attachment> toDelete = formFiles.getDeletedFiles();
		if (toDelete.size() > 0) {
			for (Attachment fn : toDelete) {
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

	private _Validation validate(String fsId) {
		_Validation ve = new _Validation();
		if (fsId.isEmpty()) {
			ve.addError("fsid", "required", "There is no \"" + EnvConst.FSID_FIELD_NAME + "\" field");
			setBadRequest();
			setValidation(ve);
		}
		return ve;
	}
}
