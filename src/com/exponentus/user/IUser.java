package com.exponentus.user;

import java.util.Date;
import java.util.List;

import com.exponentus.dataengine.jpa.ISimpleAppEntity;
import com.exponentus.localization.LanguageCode;

import administrator.model.Application;

public interface IUser<K> extends ISimpleAppEntity<K> {

	String getPwdHash();

	String getPwd();

	String getLogin();

	boolean isAuthorized();

	void setAuthorized(boolean isAuthorized);

	String getUserID();

	String getUserName();

	void setUserName(String name);

	boolean isSuperUser();

	void setRoles(List<String> allRoles);

	List<String> getRoles();

	void setDefaultLang(LanguageCode defaultLang);

	LanguageCode getDefaultLang();

	List<Application> getAllowedApps();

	boolean isAllowed(String appName);

	void setEditable(boolean b);

	String getEmail();

	void setRegDate(Date date);

	void setLogin(String string);

	void setEmail(String value);

	void setPwd(String value);

	void setPwdHash(String pwdHash);

	public void setStatus(UserStatusCode status);

	public UserStatusCode getStatus();

}
