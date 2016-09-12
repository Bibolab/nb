package com.exponentus.scriptprocessor.page;

import java.io.File;
import java.util.List;
import java.util.UUID;

import com.exponentus.common.model.Attachment;
import com.exponentus.common.service.AttachmentThumbnailService;
import com.exponentus.dataengine.IDatabase;
import com.exponentus.dataengine.jpa.AppEntity;
import com.exponentus.dataengine.jpa.IAppFile;
import com.exponentus.dataengine.jpa.TempFile;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting._FormAttachments;
import com.exponentus.scripting._POJOObjectWrapper;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._Validation;
import com.exponentus.scripting._WebFormData;
import com.exponentus.util.StringUtil;

public abstract class AbstractForm extends AbstractPage {
	private final static String REFERRER_ATTR_NAME = "_referrer";

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

	@Override
	public PageOutcome processCode(String method) {
		String fsId = formData.getAnyValueSilently(EnvConst.FSID_FIELD_NAME);
		_Session ses = getSes();
		try {
			if (method.equalsIgnoreCase("GET")) {
				doGET(ses, formData);
				if (fsId.isEmpty()) {
					fsId = StringUtil.getRandomText();
				}
				addValue(EnvConst.FSID_FIELD_NAME, fsId);
				// System.out.println("open=" + fsId + REFERRER_ATTR_NAME + " "
				// + formData.getReferrer());
				ses.setAttribute(fsId + REFERRER_ATTR_NAME, formData.getReferrer());
			} else {
				_Validation ve = validate(fsId);
				if (ve.hasError()) {
					setBadRequest();
					setValidation(ve);
				} else {
					if (method.equalsIgnoreCase("POST")) {
						doPOST(ses, formData);
						// System.out.println("post=" + fsId +
						// REFERRER_ATTR_NAME + " " + formData.getReferrer());
						String redirectURL = (String) ses.getAttribute(fsId + REFERRER_ATTR_NAME);
						result.setRedirectURL(redirectURL);
						if (result.getInfoMessageType() != InfoMessageType.VALIDATION_ERROR
						        && result.getInfoMessageType() != InfoMessageType.SERVER_ERROR) {
							// result.setFlash(entity.getId().toString());
							result.setInfoMessageType(InfoMessageType.DOCUMENT_SAVED);
						}
						ses.removeAttribute(fsId + REFERRER_ATTR_NAME);
						File userTmpDir = new File(Environment.tmpDir + File.separator + ses.getUser().getUserID());
						File dirToUploadedFile = new File(userTmpDir.getAbsolutePath() + File.separator + fsId);
						if (dirToUploadedFile.exists()) {
							File[] directoryListing = dirToUploadedFile.listFiles();
							if (directoryListing != null) {
								for (File child : directoryListing) {
									Environment.fileToDelete.add(child.getAbsolutePath());
								}
							}
						}

					} else if (method.equalsIgnoreCase("PUT")) {
						doPUT(ses, formData);
						result.setRedirectURL((String) ses.getAttribute(fsId + REFERRER_ATTR_NAME));
						ses.removeAttribute(fsId + REFERRER_ATTR_NAME);
					} else if (method.equalsIgnoreCase("DELETE")) {
						doDELETE(ses, formData);
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

	protected void doGetAttachment(_Session session, _WebFormData formData, AppEntity<UUID> entity) {
		String attachmentId = formData.getValueSilently("attachment");
		Attachment att = entity.getAttachments().stream().filter(it -> it.getIdentifier().equals(attachmentId)).findFirst().get();

		if (formData.containsField("_thumbnail")) {
			File tf = AttachmentThumbnailService.createThumbnailFileIfSupported(session, att);
			if (tf != null) {
				showFile(tf.getAbsolutePath(), att.getRealFileName());
			} else {
				setBadRequest();
				addContent("error", "Unsupported format, try without _thumbnail");
			}
		} else if (showAttachment(attachmentId, entity)) {
			//
		} else {
			setBadRequest();
		}
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

	public class RegNum {

		String key;
		int num;
		IDatabase db = getSes().getAppEnv().getDataBase();

		public int getRegNumber(String key) {
			this.key = key;
			num = db.getRegNum(key);
			return num;
		}

		public void post() {
			db.postRegNum(num, key);

		}

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
