package com.exponentus.webserver.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.exponentus.appenv.AppEnv;
import com.exponentus.env.AuthMethodType;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.env.ServletSessionPool;
import com.exponentus.env.SessionPool;
import com.exponentus.exception.AuthFailedException;
import com.exponentus.exception.AuthFailedExceptionType;
import com.exponentus.exception.PortalException;
import com.exponentus.extconnect.Connect;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.server.Server;
import com.exponentus.user.AuthModeType;
import com.exponentus.user.IUser;

public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private AppEnv env;

	@Override
	public void init(ServletConfig config) throws ServletException {
		ServletContext context = config.getServletContext();
		env = (AppEnv) context.getAttribute(EnvConst.APP_ATTR);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		_Session ses = null;
		try {
			String login = request.getParameter("login");
			String pwd = request.getParameter("pwd");
			HttpSession jses;

			Cookies appCookies = new Cookies(request);

			IUser<Long> user = new Connect().getUser(login.trim(), pwd);

			if (user != null && user.isAuthorized()) {
				jses = ServletSessionPool.get(request);
				ses = new _Session(env, user);
				ses.setAuthMode(AuthModeType.DIRECT_LOGIN);
				ses.setJsesId(jses.getId());
				String token = SessionPool.put(ses);
				ses.setLang(LanguageCode.valueOf(appCookies.currentLang));

				AppEnv.logger.infoLogEntry(user.getUserID() + " has connected");

				String redirect = "";

				jses.setAttribute(EnvConst.SESSION_ATTR, ses);
				Cookie authCookie = new Cookie(EnvConst.AUTH_COOKIE_NAME, token);
				authCookie.setMaxAge(-1);
				authCookie.setPath("/");
				response.addCookie(authCookie);

				SessionCooks cooks = new SessionCooks(request, response);
				if (cooks.refferer == null) {
					redirect = getRedirect();
				} else {
					redirect = cooks.refferer;
					Cookie cpCookie = new Cookie(EnvConst.CALLING_PAGE_COOKIE_NAME, "0");
					cpCookie.setMaxAge(0);
					cpCookie.setPath("/");
					response.addCookie(cpCookie);
				}

				if (Environment.monitoringEnable) {
					Environment.getMonitoringDAO().postLogin(user.getId());
				}

				response.sendRedirect(redirect);

			} else {
				throw new AuthFailedException(AuthFailedExceptionType.PASSWORD_INCORRECT, login);
			}
		} catch (AuthFailedException e) {
			// e.printStackTrace();
			Server.logger.warningLogEntry(e.toString());
			try {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				RequestDispatcher d = request.getRequestDispatcher("e?type=ws_auth_error");
				d.forward(request, response);
			} catch (IOException e1) {
				Server.logger.errorLogEntry(e1);
			} catch (ServletException e2) {
				Server.logger.errorLogEntry(e2);
			}
		} catch (IOException ioe) {
			Server.logger.errorLogEntry(ioe);
		} catch (IllegalStateException ise) {
			Server.logger.errorLogEntry(ise);
		} catch (Exception e) {
			new PortalException(e, response, ProviderExceptionType.INTERNAL, PublishAsType.HTML);
		}
	}

	public static String getRedirect() {
		if (Environment.authMethod == AuthMethodType.WORKSPACE_LOGIN_PAGE) {
			return "/" + EnvConst.WORKSPACE_NAME + "/p?id=" + Environment.getAppEnv(EnvConst.WORKSPACE_NAME).getDefaultPage();
		} else {
			return "/" + EnvConst.ADMINISTRATOR_APP_NAME + "/p?id=login";
		}
	}

}
