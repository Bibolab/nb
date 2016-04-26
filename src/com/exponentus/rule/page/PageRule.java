package com.exponentus.rule.page;

import java.io.File;

import org.apache.commons.lang3.ArrayUtils;
import org.w3c.dom.NodeList;

import com.exponentus.appenv.AppEnv;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.exception.RuleException;
import com.exponentus.rule.Rule;
import com.exponentus.rule.constans.RunMode;
import com.exponentus.rule.constans.ValueSourceType;
import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting._Session;
import com.exponentus.util.XMLUtil;
import com.exponentus.webserver.servlet.PublishAsType;

public class PageRule extends Rule implements IElement, IPOJOObject {
	public boolean isValid;
	public CachingStrategyType caching = CachingStrategyType.NO_CACHING;

	public PageRule(AppEnv env, File ruleFile) throws RuleException {
		super(env, ruleFile);

		try {

			String cachingValue = XMLUtil.getTextContent(doc, "/rule/caching", false);
			if (!cachingValue.equalsIgnoreCase("")) {
				caching = CachingStrategyType.valueOf(cachingValue);
			}

			NodeList fields = XMLUtil.getNodeList(doc, "/rule/element");
			for (int i = 0; i < fields.getLength(); i++) {
				ElementRule element = new ElementRule(fields.item(i), this);
				if (element.isOn != RunMode.OFF && element.isValid) {
					elements.add(element);
				}
			}

			String xsltAppsPath = "";
			if (Environment.isDevMode()) {
				if (EnvConst.ADMINISTRATOR_APP_NAME.equalsIgnoreCase(env.appName)) {
					xsltAppsPath = Environment.getKernelDir() + "webapps" + File.separator + env.appName + File.separator + "xsl";
				} else if (ArrayUtils.contains(EnvConst.OFFICEFRAME_APPS, env.appName)) {
					xsltAppsPath = Environment.getOfficeFrameDir() + "webapps" + File.separator + env.appName + File.separator + "xsl";
				} else {
					xsltAppsPath = "webapps" + File.separator + env.appName + File.separator + "xsl";
				}
			} else {
				xsltAppsPath = "webapps" + File.separator + env.appName + File.separator + "xsl";
			}

			type = RuleType.PAGE;

			// TODO need to improve
			xsltFile = XMLUtil.getTextContent(doc, "/rule/xsltfile");
			if (!xsltFile.equals("")) {
				publishAs = PublishAsType.HTML;
				if (xsltFile.equalsIgnoreCase("default") || xsltFile.equals("*")) {
					xsltFile = xsltAppsPath + File.separator + type.name().toLowerCase() + File.separator + id + ".xsl";
				} else if (xsltFile.equalsIgnoreCase("default_staff")) {
					String xsltStaffAppsPath = "";
					if (Environment.isDevMode()) {
						xsltStaffAppsPath = Environment.getOfficeFrameDir() + "webapps" + File.separator + EnvConst.STAFF_APP_NAME + File.separator
						        + "xsl";
					} else {
						xsltStaffAppsPath = "webapps" + File.separator + EnvConst.STAFF_APP_NAME + File.separator + "xsl";
					}
					xsltFile = xsltStaffAppsPath + File.separator + type.name().toLowerCase() + File.separator + id + ".xsl";
				} else if (xsltFile.equalsIgnoreCase("default_MunicipalProperty")) {
					String xsltStaffAppsPath = "webapps" + File.separator + "MunicipalProperty" + File.separator + "xsl";
					xsltFile = xsltStaffAppsPath + File.separator + type.name().toLowerCase() + File.separator + id + ".xsl";
				} else {
					xsltFile = xsltAppsPath + File.separator + xsltFile;
				}
			}

			isValid = true;
		} catch (Exception e) {
			AppEnv.logger.errorLogEntry(e);
		}
	}

	@Override
	public String toString() {
		return "PAGE id=" + id + ", ison=" + isOn;
	}

	@Override
	public String getIdentifier() {
		return id;
	}

	@Override
	public String getURL() {
		return "p?id=rule-form&amp;application=" + env.appName + "&amp;docid=" + id;
	}

	@Override
	public String getFullXMLChunk(_Session ses) {
		StringBuilder chunk = new StringBuilder(1000);
		chunk.append("<id>" + id + "</id>");
		chunk.append("<ison>" + isOn.name() + "</ison>");
		chunk.append("<xsltfile>" + xsltFile + "</xsltfile>");
		chunk.append("<issecured>" + isSecured + "</issecured>");
		chunk.append("<caching>" + caching.name() + "</caching>");
		chunk.append("<description>" + description + "</description>");
		chunk.append("<elements>");
		for (ElementRule e : elements) {
			chunk.append("<element>");
			chunk.append("<name>" + e.name + "</name>");
			chunk.append("<type>" + e.type.name() + "</type>");
			if (e.type == ElementType.SCRIPT) {
				chunk.append("<scripttype>" + e.doClassName.getType() + "</scripttype>");
				chunk.append("<classname>" + e.doClassName.getClassName() + "</classname>");
				if (e.doClassName.getType() == ValueSourceType.GROOVY_FILE) {
					chunk.append("<code>" + e.doClassName.getType() + "</code>");
				}
			} else if (e.type == ElementType.INCLUDED_PAGE) {
				try {
					PageRule rule = env.ruleProvider.getRule(e.value);
					chunk.append("<url>" + rule.getURL() + "</url>");
				} catch (RuleException e1) {
					chunk.append("<error>" + e1.toString() + "</error>");
				}
			}

			chunk.append("</element>");
		}
		chunk.append("</elements>");
		return chunk.toString();

	}

	@Override
	public String getShortXMLChunk(_Session ses) {
		StringBuilder chunk = new StringBuilder(1000);
		chunk.append("<id>" + id + "</id>");
		chunk.append("<ison>" + isOn.name() + "</ison>");
		chunk.append("<issecured>" + isSecured + "</issecured>");
		chunk.append("<caching>" + caching.name() + "</caching>");
		chunk.append("<elements>");
		for (ElementRule e : elements) {
			chunk.append("<element>");
			chunk.append("<name>" + e.name + "</name>");
			chunk.append("<type>" + e.type.name() + "</type>");
			if (e.type == ElementType.SCRIPT) {
				chunk.append("<scripttype>" + e.doClassName.getType() + "</scripttype>");
				chunk.append("<classname>" + e.doClassName.getClassName() + "</classname>");
				if (e.doClassName.getType() == ValueSourceType.GROOVY_FILE) {
					chunk.append("<url>" + "p?id=code-form&amp;class=" + e.doClassName.getClassName() + "</url>");
				}
			} else if (e.type == ElementType.INCLUDED_PAGE) {
				try {
					PageRule rule = env.ruleProvider.getRule(e.value);
					chunk.append("<url>" + rule.getURL() + "</url>");
				} catch (RuleException e1) {
					chunk.append("<error>" + e1.toString() + "</error>");
				}
			}

			chunk.append("</element>");
		}
		chunk.append("</elements>");
		return chunk.toString();
	}

	@Override
	public Object getJSONObj(_Session ses) {
		return this;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

}
