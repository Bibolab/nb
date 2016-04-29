package com.exponentus.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import administrator.dao.ApplicationDAO;
import administrator.model.Application;

/**
 * @author Kayra on 17/03/16.
 */

public class SuperUser extends SystemUser {
	public String USER_NAME = "supervisor";
	public final static long ID = -1;

	public SuperUser(String login) {
		USER_NAME = login;
	}

	public SuperUser() {

	}

	@Override
	public String getUserID() {
		return USER_NAME;
	}

	@Override
	public String getUserName() {
		return USER_NAME;
	}

	@Override
	public Long getId() {
		return (long) ID;
	}

	@Override
	public String getLogin() {
		return USER_NAME;
	}

	@Override
	public Set<String> getApps() {
		Set<String> allowed = new HashSet<String>();
		for (Application a : getAllowedApps()) {
			allowed.add(a.getName());
		}
		return allowed;
	}

	@Override
	public List<Application> getAllowedApps() {
		return new ApplicationDAO().findAll();
	}

	@Override
	public boolean isSuperUser() {
		return true;
	}

}
