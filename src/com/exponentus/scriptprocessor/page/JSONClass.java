package com.exponentus.scriptprocessor.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.exponentus.scripting._Validation;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("outcome")
public class JSONClass {
	private Collection<Object> objects = new ArrayList<Object>();
	private Map<String, String> captions;
	private InfoMessageType type;
	private String redirectURL;
	private String flash;
	private _Validation validation;

	public Map<String, String> getCaptions() {
		return captions;
	}

	public void setCaptions(Map<String, String> captions) {
		this.captions = captions;
	}

	public InfoMessageType getType() {
		return type;
	}

	public void setType(InfoMessageType type) {
		this.type = type;
	}

	public String getRedirectURL() {
		return redirectURL;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	public String getFlash() {
		return flash;
	}

	public void setFlash(String flash) {
		this.flash = flash;
	}

	public Collection<Object> getObjects() {
		return objects;
	}

	public void setObjects(Collection<IOutcomeObject> objects) {
		for (IOutcomeObject obj : objects) {
			this.objects.add(obj.toJSON());
		}
	}

	public void setObject(IOutcomeObject object) {
		this.objects.add(object);
	}

	public void setValidation(_Validation vp) {
		this.validation = vp;
	}

	public _Validation getValidation() {
		return this.validation;
	}
}