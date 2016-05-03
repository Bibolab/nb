package com.exponentus.common.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.exponentus.dataengine.jpa.SimpleAppEntity;

/**
 * Created by Kaira on 27/12/15.
 */

@Entity
@Table(name = "reading_marks")
public class ReadingMark extends SimpleAppEntity {

	private Long user;

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

	private Date markDate;

}
