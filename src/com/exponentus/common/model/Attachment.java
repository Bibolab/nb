package com.exponentus.common.model;

import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.exponentus.dataengine.jpa.AppEntity;
import com.exponentus.scripting._Session;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "attachments")
public class Attachment extends AppEntity<UUID> {

	private String fieldName;
	private String realFileName;

	@JsonIgnore
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private byte[] file;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getRealFileName() {
		return realFileName;
	}

	public void setRealFileName(String realFileName) {
		this.realFileName = realFileName;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	@Override
	public String getShortXMLChunk(_Session ses) {
		StringBuilder chunk = new StringBuilder(400);
		chunk.append("<fieldname>" + fieldName + "</fieldname>");
		chunk.append("<filename>" + realFileName + "</filename>");
		return chunk.toString();
	}

	@Override
	public String getFullXMLChunk(_Session ses) {
		return getShortXMLChunk(ses);
	}

	@Override
	public String toString() {
		return fieldName + "/" + realFileName;
	}
}
