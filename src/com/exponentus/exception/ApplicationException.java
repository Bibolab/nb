package com.exponentus.exception;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.http.HttpStatus;

import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scriptprocessor.page.IOutcomeObject;
import com.exponentus.scriptprocessor.page.PageOutcome;
import com.exponentus.server.Server;
import com.exponentus.webserver.servlet.xslt.SaxonTransformator;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.sf.saxon.s9api.SaxonApiException;

public class ApplicationException extends Exception implements IOutcomeObject {
	protected static String type = EnvConst.APP_ID;
	protected LanguageCode lang;
	protected String errorMsg;
	protected int code = HttpStatus.SC_INTERNAL_SERVER_ERROR;

	private static final long serialVersionUID = 8544634275928476077L;
	private String location;

	@JsonIgnore
	private String servletName = "Provider";

	public String appType;

	public ApplicationException(String appType, String error, LanguageCode lang) {
		super(error);
		this.appType = appType;
		this.lang = lang;
	}

	public ApplicationException(String appType, PageOutcome outcome, LanguageCode lang) {
		super(outcome.getException().toString());
		this.appType = appType;
		StringWriter errors = new StringWriter();
		outcome.getException().printStackTrace(new PrintWriter(errors));
		errorMsg = errors.toString();
		this.lang = lang;
		code = outcome.getHttpStatus();
	}

	@JsonIgnore
	public String getHTMLMessage() {
		return getHTMLMessage(code);
	}

	@JsonIgnore
	public String getHTMLMessage(int code) {
		ExceptionXML document = new ExceptionXML(getMessage(), code, location, type, servletName, errorMsg);
		document.setAppType(appType);
		String xslt = Environment.getKernelDir() + "xsl" + File.separator + EnvConst.ERROR_XSLT;
		File errorXslt = new File(xslt);

		try {
			String xml = document.toXML(lang);
			return new SaxonTransformator().toTrans(errorXslt, xml);
		} catch (IOException | SaxonApiException e) {
			Server.logger.errorLogEntry(e);
		}

		return toXML();
	}

	@Override
	public String toXML() {
		ExceptionXML document = new ExceptionXML(getMessage(), code, location, type, servletName, errorMsg);
		document.setAppType(appType);
		return document.toXML(lang);
	}

	@Override
	public Object toJSON() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.addMixIn(ApplicationException.class, MapperMixIn.class);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		String jsonInString = null;
		try {
			jsonInString = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			Server.logger.errorLogEntry(e);
		}
		return jsonInString;
	}
}
