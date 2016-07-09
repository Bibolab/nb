package com.exponentus.dataengine.jpa;

public interface IAppFile {

	public String getFieldName();

	public String getRealFileName();

	public byte[] getFile();

	public void setRealFileName(String fileName);

	public void setFile(byte[] byteArray);

	public String getSign();

	public void setFieldName(String fieldName);

	public void setSign(String sign);

}
