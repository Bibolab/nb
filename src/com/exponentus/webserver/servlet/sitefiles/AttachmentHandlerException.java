package com.exponentus.webserver.servlet.sitefiles;

public class AttachmentHandlerException extends Exception {
	private static final long serialVersionUID = -7955384945856281213L;
	private Exception realException;
	private String errorText;

	public AttachmentHandlerException(AttachmentHandlerExceptionType type, String error) {
		super(error);
		switch (type) {
		case FILE_NOT_FOUND:
			errorText = "File not found";
			break;
		case FORMSESID_IS_NOT_CORRECT:
			errorText = "there is no formsesid";
			break;
		default:
			break;

		}
		realException = this;
	}

	public AttachmentHandlerException(String error) {
		super(error);
		realException = this;
	}

	public Exception getRealException() {
		return realException;
	}

	@Override
	public String getMessage() {
		return errorText;

	}

}
