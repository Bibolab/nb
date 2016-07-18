package com.exponentus.scriptprocessor.page;

import java.util.List;

import com.exponentus.common.model.Attachment;
import com.exponentus.dataengine.jpa.IAppFile;
import com.exponentus.dataengine.jpa.TempFile;
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
		for (TempFile file : ses.getFormAttachments(formData.getValueSilently(EnvConst.FSID_FIELD_NAME)).getFiles()) {
			atts.add((Attachment) file.convertTo(new Attachment()));
		}
		_POJOObjectWrapper wrapped = new _POJOObjectWrapper(document, getSes());
		result.addObject(wrapped);
	}

	@SuppressWarnings("unused")
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
				// _Validation ve = validate(fsId);
				if (false) {
					setBadRequest();
					// setValidation(ve);
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
						getSes().removeAttribute(fsId + "_referrer");
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
			logError(e);
		}
		return result;

	}

	protected List<Attachment> getActualAttachments(List<Attachment> atts) {
		String fsId = formData.getValueSilently(EnvConst.FSID_FIELD_NAME);
		_FormAttachments formFiles = getSes().getFormAttachments(fsId);

		for (TempFile tmpFile : formFiles.getFiles()) {
			Attachment a = (Attachment) tmpFile.convertTo(new Attachment());
			a.setFieldName(a.getDefaultFormName());
			atts.add(a);
		}

		List<TempFile> toDelete = formFiles.getDeletedFiles();
		if (toDelete.size() > 0) {
			for (IAppFile fn : toDelete) {
				atts.remove(fn);
			}
		}

		return atts;
	}

	protected List<IAppFile> getActualAttachments(String fieldName, List<IAppFile> atts) {
		String fsId = formData.getValueSilently(EnvConst.FSID_FIELD_NAME);
		_FormAttachments formFiles = getSes().getFormAttachments(fsId);
		String[] fileNames = formData.getListOfValuesSilently(fieldName);
		if (fileNames.length > 0) {
			for (String fn : fileNames) {
				IAppFile ef = formFiles.getFile(fieldName, fn);
				atts.add(ef);
			}
		}

		List<TempFile> toDelete = formFiles.getDeletedFiles();
		if (toDelete.size() > 0) {
			for (IAppFile fn : toDelete) {
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
