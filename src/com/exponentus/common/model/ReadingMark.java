package com.exponentus.common.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.exponentus.dataengine.jpa.SimpleAppEntity;

/**
 * Created by Kaira on 27/12/15.
 */

@Entity
@Table(name = "reading_marks")
@NamedQuery(name = "ReadingMark.findAll", query = "SELECT m FROM ReadingMark AS m")
public class ReadingMark extends SimpleAppEntity {

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

}
