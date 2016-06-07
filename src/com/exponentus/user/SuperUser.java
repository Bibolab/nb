package com.exponentus.user;

import java.util.List;

import administrator.dao.ApplicationDAO;
import administrator.model.Application;

/**
 * @author Kayra on 17/03/16.
 */

public class SuperUser extends SystemUser {
	public static String USER_NAME = "supervisor";
	public final static long ID = -1;

	@Override
	public String getUserID() {
		return USER_NAME;
	}

	@Override
	public void setUserName(String name) {
		USER_NAME = name;
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
	public boolean isAllowed(String app) {
		return true;
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
