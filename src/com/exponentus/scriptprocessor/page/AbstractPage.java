package com.exponentus.scriptprocessor.page;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.exponentus.common.dao.ReadingMarkDAO;
import com.exponentus.common.model.Attachment;
import com.exponentus.common.model.ReadingMark;
import com.exponentus.dataengine.RuntimeObjUtil;
import com.exponentus.dataengine.jpa.DAO;
import com.exponentus.dataengine.jpa.IAppEntity;
import com.exponentus.dataengine.jpa.SecureAppEntity;
import com.exponentus.dataengine.jpa.ViewPage;
import com.exponentus.env.Environment;
import com.exponentus.exception.SecureException;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting.POJOObjectAdapter;
import com.exponentus.scripting._POJOListWrapper;
import com.exponentus.scripting._POJOObjectWrapper;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._Validation;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.actions._Action;
import com.exponentus.scripting.actions._ActionBar;
import com.exponentus.scripting.actions._ActionType;
import com.exponentus.scriptprocessor.ScriptHelper;
import com.exponentus.scriptprocessor.ScriptShowField;
import com.exponentus.server.Server;
import com.exponentus.user.IUser;
import com.exponentus.util.StringUtil;
import com.exponentus.util.Util;
import com.exponentus.webserver.servlet.PublishAsType;

import administrator.dao.LanguageDAO;
import administrator.model.Language;

public abstract class AbstractPage extends ScriptHelper implements IPageScript {
	private static final String messageTag = "msg";
	private _WebFormData formData;
	private PageOutcome result;

	@Override
	public void setOutcome(PageOutcome o) {
		result = o;
	}

	@Override
	public void setSession(_Session ses) {
		setSes(ses);
		result.setSession(ses);
	}

	public void addValue(String entryName, Object value) {
		if (value == null) {
			result.addContent(new ScriptShowField(entryName, ""));
		} else if (value instanceof String) {
			result.addContent(new ScriptShowField(entryName, (String) value));
		} else if (value instanceof Date) {
			result.addContent(new ScriptShowField(entryName, Util.convertDataTimeToString(((Date) value))));
		} else if (value instanceof BigDecimal) {
			result.addContent(new ScriptShowField(entryName, value.toString()));
		}
	}

	protected Map<LanguageCode, String> getLocalizedNames(_Session session, _WebFormData formData) {
		Map<LanguageCode, String> localizedNames = new HashMap<LanguageCode, String>();
		List<Language> langs = new LanguageDAO(session).findAll();
		for (Language l : langs) {
			String ln = formData.getValueSilently(l.getCode().name().toLowerCase() + "localizedname");
			if (!ln.isEmpty()) {
				localizedNames.put(l.getCode(), ln);
			} else {
				localizedNames.put(l.getCode(), formData.getValueSilently("name"));
			}
		}
		return localizedNames;
	}

	protected void setError(Exception e) {
		result.setException(e);
		if (e instanceof SecureException) {
			result.setForbiddenRequest();
		} else {
			setBadRequest();
		}
	}

	public void setPublishAsType(PublishAsType respType) {
		result.setPublishAs(respType);
	}

	public void showFile(String filePath, String fileName) {
		result.setPublishAs(PublishAsType.OUTPUTSTREAM);
		result.setFile(filePath, fileName);
	}

	public boolean showAttachment(Attachment att) {
		try {
			String filePath = getTmpDirPath() + File.separator + StringUtil.getRandomText() + att.getRealFileName();
			File attFile = new File(filePath);
			FileUtils.writeByteArrayToFile(attFile, att.getFile());
			showFile(filePath, att.getRealFileName());
			Environment.fileToDelete.add(filePath);
			return true;
		} catch (IOException ioe) {
			Server.logger.errorLogEntry(ioe);
			return false;
		}
	}

	@Override
	public void setFormData(_WebFormData formData) {
		this.formData = formData;
	}

	protected void setValidation(_Validation obj) {
		result.setInfoMessageType(InfoMessageType.VALIDATION_ERROR);
		result.setValidation(obj);
	}

	protected void setValidation(String localizedMessage) {
		_Validation ve = new _Validation();
		ve.addError("", "", localizedMessage);
		setValidation(ve);
	}

	protected void addContent(String elementName, List<?> list) {
		List<IPOJOObject> newList = new ArrayList<IPOJOObject>();
		for (Object element : list) {
			newList.add(new POJOObjectAdapter<Object>() {
				@Override
				public String getShortXMLChunk(_Session ses) {
					StringBuffer val = new StringBuffer(500);
					val.append("<entry>");
					val.append(element.toString());
					return val.append("</entry>").toString();
				}
			});

		}
		result.addObject(new _POJOListWrapper<IPOJOObject>(newList, getSes(), elementName));
	}

	protected void addContent(String elementName, String someValue) {
		result.addObject(new _POJOObjectWrapper(new POJOObjectAdapter<Object>() {
			@Override
			public String getFullXMLChunk(_Session ses) {
				return "<" + elementName + ">" + someValue + "</" + elementName + ">";
			}

			@Override
			public Object getJSONObj(_Session ses) {
				Map<Object, Object> map = new HashMap<>();
				map.put(elementName, someValue);
				return map;
			}
		}, getSes()));
	}

	protected void addError(IOutcomeObject obj) {
		setBadRequest();
		result.setInfoMessageType(InfoMessageType.SERVER_ERROR);
		result.addContent(obj);
	}

	protected void addWarning(String value) {
		result.setInfoMessageType(InfoMessageType.WARNING);
		addValue(messageTag, value);
	}

	protected void addValue(String value) {
		addValue(messageTag, value);
	}

	protected void addContent(IOutcomeObject obj) {
		result.addContent(obj);
	}

	protected void addContent(Collection<IOutcomeObject> list) {
		result.addContent(list);
	}

	protected void addContent(IPOJOObject document) {
		result.addObject(new _POJOObjectWrapper(document, getSes()));
	}

	@Deprecated
	protected void addContent(_POJOListWrapper<IPOJOObject> wrappedList) {
		result.addContent(wrappedList);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void addContent(List<? extends IAppEntity> list) {
		result.addContent(new _POJOListWrapper(list, getSes()));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void addContent(List<? extends IAppEntity> list, int maxPage, long count, int currentPage) {
		result.addContent(new _POJOListWrapper(list, maxPage, count, currentPage, getSes()));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void addContent(List<? extends IAppEntity> list, int maxPage, long count, int currentPage, String keyword) {
		result.addContent(new _POJOListWrapper(list, maxPage, count, currentPage, getSes(), keyword));
	}

	protected void startSaveFormTransact(IAppEntity entity) {
		getSes().addFormTransaction(entity, formData.getReferrer());

	}

	protected void finishSaveFormTransact(IAppEntity entity) {
		result.setRedirectURL(getSes().getTransactRedirect(entity));
		if (result.getType() != InfoMessageType.VALIDATION_ERROR && result.getType() != InfoMessageType.SERVER_ERROR) {
			result.setFlash(entity.getId().toString());
			result.setInfoMessageType(InfoMessageType.DOCUMENT_SAVED);
		}
	}

	protected void setRedirect(String url) {
		result.setRedirectURL(url);
	}

	protected void setBadRequest() {
		result.setBadRequest();
	}

	protected IPOJOObject getACL(_Session ses, SecureAppEntity<UUID> entity) {
		return entity.getACL(ses);
	}

	protected _ActionBar getSimpleActionBar(_Session session, String type, LanguageCode lang) {
		_ActionBar actionBar = new _ActionBar(session);
		_Action newDocAction = new _Action(getLocalizedWord("new_", lang), "", "new_" + type);
		newDocAction.setURL("p?id=" + type);
		actionBar.addAction(newDocAction);
		actionBar.addAction(new _Action(getLocalizedWord("del_document", lang), "", _ActionType.DELETE_DOCUMENT));
		return actionBar;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected _POJOListWrapper<? extends IPOJOObject> getViewPage(DAO<? extends IPOJOObject, UUID> dao, _WebFormData formData) {
		int pageNum = 1;
		int pageSize = dao.getSession().pageSize;
		if (formData.containsField("page")) {
			pageNum = formData.getNumberValueSilently("page", pageNum);
		}
		long count = dao.getCount();
		int maxPage = RuntimeObjUtil.countMaxPage(count, pageSize);
		if (pageNum == 0) {
			pageNum = maxPage;
		}
		int startRec = RuntimeObjUtil.calcStartEntry(pageNum, pageSize);
		List<? extends IPOJOObject> list = dao.findAll(startRec, pageSize);
		return new _POJOListWrapper(list, maxPage, count, pageNum, getSes());
	}

	public void markAsRead(String id, IUser<Long> user) {
		ReadingMarkDAO rmDao = new ReadingMarkDAO();
		try {
			UUID uuidId = UUID.fromString(id);
			rmDao.markAsRead(uuidId, user);
		} catch (IllegalArgumentException e) {
			Server.logger.errorLogEntry(e);
		}
	}

	public boolean isRead(String id, IUser<Long> user) {
		ReadingMarkDAO rmDao = new ReadingMarkDAO();
		try {
			UUID uuidId = UUID.fromString(id);
			return rmDao.isRead(uuidId, user);
		} catch (IllegalArgumentException e) {
			Server.logger.errorLogEntry(e);
		}
		return false;
	}

	public ViewPage<ReadingMark> whoRead(String id) {
		ReadingMarkDAO rmDao = new ReadingMarkDAO();
		try {
			UUID uuidId = UUID.fromString(id);
			return rmDao.findAllWhoRead(uuidId);
		} catch (IllegalArgumentException e) {
			Server.logger.errorLogEntry(e);
		}
		return null;
	}

	@Override
	public PageOutcome processCode(String method) {
		try {
			if (method.equalsIgnoreCase("POST")) {
				doPOST(getSes(), formData);
			} else if (method.equalsIgnoreCase("PUT")) {
				doPUT(getSes(), formData);
			} else if (method.equalsIgnoreCase("DELETE")) {
				doDELETE(getSes(), formData);
			} else {
				doGET(getSes(), formData);
			}
		} catch (Exception e) {
			result.setException(e);
			result.setInfoMessageType(InfoMessageType.SERVER_ERROR);
			result.setVeryBadRequest();
			error(e);
		}
		return result;
	}

	public abstract void doGET(_Session session, _WebFormData formData) throws Exception;

	public abstract void doPUT(_Session session, _WebFormData formData) throws Exception;

	public abstract void doPOST(_Session session, _WebFormData formData) throws Exception;

	public abstract void doDELETE(_Session session, _WebFormData formData) throws Exception;
}
