package com.exponentus.rest.pojo;

import java.util.ArrayList;

import com.exponentus.env.Environment;
import com.exponentus.localization.LanguageCode;
import com.exponentus.rest.pojo.constants.OutcomeType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("outcome")
@JsonPropertyOrder({ "type", "warningId", "errorId", "localizedMessage" })
public class Outcome {
	private OutcomeType type = OutcomeType.OK;
	private String errorId;
	private String warningId;
	private String localizedMessage;
	private Object payload;

	public OutcomeType getType() {
		return type;
	}

	public Outcome setType(OutcomeType type) {
		this.type = type;
		return this;
	}

	public Outcome addMessage(String message, LanguageCode lang) {
		localizedMessage = Environment.vocabulary.getWord(message, lang);
		return this;
	}

	public String getErrorId() {
		return errorId;
	}

	public Outcome setMessage(String s, LanguageCode lang) {
		addMessage(s, lang);
		return this;
	}

	public Outcome setMessage(Exception exception, ServerServiceExceptionType e, LanguageCode lang) {
		type = OutcomeType.ERROR;
		if (Environment.isDevMode()) {
			errorId = e.name();
			localizedMessage = errorId;
		} else {
			errorId = e.name();
			localizedMessage = Environment.vocabulary.getWord(e.name(), lang);
		}
		return this;
	}

	public Outcome setMessage(ServerServiceWarningType w, LanguageCode lang) {
		type = OutcomeType.WARNING;
		warningId = w.name();
		localizedMessage = Environment.vocabulary.getWord(w.name(), lang);
		return this;
	}

	public String getWarningId() {
		return warningId;
	}

	public Outcome addMessage(String msg) {
		localizedMessage = msg;
		return this;

	}

	public Outcome setMessages(ArrayList<String> e, LanguageCode lang) {
		for (String msg : e) {
			addMessage(msg, lang);
		}
		return this;
	}

	public String getLocalizedMessage() {
		return localizedMessage;
	}

	public void setLocalizedMessage(String localizedMessage) {
		this.localizedMessage = localizedMessage;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payLoad) {
		this.payload = payLoad;
	}

}
