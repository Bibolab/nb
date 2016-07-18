package com.exponentus.scripting;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exponentus.dataengine.jpa.TempFile;

public class _FormAttachments {
	private Map<String, Map<String, TempFile>> addedAttachments = new HashMap<String, Map<String, TempFile>>();
	private Map<String, TempFile> deletedAttachments = new HashMap<String, TempFile>();

	_FormAttachments(_Session ses) {

	}

	public TempFile addFile(File file, String fileName, String fieldName) {
		TempFile tmpFile = new TempFile();
		tmpFile.setPath(file.getAbsolutePath());
		tmpFile.setRealFileName(fileName);
		tmpFile.setFieldName(fieldName);
		Map<String, TempFile> attachField = addedAttachments.get(fieldName);
		// TODO it need to improve
		if (attachField == null || fieldName.equalsIgnoreCase("avatar")) {
			attachField = new HashMap<String, TempFile>();
			addedAttachments.put(fieldName, attachField);
		}
		attachField.put(fileName, tmpFile);
		return tmpFile;
	}

	public void addFileWithSign(File file, String fileName, String fieldName, String sign) {
		TempFile a = addFile(file, fileName, fieldName);
		a.setSign(sign);
	}

	public List<TempFile> getFiles(String fieldName) {
		ArrayList<TempFile> atts = new ArrayList<TempFile>();
		Map<String, TempFile> attachField = addedAttachments.get(fieldName);
		if (attachField != null) {
			for (TempFile att : attachField.values()) {
				atts.add(getFile(fieldName, att.getRealFileName()));
			}
		}
		return atts;
	}

	public List<TempFile> getFiles() {
		ArrayList<TempFile> atts = new ArrayList<TempFile>();
		for (Map<String, TempFile> attsMap : addedAttachments.values()) {
			for (TempFile att : attsMap.values()) {
				atts.add(getFile(att.getRealFileName()));
			}
		}
		return atts;
	}

	public TempFile getFile(String fieldName, String fileName) {
		TempFile att = null;
		Map<String, TempFile> attsMap = addedAttachments.get(fieldName);
		if (attsMap != null) {
			att = attsMap.get(fileName);
		}
		return att;
	}

	public TempFile getFile(String fn) {
		return getFile("", fn);
	}

	public Map<String, TempFile> getFieldFile(String fieldName) {
		return addedAttachments.get(fieldName);
	}

	public void removeFile(String fieldName, String fileName) {
		TempFile a = new TempFile();
		a.setRealFileName(fileName);
		a.setFieldName(fieldName);
		deletedAttachments.put(fieldName + "_" + fileName, a);

	}

	// TODO it need to consider name of field
	public List<TempFile> getDeletedFiles() {
		return new ArrayList<TempFile>(deletedAttachments.values());
	}
}
