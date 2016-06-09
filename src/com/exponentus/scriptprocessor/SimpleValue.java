package com.exponentus.scriptprocessor;

import com.exponentus.scriptprocessor.page.IOutcomeObject;
import com.exponentus.util.XMLUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class SimpleValue implements IOutcomeObject {
	private String name;
	private String value = "";

	@JsonIgnore
	private String XMLPiece;

	public SimpleValue(String name, String value) {
		this.name = name;
		this.value = value;
		XMLPiece = "<" + name + ">" + XMLUtil.getAsTagValue(value) + "</" + name + ">";
	}

	public String getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	@JsonIgnore
	@Override
	public String toString() {
		return "name=" + name + ", value=" + value;
	}

	@JsonIgnore
	@Override
	public String toXML() {
		return XMLPiece;
	}

	@JsonIgnore
	@Override
	public Object toJSON() {
		return this;
	}

}
