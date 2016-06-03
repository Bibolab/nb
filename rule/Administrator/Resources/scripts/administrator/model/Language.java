package administrator.model;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.exponentus.dataengine.jpa.AppEntity;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.util.Util;

import administrator.dao.LanguageDAO;

@Entity
@Table(name = "_langs")
@NamedQuery(name = "Language.findAll", query = "SELECT m FROM Language AS m ORDER BY m.regDate")
public class Language extends AppEntity {
	@Enumerated(EnumType.STRING)
	@Column(nullable = true, length = 7, unique = true)
	private LanguageCode code = LanguageCode.UNKNOWN;

	@Column(length = 128, unique = true)
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

	public void setLocalizedName(Map<LanguageCode, String> name) {
		this.localizedName = name;
	}

	@Override
	public String getFullXMLChunk(_Session ses) {
		StringBuilder chunk = new StringBuilder(1000);
		chunk.append("<regdate>" + Util.dateFormat.format(regDate) + "</regdate>");
		chunk.append("<name>" + name + "</name>");
		chunk.append("<code>" + code + "</code>");
		chunk.append("<localizednames>");
		LanguageDAO lDao = new LanguageDAO(ses);
		List<Language> list = lDao.findAll();
		for (Language l : list) {
			chunk.append("<entry id=\"" + l.getCode() + "\">" + getLocalizedName(l.getCode()) + "</entry>");
		}
		chunk.append("</localizednames>");
		return chunk.toString();
	}

	@Override
	public String getShortXMLChunk(_Session ses) {
		return "<lang id=\"" + code + "\">" + localizedName.get(code) + "</lang>";
	}

	public LanguageCode getCode() {
		return code;
	}

	public void setCode(LanguageCode code) {
		this.code = code;
	}

	public String getLocalizedName(LanguageCode lang) {
		try {
			return localizedName.get(lang);
		} catch (Exception e) {
			return name;
		}
	}

}
