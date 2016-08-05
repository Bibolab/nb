package com.exponentus.common.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.exponentus.dataengine.jpa.IAppEntity;
import com.exponentus.dataengine.jpa.SecureAppEntity;
import com.exponentus.dataengine.system.IEmployee;
import com.exponentus.dataengine.system.IExtUserDAO;
import com.exponentus.env.Environment;
import com.exponentus.scripting.POJOObjectAdapter;
import com.exponentus.scripting._Session;
import com.exponentus.user.AnonymousUser;
import com.exponentus.user.SuperUser;
import com.exponentus.user.UndefinedUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "entityKind", "identifier", "readers", "editors" })
public class ACL extends POJOObjectAdapter<UUID> {
	public Map<Long, Object> readers = new HashMap<>();
	public Map<Long, Object> editors = new HashMap<>();

	@JsonIgnore
	private IExtUserDAO eDao = Environment.getExtUserDAO();

	@JsonIgnore
	private UUID id;

	@JsonIgnore
	private _Session ses;

	@SuppressWarnings("unchecked")
	public ACL(_Session ses, IAppEntity e) {
		this.ses = ses;
		SecureAppEntity<UUID> entity = (SecureAppEntity<UUID>) e;
		id = e.getId();

		Iterator<Long> it = entity.getReaders().iterator();
		while (it.hasNext()) {
			long id = it.next();
			readers.put(id, getUserName(id));
		}
		it = entity.getEditors().iterator();
		while (it.hasNext()) {
			long id = it.next();
			editors.put(id, getUserName(id));
		}
	}

	@Override
	public String getEntityKind() {
		return "ACL";
	}

	@Override
	public String getIdentifier() {
		try {
			return id.toString();
		} catch (Exception e) {
			return "null";
		}
	}

	@JsonIgnore
	@Override
	public String getURL() {
		return null;
	}

	@Override
	public String getFullXMLChunk(_Session ses) {
		StringBuilder chunk = new StringBuilder(500);
		try {
			String asText = "";
			for (Entry<Long, Object> entry : readers.entrySet()) {
				asText += "<entry id=\"" + entry.getKey() + "\">" + entry.getValue() + "</entry>";
			}
			chunk.append("<readers>" + asText + "</readers>");
		} catch (NullPointerException e) {
			chunk.append("<readers></readers>");
		}
		try {
			String asText = "";
			for (Entry<Long, Object> entry : editors.entrySet()) {
				asText += "<entry id=\"" + entry.getKey() + "\">" + entry.getValue() + "</entry>";
			}
			chunk.append("<editors>" + asText + "</editors>");
		} catch (NullPointerException e) {
			chunk.append("<editors></editors>");
		}

		return chunk.toString();
	}

	@Override
	public String getShortXMLChunk(_Session ses) {
		return getFullXMLChunk(ses);
	}

	@JsonIgnore
	@Override
	public boolean isWasRead() {
		return true;
	}

	@Override
	public Object getJSONObj(_Session ses) {
		return this;
	}

	@JsonIgnore
	@Override
	public boolean isEditable() {
		return false;
	}

	private String getUserName(long id) {
		if (id > 0) {
			IEmployee emp = eDao.getEmployee(id);
			if (emp != null) {
				return emp.getName();
			} else {
				return UndefinedUser.USER_NAME;
			}
		} else if (id == 0) {
			return AnonymousUser.USER_NAME;
		} else if (id == -1) {
			return SuperUser.USER_NAME;
		} else {
			return UndefinedUser.USER_NAME;
		}
	}
}
