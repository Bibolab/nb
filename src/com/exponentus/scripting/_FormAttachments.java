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
import com.exponentus.common.model.EntityFile;
import com.exponentus.env.Environment;
import com.exponentus.server.Server;

public class _FormAttachments {
	private Map<String, EntityFile> addedAttachments = new HashMap<String, EntityFile>();
	private Map<String, EntityFile> deletedAttachments = new HashMap<String, EntityFile>();
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

	public List<EntityFile> getFiles() {
		return new ArrayList<EntityFile>(addedAttachments.values());
	}

	public EntityFile getFile(String fieldName, String fileName) {
		return addedAttachments.get(fieldName + "_" + fileName);
	}

	public EntityFile getFile(String fn) {
		EntityFile att = addedAttachments.get("_" + fn);
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

	public List<EntityFile> getDeletedFiles() {
		return new ArrayList<EntityFile>(deletedAttachments.values());
	}
}
