package administrator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.exponentus.common.model.Attachment;
import com.exponentus.dataengine.jpa.AppEntity;
import com.exponentus.dataengine.jpa.constants.AppCode;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.util.TimeUtil;
import com.exponentus.util.XMLUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import administrator.dao.LanguageDAO;

@Entity
@Table(name = "_apps")
@NamedQuery(name = "Application.findAll", query = "SELECT m FROM Application AS m ORDER BY m.regDate")
public class Application extends AppEntity<UUID> {
	@Enumerated(EnumType.STRING)
	@Column(nullable = true, length = 16)
	private AppCode code = AppCode.UNKNOWN;

	@Column(length = 128, unique = true)
	private String name;

	@JsonIgnore
	@ManyToMany(mappedBy = "allowedApps")
	private List<User> users;

	private List<AppCode> dependencies;

	@Column(name = "localized_name")
	private Map<LanguageCode, String> localizedName;

	private String authURL;

	@Column(length = 64)
	private String defaultPage;

	private int position;

	@Column(name = "localized_descr")
	private Map<LanguageCode, String> localizedDescr;

	@Column(name = "ftsearch_fields")
	private List<String> ftSearchFields;

	@Column(name = "is_on")
	private boolean isOn;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AppCode> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<AppCode> dependencies) {
		this.dependencies = dependencies;
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

	public String getAuthURL() {
		return authURL;
	}

	public void setAuthURL(String authURL) {
		this.authURL = authURL;
	}

	public String getDefaultPage() {
		return defaultPage;
	}

	public void setDefaultPage(String defaultPage) {
		this.defaultPage = defaultPage;
	}

	public AppCode getCode() {
		return code;
	}

	public void setCode(AppCode code) {
		this.code = code;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public List<String> getFtSearchFields() {
		return ftSearchFields;
	}

	public void setFtSearchFields(List<String> ftSearchFields) {
		this.ftSearchFields = ftSearchFields;
	}

	public boolean isOn() {
		return true;
	}

	public void setOn(boolean isOn) {
		this.isOn = isOn;
	}

	@Override
	public String getFullXMLChunk(_Session ses) {
		StringBuilder chunk = new StringBuilder(1000);
		chunk.append("<regdate>" + TimeUtil.dateTimeToStringSilently(regDate) + "</regdate>");
		chunk.append("<name>" + name + "</name>");
		chunk.append("<ison>" + isOn + "</ison>");
		chunk.append("<appcode>" + code + "</appcode>");
		chunk.append("<position>" + position + "</position>");
		chunk.append("<defaultpage>" + defaultPage + "</defaultpage>");
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
		return "<app id=\"" + name + "\">" + getLocalizedName(ses.getLang()) + "</app><pos>" + position + "</pos><url>"
		        + XMLUtil.getAsTagValue(getDefaultURL()) + "</url>";
	}

	public String getLocalizedName(LanguageCode lang) {
		try {
			return localizedName.get(lang);
		} catch (Exception e) {
			return name;
		}
	}

	@Override
	public List<Attachment> getAttachments() {
		return new ArrayList<Attachment>();
	}

	@Override
	public void setAttachments(List<Attachment> attachments) {

	}

	public String getDefaultURL() {
		if (defaultPage == null) {
			return "";
		}
		return "p?id=" + defaultPage;
	}

}
