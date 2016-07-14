package com.exponentus.localization;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import com.exponentus.messaging.MessageType;

public class TemplatesSet {
	private HashMap<String, String> templs;
	private String appName;
	private File templateDir;

	public TemplatesSet(String appName, String templatesFilePath) {
		this.appName = appName;
		templateDir = new File(templatesFilePath);
		templs = new HashMap<String, String>();
		templs.put("appname", appName);
		templs.put("lang", LanguageCode.ENG.name());
	}

	public String getTemplate(MessageType type, String templateName, LanguageCode lang) {
		String tmpl = null;
		templs.put("lang", lang.name());
		String msgType = type.name().toLowerCase();
		String key = msgType + "_" + templateName + "_" + lang.toString().toLowerCase();
		try {
			tmpl = templs.get(key);
			if (tmpl == null) {
				File templateFile = new File(templateDir.getAbsoluteFile() + File.separator + msgType + File.separator + templateName + File.separator
				        + lang.toString().toLowerCase() + ".html");
				if (templateFile.exists()) {
					tmpl = FileUtils.readFileToString(templateFile);
					templs.put(key, tmpl);
				} else {
					File defaultTemplFile = new File(templateDir.getAbsoluteFile() + File.separator + msgType + File.separator + "default.html");
					tmpl = FileUtils.readFileToString(defaultTemplFile);
				}
			}
			return tmpl;
		} catch (Exception e) {
			return null;
		}
	}

	public void reset() {
		templs.clear();
		templs.put("appname", appName);
		templs.put("lang", LanguageCode.ENG.name());
	}

	public StringBuffer toXML(String lang) {
		StringBuffer output = new StringBuffer(1000);

		return output;
	}

}
