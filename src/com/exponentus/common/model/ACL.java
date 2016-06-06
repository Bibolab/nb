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
import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting._Session;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "entityKind", "identifier", "readers", "editors" })
public class ACL implements IPOJOObject {
	public Map<Long, Object> readers = new HashMap<>();
	public Map<Long, Object> editors = new HashMap<>();

	@JsonIgnore
	private UUID id;

	@JsonIgnore
	private _Session ses;

	@SuppressWarnings("unchecked")
	public ACL(_Session ses, IAppEntity e) {
		this.ses = ses;
		SecureAppEntity<UUID> entity = (SecureAppEntity<UUID>) e;
		id = e.getId();
		IExtUserDAO eDao = Environment.getExtUserDAO();
		Iterator<Long> it = entity.getReaders().iterator();
		while (it.hasNext()) {
			IEmployee emp = eDao.getEmployee(it.next());
			readers.put(emp.getUserID(), emp.getName());
		}
		it = entity.getEditors().iterator();
		while (it.hasNext()) {
			IEmployee emp = eDao.getEmployee(it.next());
			editors.put(emp.getUserID(), emp.getName());
		}
	}

	@Override
	public String getEntityKind() {
		return "ACL";
	}

	@Override
	public String getIdentifier() {
		return id.toString();
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

}