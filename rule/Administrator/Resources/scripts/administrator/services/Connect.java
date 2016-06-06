package administrator.services;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.exponentus.appenv.AppEnv;
import com.exponentus.dataengine.system.IEmployee;
import com.exponentus.dataengine.system.IExtUserDAO;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.scripting._Session;
import com.exponentus.server.Server;
import com.exponentus.user.AnonymousUser;
import com.exponentus.user.IUser;
import com.exponentus.user.SuperUser;
import com.exponentus.util.StringUtil;

import administrator.dao.UserDAO;

public class Connect {
	private AppEnv env = Environment.getAppEnv(EnvConst.ADMINISTRATOR_APP_NAME);

	public IUser<Long> getUser(String login, String pwd) {

		UserDAO uDao = new UserDAO();
		IUser<Long> user = uDao.findByLogin(login);

		if (user != null) {
			String pwdHash = StringUtil.encode(pwd);
			if (user.getPwdHash() != null && user.getPwdHash().equals(pwdHash)) {
				user.setAuthorized(true);
				if (user.isSuperUser()) {
					user = new SuperUser(user.getLogin());
				}
			} else {
				Server.logger.errorLogEntry("password has not been encoded");
				user.setAuthorized(false);
			}

			if (user.isAuthorized()) {
				IExtUserDAO eDao = null;
				try {
					Class<?> clazz = Class.forName(EnvConst.STAFF_DAO_CLASS);
					Class[] args = new Class[] { _Session.class };
					Constructor<?> contructor = clazz.getConstructor(args);
					_Session ses = new _Session(env, new AnonymousUser());
					eDao = (IExtUserDAO) contructor.newInstance(new Object[] { ses });
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
				} catch (ClassNotFoundException e) {

				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				        | NoSuchMethodException | SecurityException e) {
					Server.logger.errorLogEntry(e);
				}

			}

		} else {
			Server.logger.warningLogEntry("\"" + login + "\" user not found");
		}

		return user;

	}
}
