package com.exponentus.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.exponentus.dataengine.jpa.AppEntity;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.util.TimeUtil;

import administrator.dao.LanguageDAO;
import administrator.model.Language;

@MappedSuperclass
public class SimpleReferenceEntity extends AppEntity<UUID> {
	@Column(length = 128)
	private String name;

	@Column(name = "localized_name")
	private Map<LanguageCode, String> localizedName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public Map<LanguageCode, String> getLocalizedName() {
		return localizedName;
	}

	public String getLocalizedName(LanguageCode lang) {
		try {
			return localizedName.get(lang);
		} catch (Exception e) {
			return name;
		}
	}

	public void setLocalizedName(Map<LanguageCode, String> name) {
		this.localizedName = name;
	}

	@Override
	public String getFullXMLChunk(_Session ses) {
		StringBuilder chunk = new StringBuilder(1000);
		chunk.append("<regdate>" + TimeUtil.dateTimeToStringSilently(regDate) + "</regdate>");
		chunk.append("<name>" + getName().replace("&", "&amp;") + "</name>");
		chunk.append("<localizednames>");
		LanguageDAO lDao = new LanguageDAO(ses);
		List<Language> list = lDao.findAll();
		for (Language l : list) {
			chunk.append("<entry id=\"" + l.getCode() + "\">" + getLocalizedName(l.getCode()).replace("&", "&amp;") + "</entry>");
		}
		chunk.append("</localizednames>");
		return chunk.toString();
	}

	@Override
	public String getShortXMLChunk(_Session ses) {
		return "<name>" + getName().replace("&", "&amp;") + "</name>";
	}

	@Override
	public List<Attachment> getAttachments() {
		return new ArrayList<>();
	}

	@Override
	public void setAttachments(List<Attachment> attachments) {

	}

}
