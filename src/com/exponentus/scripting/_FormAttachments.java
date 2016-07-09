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

import com.exponentus.dataengine.jpa.TempFile;
import com.exponentus.env.Environment;
import com.exponentus.server.Server;

public class _FormAttachments {
	private Map<String, TempFile> addedAttachments = new HashMap<String, TempFile>();
	private Map<String, TempFile> deletedAttachments = new HashMap<String, TempFile>();
	private _Session ses;

	_FormAttachments(_Session ses) {
		this.ses = ses;
	}

	public void addFile(String fileName, String fieldName) {
		TempFile a = new TempFile();
		a.setRealFileName(fileName);
		a.setFieldName(fieldName);
		addedAttachments.put(fieldName + "_" + fileName, a);
	}

	public void addFileWithSign(String fileName, String fieldName, String sign) {
		TempFile a = new TempFile();
		a.setRealFileName(fileName);
		a.setFieldName(fieldName);
		a.setSign(sign);
		addedAttachments.put(fieldName + "_" + fileName, a);
	}

	public List<TempFile> getFiles(String fieldName) {
		ArrayList<TempFile> atts = new ArrayList<TempFile>();
		for (TempFile att : addedAttachments.values()) {
			atts.add(getFile(fieldName, att.getRealFileName()));
		}
		return atts;
	}

	public List<TempFile> getFiles() {
		ArrayList<TempFile> atts = new ArrayList<TempFile>();
		for (TempFile att : addedAttachments.values()) {
			atts.add(getFile(att.getRealFileName()));
		}
		return atts;
	}

	public TempFile getFile(String fieldName, String fileName) {
		TempFile att = addedAttachments.get(fieldName + "_" + fileName);
		if (att != null) {
			att.setRealFileName(fileName);
			File file = new File(Environment.tmpDir + File.separator + ses.getUser().getUserID() + File.separator + fileName);
			InputStream is = null;

			try {
				is = new FileInputStream(file);
				att.setFile(IOUtils.toByteArray(is));
			} catch (IOException e) {
				Server.logger.errorLogEntry(e);
			}
		}
		return att;
	}

	public TempFile getFile(String fn) {
		return getFile("", fn);
	}

	public void removeFile(String fieldName, String fileName) {
		TempFile a = new TempFile();
		a.setRealFileName(fileName);
		a.setFieldName(fieldName);
		deletedAttachments.put(fieldName + "_" + fileName, a);

	}

	public void removeFile(String fileName) {
		TempFile a = new TempFile();
		a.setRealFileName(fileName);
		a.setFieldName("");
		deletedAttachments.put("_" + fileName, a);

	}

	public List<TempFile> getDeletedFiles() {
		return new ArrayList<TempFile>(deletedAttachments.values());
	}
}
