package com.exponentus.common.model;

import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.exponentus.dataengine.jpa.AppEntity;
import com.exponentus.dataengine.jpa.IAppFile;
import com.exponentus.scripting._Session;
import com.exponentus.user.AnonymousUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "attachments")
public class Attachment extends AppEntity<UUID>implements IAppFile {

	private String fieldName;
	private String realFileName;
	protected String form = "attachment";
	protected Long author = AnonymousUser.ID;
	@Transient
	private String sign = "";

	@JsonIgnore
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private byte[] file;

	@Override
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String getRealFileName() {
		return realFileName;
	}

	@Override
	public void setRealFileName(String realFileName) {
		this.realFileName = realFileName;
	}

	@Override
	public byte[] getFile() {
		return file;
	}

	@Override
	public void setFile(byte[] file) {
		this.file = file;
	}

	@Override
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public String getDefaultFormName() {
		return "attachment";
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
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(getClass() == obj.getClass())) {
			return false;
		} else {
			Attachment tmp = (Attachment) obj;

			if (tmp.id == null) {
				return false;
			}

			if (tmp.fieldName != null && this.fieldName == null) {
				return false;
			}

			if (tmp.realFileName != null && this.realFileName == null) {
				return false;
			}

			if ((tmp.fieldName == null && this.fieldName == null || tmp.fieldName.equals(this.fieldName))
			        && (tmp.realFileName == null && this.realFileName == null || tmp.realFileName.equals(this.realFileName))) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public String toString() {
		return "fieldName=" + fieldName + ", realFileName=" + realFileName;
	}
}
