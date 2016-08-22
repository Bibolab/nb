package com.exponentus.extconnect;

import java.util.ArrayList;

import com.exponentus.server.Server;
import com.exponentus.user.IUser;
import com.exponentus.user.SuperUser;
import com.exponentus.util.StringUtil;

import administrator.dao.UserDAO;

public class Connect {

	public IUser<Long> getUser(String login, String pwd) {

		UserDAO uDao = new UserDAO();
		IUser<Long> user = uDao.findByLogin(login);

		if (user != null) {
			String pwdHash = StringUtil.encode(pwd);
			if (user.getPwdHash() != null && user.getPwdHash().equals(pwdHash)) {
				user.setAuthorized(true);
				if (user.isSuperUser()) {
					user = new SuperUser();
				}

				if (user.getUserName() == null) {
					user.setUserName(user.getLogin());
					user.setRoles(new ArrayList<String>());
				} else {
					user.setUserName(login);
				}
			} else {
				user.setAuthorized(false);
				return user;
			}

		} else {
			Server.logger.warningLogEntry("\"" + login + "\" user not found");
		}

		return user;

	}
}
