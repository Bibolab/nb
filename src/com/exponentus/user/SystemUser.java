package com.exponentus.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.exponentus.localization.LanguageCode;

import administrator.model.Application;

/**
 * @author Kayra on 17/03/16.
 */

public abstract class SystemUser implements IUser<Long> {

	@Override
	public String getPwdHash() {
		return null;
	}

	@Override
	public String getPwd() {
		return null;
	}

	@Override
	public boolean isAuthorized() {
		return true;
	}

	@Override
	public void setAuthorized(boolean isAuthorized) {

	}

	@Override
	public abstract String getUserID();

	@Override
	public abstract String getUserName();

	@Override
	public void setUserName(String name) {

	}

	@Override
	public void setId(Long id) {

	}

	@Override
	public boolean isSuperUser() {
		return false;
	}

	@Override
	public boolean isAllowed(String app) {
		return false;
	}

	@Override
	public List<Application> getAllowedApps() {
		return new ArrayList<Application>();
	}

	@Override
	public void setRoles(List<String> allRoles) {

	}

	@Override
	public List<String> getRoles() {
		return new ArrayList<String>();
	}

	@Override
	public void setDefaultLang(LanguageCode defaultLang) {

	}

	@Override
	public LanguageCode getDefaultLang() {
		return LanguageCode.ENG;
	}

	@Override
	public abstract Long getId();

	@Override
	public abstract String getLogin();

	@Override
	public void setEditable(boolean b) {

	}

	@Override
	public String getEmail() {
		return null;
	}

	@Override
	public void setRegDate(Date date) {

	}

	@Override
	public void setLogin(String string) {

	}

	@Override
	public void setEmail(String value) {

	}

	@Override
	public void setPwd(String value) {

	}

	@Override
	public void setPwdHash(String pwdHash) {

	}

	@Override
	public void setStatus(UserStatusCode status) {

	}

	@Override
	public UserStatusCode getStatus() {
		return UserStatusCode.REGISTERED;
	}
}
