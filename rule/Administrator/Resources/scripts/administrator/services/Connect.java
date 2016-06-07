package administrator.services;

import java.util.ArrayList;

import com.exponentus.dataengine.system.IEmployee;
import com.exponentus.dataengine.system.IExtUserDAO;
import com.exponentus.env.Environment;
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
			IExtUserDAO eDao = Environment.getExtUserDAO();
			if (user.getPwdHash() != null && user.getPwdHash().equals(pwdHash)) {
				user.setAuthorized(true);
				if (user.isSuperUser()) {
					IEmployee emp = eDao.getEmployee(user.getId());
					user = new SuperUser();
					if (emp != null) {
						user.setUserName(emp.getName());
					} else {
						user.setUserName(login);
					}
				}
			} else {
				Server.logger.errorLogEntry("password has not been encoded");
				user.setAuthorized(false);
			}

			if (user.isAuthorized()) {
				if (user.getId() != SuperUser.ID) {
					IEmployee emp = eDao.getEmployee(user.getId());
					if (emp != null) {
						user.setUserName(emp.getName());
						user.setRoles(emp.getAllRoles());
					} else {
						user.setUserName(user.getLogin());
						user.setRoles(new ArrayList<String>());
					}
				}
			}

		} else {
			Server.logger.warningLogEntry("\"" + login + "\" user not found");
		}

		return user;

	}
}
