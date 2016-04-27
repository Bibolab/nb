package com.exponentus.scriptprocessor;

import com.exponentus.rule.constans.ValueSourceType;
import com.exponentus.scriptprocessor.page.IOutcomeObject;
import com.exponentus.util.XMLUtil;

import groovy.lang.GroovyObject;

public class ScriptShowField implements IOutcomeObject {
	private String name;
	private String value = "";
	private String idValue;
	private boolean hasAttr;
	private String XMLPiece;

	public ScriptShowField(String name, String value, boolean noConvert) {
		this.name = name;
		this.value = value;
		XMLPiece = "<" + name + ">" + (noConvert ? value : XMLUtil.getAsTagValue(value)) + "</" + name + ">";
	}

	public ScriptShowField(String name, String value) {
		this.name = name;
		this.value = value;
		XMLPiece = "<" + name + ">" + XMLUtil.getAsTagValue(value) + "</" + name + ">";
	}

	public ScriptShowField(String name, int idv, String value) {
		this.name = name;
		this.value = value;
		this.idValue = Integer.toString(idv);
		hasAttr = true;
		XMLPiece = "<" + name + " attrval=\"" + idValue + "\">" + XMLUtil.getAsTagValue(value) + "</" + name + ">";
	}

	public ScriptShowField(String name, String idValue, String value) {
		this.name = name;
		this.value = value;
		this.idValue = idValue;
		hasAttr = true;
		XMLPiece = "<" + name + " attrval=\"" + idValue + "\">" + XMLUtil.getAsTagValue(value) + "</" + name + ">";
	}

	public ValueSourceType getSourceType() {
		return ValueSourceType.DOCFIELD;
	}

	public String getValue() {
		return value;
	}

	public Class<GroovyObject> getCompiledClass() {
		return null;
	}

	public String getName() {
		return name;
	}

	public boolean hasAttrValue() {
		return hasAttr;
	}

	public ValueSourceType getAttrSourceType() {
		return ValueSourceType.UNKNOWN;
	}

	@Override
	public String toString() {
		return XMLPiece;
	}

	@Override
	public String toXML() {
		return XMLPiece;
	}

	@Override
	public Object toJSON() {
		return null;
	}

}
