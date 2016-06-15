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
import com.exponentus.dataengine.jpa.IAppEntity;
import com.exponentus.dataengine.jpa.ViewPage;
import com.exponentus.env.Environment;
import com.exponentus.exception.SecureException;
import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting.POJOObjectAdapter;
import com.exponentus.scripting._POJOListWrapper;
import com.exponentus.scripting._POJOObjectWrapper;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._Validation;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scriptprocessor.ScriptHelper;
import com.exponentus.scriptprocessor.SimpleValue;
import com.exponentus.server.Server;
import com.exponentus.user.IUser;
import com.exponentus.util.StringUtil;
import com.exponentus.util.Util;
import com.exponentus.webserver.servlet.PublishAsType;

public abstract class AbstractPage extends ScriptHelper implements IPageScript {
	private static final String DEFAULT_MESSAGE_TAG = "msg";
	protected _WebFormData formData;
	protected PageOutcome result;

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
			result.addContent(new SimpleValue(entryName, ""));
		} else if (value instanceof String) {
			result.addContent(new SimpleValue(entryName, (String) value));
		} else if (value instanceof Date) {
			result.addContent(new SimpleValue(entryName, Util.convertDataTimeToString(((Date) value))));
		} else if (value instanceof Integer) {
			result.addContent(new SimpleValue(entryName, value.toString()));
		} else if (value instanceof BigDecimal) {
			result.addContent(new SimpleValue(entryName, value.toString()));
		}
	}

	protected void addError(IOutcomeObject obj) {
		setBadRequest();
		result.setInfoMessageType(InfoMessageType.SERVER_ERROR);
		result.addContent(obj);
	}

	protected void addWarning(String value) {
		result.setInfoMessageType(InfoMessageType.WARNING);
		addValue(DEFAULT_MESSAGE_TAG, value);
	}

	protected void addValue(String value) {
		addValue(DEFAULT_MESSAGE_TAG, value);
	}

	protected void setError(Exception e) {
		result.setException(e);
		if (e instanceof SecureException) {
			result.setForbiddenRequest();
		} else {
			setBadRequest();
		}
	}

	public void showFile(String filePath, String fileName) {
		result.setPublishAs(PublishAsType.OUTPUTSTREAM);
		result.setFile(filePath, fileName);
	}

	public boolean showAttachment(byte[] att) {
		try {
			String tempFileName = StringUtil.getRandomText();
			String filePath = getTmpDirPath() + File.separator + StringUtil.getRandomText() + tempFileName;
			File attFile = new File(filePath);
			FileUtils.writeByteArrayToFile(attFile, att);
			showFile(filePath, tempFileName);
			Environment.fileToDelete.add(filePath);
			return true;
		} catch (IOException ioe) {
			Server.logger.errorLogEntry(ioe);
			return false;
		}
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

	protected void addContent(IOutcomeObject obj) {
		result.addContent(obj);
	}

	protected void addContent(Collection<IOutcomeObject> list) {
		result.addContent(list);
	}

	protected void addContent(IPOJOObject document) {
		result.addObject(new _POJOObjectWrapper(document, getSes()));
	}

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
		if (result.getInfoMessageType() != InfoMessageType.VALIDATION_ERROR && result.getInfoMessageType() != InfoMessageType.SERVER_ERROR) {
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
