package com.exponentus.dataengine.jpa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.exponentus.server.Server;

public class TempFile implements IAppFile {

	private String fieldName;
	private String realFileName;
	private String path;
	private String sign = "";

	@Override
	public String getFieldName() {
		return fieldName;
	}

	@Override
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String getSign() {
		return sign;
	}

	@Override
	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public byte[] getFile() {
		try {
			return Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			Server.logger.errorLogEntry(e);
			return null;
		}
	}

	@Override
	public void setFile(byte[] byteArray) {

	}

	public IAppFile convertTo(IAppFile file) {
		file.setRealFileName(realFileName);
		file.setFieldName(fieldName);
		file.setSign(sign);
		file.setFile(getFile());
		return file;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(IAppFile.class.isAssignableFrom(getClass()) && IAppFile.class.isAssignableFrom(obj.getClass()))) {
			return false;
		} else {
			IAppFile tmp = (IAppFile) obj;

			if (tmp.getFieldName() != null && this.fieldName == null) {
				return false;
			}

			if (tmp.getRealFileName() != null && this.realFileName == null) {
				return false;
			}

			if ((tmp.getFieldName() == null && this.fieldName == null || tmp.getFieldName().equals(this.fieldName))
			        && (tmp.getRealFileName() == null && this.realFileName == null || tmp.getRealFileName().equals(this.realFileName))) {
				return true;
			} else {
				return false;
			}
		}
	}

}
