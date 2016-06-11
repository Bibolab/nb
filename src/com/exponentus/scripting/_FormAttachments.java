package com.exponentus.scripting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exponentus.common.model.Attachment;

public class _FormAttachments {
	private Map<String, Attachment> attachments = new HashMap<String, Attachment>();

	public void addFile(String fileName, String fieldName) {
		Attachment a = new Attachment();
		a.setRealFileName(fileName);
		a.setFieldName(fieldName);
		attachments.put(fieldName + "_" + fileName, a);
	}

	public void addFileWithSign(String fileName, String fieldName, String sign) {
		Attachment a = new Attachment();
		a.setRealFileName(fileName);
		a.setFieldName(fieldName);
		a.setSign(sign);
		attachments.put(fieldName + "_" + fileName, a);
	}

	public List<Attachment> getFiles() {
		return new ArrayList<Attachment>(attachments.values());
	}

	public Attachment getFile(String fieldName, String fileName) {
		return attachments.get(fieldName + "_" + fileName);
	}

	public Attachment getFile(String fileName) {
		return attachments.get(fileName);
	}
}
