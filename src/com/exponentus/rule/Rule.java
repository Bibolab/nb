package com.exponentus.rule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.exponentus.appenv.AppEnv;
import com.exponentus.exception.RuleException;
import com.exponentus.rule.constans.RunMode;
import com.exponentus.rule.page.ElementRule;
import com.exponentus.rule.page.IElement;
import com.exponentus.rule.page.RuleType;
import com.exponentus.util.XMLUtil;
import com.exponentus.webserver.servlet.PublishAsType;

public abstract class Rule implements IElement {
	public RunMode isOn = RunMode.ON;
	public boolean isValid = true;
	public String description;
	public String id = "unknown";
	public String xsltFile;
	@Deprecated
	public PublishAsType publishAs = PublishAsType.XML;
	public int hits;
	public ArrayList<Caption> captions = new ArrayList<>();
	public AppEnv env;
	public ArrayList<ElementRule> elements = new ArrayList<>();
	protected org.w3c.dom.Document doc;
	protected RuleType type = RuleType.UNKNOWN;

	private boolean allowAnonymousAccess;

	protected Rule(AppEnv env, File docFile) throws RuleException {
		try {
			this.env = env;
			DocumentBuilderFactory pageFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder pageBuilder = pageFactory.newDocumentBuilder();
			Document xmlFileDoc = pageBuilder.parse(docFile.toString());
			doc = xmlFileDoc;
			id = XMLUtil.getTextContent(doc, "/rule/@id", true);
			if (id.equals("")) {
				id = FilenameUtils.removeExtension(docFile.getName());
			}
			AppEnv.logger.debugLogEntry("Load rule: " + this.getClass().getSimpleName() + ", id=" + id);
			if (XMLUtil.getTextContent(doc, "/rule/@mode").equalsIgnoreCase("off")) {
				isOn = RunMode.OFF;
				isValid = false;
			}

			if (XMLUtil.getTextContent(doc, "/rule/@anonymous").equalsIgnoreCase("on")) {
				allowAnonymousAccess = true;
			}

			description = XMLUtil.getTextContent(doc, "/rule/description");

			NodeList captionList = XMLUtil.getNodeList(doc, "/rule/caption");
			for (int i = 0; i < captionList.getLength(); i++) {
				Caption c = new Caption(captionList.item(i));
				if (c.isOn == RunMode.ON) {
					captions.add(c);
				}
			}

		} catch (SAXParseException spe) {
			AppEnv.logger.errorLogEntry("XML-file structure error (" + docFile.getAbsolutePath() + ")");
			AppEnv.logger.errorLogEntry(spe);
		} catch (FileNotFoundException e) {
			throw new RuleException("Rule \"" + docFile.getAbsolutePath() + "\" has not found");
		} catch (ParserConfigurationException e) {
			AppEnv.logger.errorLogEntry(e);
		} catch (IOException e) {
			AppEnv.logger.errorLogEntry(e);
		} catch (SAXException se) {
			AppEnv.logger.errorLogEntry(se);
		}

	}

	protected void setIsOn(String isOnAsText) {
		if (isOnAsText.equalsIgnoreCase("on")) {
			isOn = RunMode.ON;
		} else {
			isOn = RunMode.OFF;
		}
	}

	protected void setDescription(String d) {
		description = d;
	}

	protected void setID(String id) {
		this.id = id;
	}

	protected void setCaptions(String[] id) {

	}

	public void plusHit() {
		hits++;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " id=" + id;
	}

	public String getXSLT() {
		return xsltFile.replace("\\", File.separator);
	}

	public String getAsXML() {
		return "";
	}

	public boolean isAnonymousAccessAllowed() {
		return allowAnonymousAccess;
	}

	public String getRuleID() {
		return type + "_" + id;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public AppEnv getAppEnv() {
		return env;
	}

	@Override
	public ArrayList<Caption> getCaptions() {
		return captions;
	}

}
