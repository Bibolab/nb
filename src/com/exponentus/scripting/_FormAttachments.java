package com.exponentus.scripting;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.exponentus.common.model.Attachment;
import com.exponentus.env.Environment;
import com.exponentus.server.Server;

public class _FormAttachments {
	private Map<String, Attachment> addedAttachments = new HashMap<String, Attachment>();
	private Map<String, Attachment> deletedAttachments = new HashMap<String, Attachment>();
	private _Session ses;

	_FormAttachments(_Session ses) {
		this.ses = ses;
	}

	public void addFile(String fileName, String fieldName) {
		Attachment a = new Attachment();
		a.setRealFileName(fileName);
		a.setFieldName(fieldName);
		addedAttachments.put(fieldName + "_" + fileName, a);
	}

	public void addFileWithSign(String fileName, String fieldName, String sign) {
		Attachment a = new Attachment();
		a.setRealFileName(fileName);
		a.setFieldName(fieldName);
		a.setSign(sign);
		addedAttachments.put(fieldName + "_" + fileName, a);
	}

	public List<Attachment> getFiles() {
		ArrayList<Attachment> atts = new ArrayList<Attachment>();
		for (Attachment att : addedAttachments.values()) {
			atts.add(getFile(att.getRealFileName()));
		}
		return atts;
	}

	public Attachment getFile(String fieldName, String fileName) {
		return addedAttachments.get(fieldName + "_" + fileName);
	}

	public Attachment getFile(String fn) {
		Attachment att = addedAttachments.get("_" + fn);
		att.setRealFileName(fn);
		File file = new File(Environment.tmpDir + File.separator + ses.getUser().getUserID() + File.separator + fn);
		InputStream is = null;

		try {
			is = new FileInputStream(file);
			att.setFile(IOUtils.toByteArray(is));
		} catch (IOException e) {
			Server.logger.errorLogEntry(e);
		}

		return att;
	}

	public void removeFile(String fieldName, String fileName) {
		Attachment a = new Attachment();
		a.setRealFileName(fileName);
		a.setFieldName(fieldName);
		deletedAttachments.put(fieldName + "_" + fileName, a);

	}

	public List<Attachment> getDeletedFiles() {
		return new ArrayList<Attachment>(deletedAttachments.values());
	}
}
