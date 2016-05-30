package com.exponentus.exception;

import java.io.File;

public class TransformatorException extends Exception {
	private String errorText;

	private static final long serialVersionUID = -6819745722255830336L;
	private Exception realException;
	private String xsltFilePath;

	public TransformatorException(TransformatorExceptionType error, File xsltFile) {
		super();

		this.xsltFilePath = xsltFile.getAbsolutePath();
		switch (error) {
		case COMPILATION_ERROR_OR_FILE_DOES_NOT_EXIST:
			errorText = "XSLT file error or xslt file does not exist (path=" + xsltFilePath + ")";
			break;
		default:
			break;

		}
	}

	public TransformatorException(TransformatorExceptionType error, String xmlContent) {
		super();

		switch (error) {
		case XML_CONTENT_ERROR:
			errorText = "XML Content error (xml=" + xmlContent + ")";
			break;
		default:
			break;

		}
	}

	public Exception getRealException() {
		return realException;
	}

	@Override
	public String getMessage() {
		return errorText;
	}

	@Override
	public String toString() {
		return errorText;
	}
}
