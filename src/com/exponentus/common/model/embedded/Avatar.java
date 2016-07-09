package com.exponentus.common.model.embedded;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.Lob;

import com.exponentus.dataengine.jpa.IAppFile;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Embeddable
public class Avatar implements IAppFile {
	@JsonIgnore
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private byte[] file;

	@Override
	public void setFieldName(String fieldName) {

	}

	@Override
	public String getFieldName() {
		return "avatar";
	}

	@Override
	public String getRealFileName() {
		return "avatar";
	}

	@Override
	public void setFile(byte[] file) {
		this.file = file;
	}

	@Override
	public byte[] getFile() {
		return file;
	}

	@Override
	public void setRealFileName(String fileName) {

	}

	@Override
	public void setSign(String sign) {

	}

	@Override
	public String getSign() {
		return null;
	}

}
