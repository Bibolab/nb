package com.exponentus.exception;

import org.apache.http.HttpStatus;

import com.exponentus.localization.LanguageCode;
import com.exponentus.scriptprocessor.page.IOutcomeObject;
import com.exponentus.server.Server;

public class MsgException extends ApplicationException implements IOutcomeObject {
	protected int code = HttpStatus.SC_BAD_REQUEST;

	private static final long serialVersionUID = 7716838564586492848L;

	public MsgException(String error, LanguageCode lang) {
		super(type, error, lang);
		this.lang = lang;
	}

	@Override
	public String toString() {
		return errorMsg;
	}

	@Override
	public String toXML() {
		return "<request><error><message>" + errorMsg + "</message><type>" + type + "</type><exception><![CDATA[" + errorMsg
		        + "]]></exception><server>" + Server.serverVersion + "</server></error></request>";
	}

	@Override
	public Object toJSON() {
		return this;
	}

}
