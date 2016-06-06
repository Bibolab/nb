package com.exponentus.scripting.actions;

import java.util.ArrayList;
import java.util.UUID;

import com.exponentus.localization.LanguageCode;
import com.exponentus.rule.constans.RunMode;
import com.exponentus.scripting._Exception;
import com.exponentus.scripting._ExceptionType;
import com.exponentus.scripting._Session;
import com.exponentus.scriptprocessor.page.IOutcomeObject;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class _ActionBar implements IOutcomeObject {
	public RunMode isOn = RunMode.ON;
	public String caption = "";
	public String hint = "";

	private ArrayList<_Action> actions = new ArrayList<_Action>();
	@JsonIgnore
	private _Session session;

	public _ActionBar() throws _Exception {
		throw new _Exception(_ExceptionType.CONSTRUCTOR_UNDEFINED, "Default constructor undefined, you should use  \"new _ActionBar(_Session)\"");
	}

	public _ActionBar(_Session ses) {
		session = ses;
	}

	public _ActionBar addAction(_Action action) {
		action.setSession(session);
		actions.add(action);
		return this;
	}

	public UUID getId() {
		return null;
	}

	public String getURL() {
		return null;
	}

	public String getFullXMLChunk(LanguageCode lang) {
		String a = "";
		for (_Action act : actions) {
			a += act.toXML();
		}
		return "<actionbar mode=\"" + isOn + "\" caption=\"" + caption + "\" hint=\"" + hint + "\">" + a + "</actionbar>";
	}

	public String getShortXMLChunk(LanguageCode lang) {
		return getFullXMLChunk(lang);
	}

	public boolean isEditable() {
		return false;
	}

	@Override
	public String toXML() {
		String a = "";
		for (_Action act : actions) {
			a += act.toXML();
		}
		return "<actionbar mode=\"" + isOn + "\" caption=\"" + caption + "\" hint=\"" + hint + "\">" + a + "</actionbar>";
	}

	@Override
	public Object toJSON() {
		return this;
	}

}
