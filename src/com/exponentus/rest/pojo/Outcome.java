package com.exponentus.rest.pojo;

import java.util.ArrayList;
import java.util.HashMap;

import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.localization.LanguageCode;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("outcome")
@JsonPropertyOrder({ "type", "warningId", "errorId", "messages" })
public class Outcome {
	private OutcomeType type = OutcomeType.OK;
	private String errorId;
	private String warningId;
	private HashMap<LanguageCode, String> message = new HashMap<LanguageCode, String>();

	public OutcomeType getType() {
		return type;
	}

	public Outcome setType(OutcomeType type) {
		this.type = type;
		return this;
	}

	public Outcome addMessage(String message, LanguageCode lang) {
		this.message.put(lang, Environment.vocabulary.getWord(message, lang));
		return this;
	}

	public String getErrorId() {
		return errorId;
	}

	public Outcome setMessage(String s, LanguageCode lang) {
		message.clear();
		addMessage(s, lang);
		return this;
	}

	public Outcome setMessage(Exception exception, ServerServiceExceptionType e, LanguageCode lang) {
		type = OutcomeType.ERROR;
		if (Environment.isDevMode()) {
			errorId = e.name();
			message.put(LanguageCode.ENG, exception.toString());
		} else {
			errorId = e.name();
			message.put(lang, Environment.vocabulary.getWord(e.name(), lang));
		}
		return this;
	}

	public Outcome setMessage(ServerServiceWarningType w, LanguageCode lang) {
		type = OutcomeType.WARNING;
		warningId = w.name();
		message.put(lang, Environment.vocabulary.getWord(w.name(), lang));
		return this;
	}

	public String getWarningId() {
		return warningId;
	}

	public Outcome addMessage(String msg) {
		this.message.put(LanguageCode.valueOf(EnvConst.DEFAULT_LANG), msg);
		return this;

	}

	public Outcome setMessages(ArrayList<String> e, LanguageCode lang) {
		for (String msg : e) {
			addMessage(msg, lang);
		}
		return this;
	}

	public HashMap<LanguageCode, String> getMessages() {
		return message;
	}

	public Outcome setMessages(HashMap<LanguageCode, String> associatedList) {
		this.message = associatedList;
		return this;
	}

}
