package com.exponentus.user;

public class UndefinedUser extends SystemUser {
	public final static String USER_NAME = "undefined";
	public final static long ID = -999;

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

}
