package administrator.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.config.CacheIsolationType;

import com.exponentus.common.model.Attachment;
import com.exponentus.dataengine.system.IEmployee;
import com.exponentus.dataengine.system.IExtUserDAO;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting._Session;
import com.exponentus.user.IUser;
import com.exponentus.user.UserStatusCode;
import com.exponentus.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import administrator.model.constants.MessagingType;

@Entity
@Table(name = "_users")
@NamedQuery(name = "User.findAll", query = "SELECT m FROM User AS m ORDER BY m.regDate")
@Cache(isolation = CacheIsolationType.ISOLATED)
public class User implements IUser<Long>, IPOJOObject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	protected Long id;

	private UserStatusCode status = UserStatusCode.UNKNOWN;

	private MessagingType messagingType = MessagingType.EMAIL;

	@Column(name = "reg_date", nullable = false, updatable = false)
	protected Date regDate;

	@Transient
	private String userName;

	@Transient
	private List<String> roles;

	@Column(length = 64, unique = true)
	private String login;

	@Column(length = 64)
	private String email = "";

	@Column(length = 64)
	private String xmpp = "";

	@Column(length = 64)
	private String slack = "";

	@JsonIgnore
	private String pwd;

	@JsonIgnore
	private String pwdHash;

	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "_allowed_apps", joinColumns = @JoinColumn(name = "app_id", referencedColumnName = "id") , inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id") )
	private List<Application> allowedApps;

	private String theme;

	@Column(name = "default_lang")
	private LanguageCode defaultLang;

	@Column(name = "i_su")
	private boolean isSuperUser;

	@Transient
	private boolean isAuthorized;

	@JsonIgnore
	@Transient
	protected boolean isEditable;

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	@PrePersist
	private void prePersist() {
		regDate = new Date();
	}

	public Date getRegDate() {
		return regDate;
	}

	@Override
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	@Override
	public String getUserName() {
		if (userName == null) {
			IExtUserDAO eDao = Environment.getExtUserDAO();
			IEmployee emp = eDao.getEmployee(id);
			if (emp != null) {
				userName = emp.getName();
				if (roles == null) {
					roles = new ArrayList<String>();
				}
				roles.addAll(emp.getAllRoles());
			}
		}
		return userName;
	}

	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public List<String> getRoles() {
		return roles;
	}

	@Override
	public String getLogin() {
		return login;
	}

	@Override
	public void setLogin(String login) {
		this.login = login;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	public String getXmpp() {
		return xmpp;
	}

	public void setXmpp(String xmpp) {
		this.xmpp = xmpp;
	}

	public String getSlack() {
		return slack;
	}

	public void setSlack(String slack) {
		this.slack = slack;
	}

	@Override
	public String getPwd() {
		return pwd;
	}

	@Override
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@Override
	public String getPwdHash() {
		return pwdHash;
	}

	@Override
	public void setPwdHash(String pwdHash) {
		this.pwdHash = pwdHash;
	}

	@Override
	public List<Application> getAllowedApps() {
		return allowedApps;
	}

	// TODO it seems too slow
	@Override
	public boolean isAllowed(String appName) {
		for (Application a : allowedApps) {
			if (a.getName().equals(appName)) {
				return true;
			}
		}
		return false;
	}

	public void setAllowedApps(List<Application> allowedApps) {
		this.allowedApps = allowedApps;
	}

	@Override
	public void setStatus(UserStatusCode status) {
		this.status = status;
	}

	@Override
	public UserStatusCode getStatus() {
		return status;
	}

	public MessagingType getMessagingType() {
		return messagingType;
	}

	public void setMessagingType(MessagingType messagingType) {
		this.messagingType = messagingType;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	@Override
	public LanguageCode getDefaultLang() {
		if (defaultLang == null) {
			defaultLang = LanguageCode.valueOf(EnvConst.DEFAULT_LANG);
		}
		return defaultLang;
	}

	@Override
	public void setDefaultLang(LanguageCode defaultLang) {
		this.defaultLang = defaultLang;
	}

	@Override
	public boolean isSuperUser() {
		return isSuperUser;
	}

	public void setSuperUser(boolean isSuperUser) {
		this.isSuperUser = isSuperUser;
	}

	@Override
	public boolean isAuthorized() {
		return isAuthorized;
	}

	@Override
	public void setAuthorized(boolean isAuthorized) {
		this.isAuthorized = isAuthorized;
	}

	@Override
	public boolean isEditable() {
		return isEditable;
	}

	@Override
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	@Override
	public String getUserID() {
		return login;
	}

	@Override
	public String getURL() {
		return "p?id=user-form&amp;docid=" + getId();
	}

	@Override
	public String getFullXMLChunk(_Session ses) {
		StringBuilder chunk = new StringBuilder(1000);
		chunk.append("<regdate>" + TimeUtil.dateTimeToStringSilently(regDate) + "</regdate>");
		chunk.append("<status>" + status + "</status>");
		chunk.append("<login>" + login + "</login>");
		chunk.append("<defaultlang>" + defaultLang + "</defaultlang>");
		chunk.append("<email>" + email + "</email>");
		chunk.append("<xmpp>" + xmpp + "</xmpp>");
		chunk.append("<slack>" + slack + "</slack>");
		chunk.append("<issuperuser>" + isSuperUser + "</issuperuser>");
		try {
			String asText = "";
			for (Application a : allowedApps) {
				asText += "<entry name=\"" + a.getName() + "\" id=\"" + a.getId() + "\">" + a.getLocalizedName().get(ses.getLang()) + "</entry>";
			}
			chunk.append("<apps>" + asText + "</apps>");
		} catch (NullPointerException e) {
			chunk.append("<apps></apps>");
		}
		return chunk.toString();
	}

	@Override
	public String getShortXMLChunk(_Session ses) {
		StringBuilder chunk = new StringBuilder(1000);
		chunk.append("<regdate>" + TimeUtil.dateTimeToStringSilently(regDate) + "</regdate>");
		chunk.append("<login>" + login + "</login>");
		chunk.append("<defaultlang>" + defaultLang + "</defaultlang>");
		chunk.append("<email>" + email + "</email>");
		chunk.append("<issuperuser>" + isSuperUser + "</issuperuser>");
		return chunk.toString();
	}

	@Override
	public Object getJSONObj(_Session ses) {
		return this;
	}

	@Override
	public String getIdentifier() {
		Long id = getId();
		if (id == null) {
			return "null";
		} else {
			return getId().toString();
		}
	}

	@Override
	public void setRoles(List<String> allRoles) {
		roles = allRoles;

	}

	@Override
	public boolean isWasRead() {
		return true;
	}

	@Override
	public String toString() {
		return "id=" + id + ", login=" + login;
	}

	@Override
	public List<Attachment> getAttachments() {
		return new ArrayList<Attachment>();
	}

	@Override
	public void setAttachments(List<Attachment> attachments) {

	}

	@Override
	public String getEntityKind() {
		return "user";
	}

}
