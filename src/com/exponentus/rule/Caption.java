package com.exponentus.rule;

import org.w3c.dom.Node;

import com.exponentus.rule.constans.RunMode;
import com.exponentus.util.XMLUtil;

public class Caption {
	public RunMode isOn = RunMode.OFF;
	public String captionID = "";
	public String value = "";

	public Caption(Node node) {
		captionID = XMLUtil.getTextContent(node, "@name", false);
		if (!captionID.equals("")) {
			value = XMLUtil.getTextContent(node, ".", false);
			isOn = RunMode.ON;
		}
	}

	public Caption(String value) {
		this.value = value;
		isOn = RunMode.ON;
	}
}
