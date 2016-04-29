package com.exponentus.user;

import java.util.List;
import java.util.Set;

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

	LanguageCode getDefaultLang();

	List<Application> getAllowedApps();

	Set<String> getApps();

}
