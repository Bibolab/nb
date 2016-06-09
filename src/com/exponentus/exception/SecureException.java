package com.exponentus.exception;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpStatus;

import com.exponentus.env.EnvConst;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scriptprocessor.page.IOutcomeObject;
import com.exponentus.server.Server;
import com.exponentus.webserver.servlet.xslt.SaxonTransformator;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.sf.saxon.s9api.SaxonApiException;

public class SecureException extends Exception implements IOutcomeObject {
	private static final long serialVersionUID = 1L;
	private String location;
	private String type = EnvConst.APP_ID;
	private String servletName = "Provider";
	private String exception;
	private String appType;
	private LanguageCode lang;
	private int code = HttpStatus.SC_FORBIDDEN;

	public SecureException(String appType, String error, LanguageCode lang) {
		super(error);
		this.appType = appType;
		this.lang = lang;
	}

	@JsonIgnore
	public String getHTMLMessage() {
		return getHTMLMessage(HttpStatus.SC_FORBIDDEN);
	}

	@JsonIgnore
	public String getHTMLMessage(int code) {
		ExceptionXML document = new ExceptionXML(getMessage(), code, location, type, servletName, exception);
		document.setAppType(appType);
		String xslt = "webapps" + File.separator + appType + File.separator + EnvConst.ERROR_XSLT;
		File errorXslt = new File(xslt);
		if (!errorXslt.exists()) {
			errorXslt = new File("webapps" + File.separator + EnvConst.WORKSPACE_NAME + File.separator + EnvConst.ERROR_XSLT);
		}

		try {
			new SaxonTransformator().toTrans(errorXslt, document.toXML(lang));
		} catch (IOException | SaxonApiException e) {
			Server.logger.errorLogEntry(e);
		}

		return toXML();
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toXML() {
		String xmlText = null;

		ExceptionXML document = new ExceptionXML(getMessage(), code, location, type, servletName, exception);
		document.setAppType(appType);
		String xslt = "webapps" + File.separator + appType + File.separator + EnvConst.ERROR_XSLT;
		File errorXslt = new File(xslt);
		if (!errorXslt.exists()) {
			errorXslt = new File("webapps" + File.separator + EnvConst.WORKSPACE_NAME + File.separator + EnvConst.ERROR_XSLT);
		}

		try {
			xmlText = new SaxonTransformator().toTrans(errorXslt, document.toXML(lang));
		} catch (IOException | SaxonApiException e) {
			Server.logger.errorLogEntry(e);
		}

		return xmlText;
	}

	@Override
	public Object toJSON() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.addMixIn(SecureException.class, MapperMixIn.class);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		String jsonInString = null;
		try {
			jsonInString = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return jsonInString;
	}
}
