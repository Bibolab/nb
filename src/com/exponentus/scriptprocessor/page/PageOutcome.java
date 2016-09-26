package com.exponentus.scriptprocessor.page;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exponentus.scripting.IPOJOObject;
import org.apache.http.HttpStatus;

import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._Validation;
import com.exponentus.server.Server;
import com.exponentus.user.IUser;
import com.exponentus.webserver.servlet.PublishAsType;
import com.exponentus.webserver.servlet.xslt.SaxonTransformator;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.sf.saxon.s9api.SaxonApiException;

public class PageOutcome {
	public String name;
	public boolean disableClientCache;
	private PublishAsType publishAs;
	private int httpStatus = HttpStatus.SC_OK;
	private static final String xmlTextUTF8Header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	private List<PageOutcome> includedPage = new ArrayList<PageOutcome>();
	private Collection<IOutcomeObject> objects = new ArrayList<IOutcomeObject>();
	private Map<String, Object> objectsMap = new HashMap<>();
	private _Session ses;
	private LanguageCode lang;
	private InfoMessageType infoMessage = InfoMessageType.OK;
	private Map<String, String> captions = new HashMap<String, String>();
	private boolean isScriptResult;
	private String pageId;
	private String redirectURL;
	private String flash;
	private String filePath, fileName;
	private _Validation validation;
	private Exception exception;

	public void setSession(_Session ses) {
		this.ses = ses;
		lang = ses.getLang();
	}

	public PublishAsType getPublishAs() {
		return publishAs;
	}

	public void setPublishAs(PublishAsType publishAs) {
		this.publishAs = publishAs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addPageOutcome(PageOutcome o) {
		includedPage.add(o);
	}

	public void setObject(IOutcomeObject obj) {
		objects.clear();
		objects.add(obj);
	}

	public void addObject(IOutcomeObject obj) {
		objects.add(obj);
	}

	public void setValidation(_Validation obj) {
		validation = obj;
	}

	public void setBadRequest() {
		httpStatus = HttpStatus.SC_BAD_REQUEST;
	}

	public void setForbiddenRequest() {
		httpStatus = HttpStatus.SC_FORBIDDEN;
	}

	public void setVeryBadRequest() {
		httpStatus = HttpStatus.SC_INTERNAL_SERVER_ERROR;
	}

	public void setInfoMessageType(InfoMessageType type) {
		this.infoMessage = type;
		String keyWord = "";
		if (type == InfoMessageType.OK) {
			keyWord = "action_completed_successfully";
		} else if (type == InfoMessageType.DOCUMENT_SAVED) {
			keyWord = "document_was_saved_succesfully";
		} else if (type == InfoMessageType.SERVER_ERROR) {
			keyWord = "internal_server_error";
		} else if (type == InfoMessageType.VALIDATION_ERROR) {
			keyWord = "validation_error";
		}
		captions.put("type", Environment.vocabulary.getWord(keyWord, lang));
	}

	public InfoMessageType getInfoMessageType() {
		return infoMessage;
	}

	public void setScriptResult(boolean isScriptResult) {
		this.isScriptResult = isScriptResult;
	}

	public void setFile(String fp, String fn) {
		filePath = fp;
		fileName = fn;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public String getValue() throws IOException, SaxonApiException {
		if (publishAs == PublishAsType.HTML) {
			SaxonTransformator st = new SaxonTransformator();
			return st.toTrans(null, toCompleteXML());
		} else if (publishAs == PublishAsType.JSON) {
			return getJSONText();
		} else {
			return toCompleteXML();
		}

	}

	public String getFlash() {
		return flash;
	}

	public void setFlash(String flash) {
		this.flash = flash;
	}

	public String toXML() {
		StringBuffer result = new StringBuffer(100);
		result.append("<page>");
		if (name != null) {
			result.append("<" + name + ">");
		}
		if (isScriptResult) {
			result.append("<response type=\"RESULT_OF_PAGE_SCRIPT\"><content>");
		}

		if (validation != null) {
			result.append(validation.toXML());
		}

		for (IOutcomeObject xmlContent : objects) {
			result.append(xmlContent.toXML());
		}

		for (PageOutcome included : includedPage) {
			result.append(included.toXML());
		}

		if (isScriptResult) {
			result.append("</content></response>");
		}
		if (name != null) {
			result.append("</" + name + ">");
		}

		StringBuffer captionsText = new StringBuffer(100);
		for (String capKey : captions.keySet()) {
			String translatedVal = captions.get(capKey);
			captionsText.append("<" + capKey + " caption=\"" + translatedVal + "\"></" + capKey + ">");
		}

		result.append("<captions>" + captionsText.toString() + "</captions></page>");
		return result.toString();
	}

	public String toCompleteXML() {
		IUser<Long> user = ses.getUser();
		return xmlTextUTF8Header + "<request  lang=\"" + ses.getLang().name() + "\" id=\"" + pageId + "\" userid=\"" + user.getUserID()
		        + "\" username=\"" + user.getUserName() + "\">" + toXML() + "</request>";

	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public String getJSONText() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat(EnvConst.DEFAULT_DATETIME_FORMAT));
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

		String jsonInString = null;
		try {
			jsonInString = mapper.writeValueAsString(getJSON());
		} catch (JsonProcessingException e) {
			Server.logger.errorLogEntry(e);
		}
		return jsonInString;
	}

	public Object getJSON() {
		JSONClass clazz = new JSONClass();
		clazz.setObjects(objects);
		clazz.setData(objectsMap);
		clazz.setCaptions(captions);
		clazz.setType(infoMessage);
		clazz.setRedirectURL(redirectURL);
		clazz.setFlash(flash);
		clazz.setValidation(validation);
		return clazz;
	}

	public void setContent(IOutcomeObject wrappedObj) {
		addContent(wrappedObj);

	}

	public void addContent(IOutcomeObject element) {
		objects.add(element);

	}

	public void addContent(Collection<IOutcomeObject> elements) {
		objects.addAll(elements);

	}

	public void addContent(String key, IPOJOObject obj) {
		objectsMap.put(key, obj);
	}

	public void addContent(String key, List<IPOJOObject> list) {
		objectsMap.put(key, list);
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setCaptions(HashMap<String, String> captions) {
		this.captions.putAll(captions);
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getRedirectURL() {
		return redirectURL;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

}
