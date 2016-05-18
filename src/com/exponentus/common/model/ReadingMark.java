package com.exponentus.common.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Convert;

import com.exponentus.dataengine.jpa.SimpleAppEntity;
import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting._Session;
import com.exponentus.util.Util;

/**
 * Created by Kaira on 27/12/15.
 */

@Entity
@Table(name = "reading_marks")
@NamedQuery(name = "ReadingMark.findAll", query = "SELECT m FROM ReadingMark AS m")
public class ReadingMark extends SimpleAppEntity implements IPOJOObject {

	@Convert("uuidConverter")
	private UUID docId;

	@Column(name = "user_id")
	private Long user;

	@Column(name = "mark_date")
	private Date markDate;

	public UUID getDocId() {
		return docId;
	}

	public void setDocId(UUID docId) {
		this.docId = docId;
	}

	public Long getUser() {
		return user;
	}

	public void setUser(Long user) {
		this.user = user;
	}

	public Date getMarkDate() {
		return markDate;
	}

	public void setMarkDate(Date markDate) {
		this.markDate = markDate;
	}

	@Override
	public String getEntityKind() {
		return this.getClass().getSimpleName().toLowerCase();
	}

	@Override
	public String getIdentifier() {
		return id.toString();
	}

	@Override
	public String getURL() {
		return "";
	}

	@Override
	public String getFullXMLChunk(_Session ses) {
		StringBuilder chunk = new StringBuilder(1000);
		chunk.append("<user>" + user + "</user>");
		chunk.append("<markdate>" + Util.simpleDateTimeFormat.format(markDate) + "</markdate>");
		return chunk.toString();
	}

	@Override
	public String getShortXMLChunk(_Session ses) {
		return getFullXMLChunk(ses);
	}

	@Override
	public boolean isWasRead() {
		return false;
	}

	@Override
	public Object getJSONObj(_Session ses) {
		return this;
	}

	@Override
	public boolean isEditable() {
		return false;
	}

}
