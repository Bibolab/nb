package com.exponentus.webserver.valve;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import com.exponentus.appenv.AppEnv;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.env.ServletSessionPool;
import com.exponentus.env.SessionPool;
import com.exponentus.exception.AuthFailedException;
import com.exponentus.exception.AuthFailedExceptionType;
import com.exponentus.scripting._Session;
import com.exponentus.server.Server;
import com.exponentus.user.AnonymousUser;
import com.exponentus.user.IUser;
import com.exponentus.webserver.servlet.SessionCooks;

public class Secure extends ValveBase {
	String appType;
	String referer;

	public void invoke(Request request, Response response, String appType, String referer) throws IOException, ServletException {
		this.appType = appType;
		this.referer = referer;
		invoke(request, response);
	}

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		HttpServletRequest http = request;

		if (!appType.equalsIgnoreCase("")) {
			HttpSession jses = http.getSession(false);
			if (jses != null) {
				_Session ses = (_Session) jses.getAttribute(EnvConst.SESSION_ATTR);
				if (ses != null) {
					IUser<Long> user = ses.getUser();
					if (!user.getUserID().equals(AnonymousUser.USER_NAME)) {
						if (user.isAllowed(appType)) {
							getNext().invoke(request, response);
						} else {
							Server.logger.warningLogEntry("work with the application was restricted");
							if (jses != null) {
								jses.invalidate();
							}
							response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							request.getRequestDispatcher("/Error?type=application_was_restricted").forward(request, response);
						}
					} else {
						gettingSession(request, response);
					}
				} else {
					gettingSession(request, response);
				}
			} else {
				gettingSession(request, response);
			}
		} else {
			getNext().invoke(request, response);
		}

	}

	private void gettingSession(Request request, Response response) throws IOException, ServletException {
		HttpServletRequest http = request;
		Token token = getToken(request, response);

		if (token.getValue() != null) {
			_Session ses = SessionPool.getLoggeedUser(token.getValue());
			// String token2 = "";
			if (ses != null) {
				RequestURL ru = new RequestURL(http.getRequestURI());
				AppEnv env = Environment.getAppEnv(ru.getAppType());
				_Session clonedSes = ses.clone(env);
				HttpSession jses = ServletSessionPool.get(request);
				jses.setAttribute(EnvConst.SESSION_ATTR, clonedSes);
				Server.logger.debugLogEntry(ses.getUser().getUserID() + "\" got from session pool " + jses.getServletContext().getContextPath());
				invoke(request, response);
			} else {
				Server.logger.warningLogEntry("there is no associated user session for the token");
				new AuthFailedException(AuthFailedExceptionType.NO_ASSOCIATED_SESSION_FOR_THE_TOKEN, appType);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				HttpSession jses = ServletSessionPool.get(request);
				jses.setAttribute("callingPage", referer);
				request.getRequestDispatcher("/Error?type=ws_auth_error").forward(request, response);
			}
			if (token.isLimitedToken()) {
				// SessionPool.remove(token.getValue());
				// SessionPool.remove(token2);
			}
		} else {
			Server.logger.warningLogEntry("user session was expired");
			HttpSession jses = request.getSession(false);
			if (jses != null) {
				jses.invalidate();
			}
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			request.getRequestDispatcher("/Error?type=session_lost").forward(request, response);
		}
	}

	private Token getToken(HttpServletRequest request, HttpServletResponse response) {
		Token t = new Token();
		SessionCooks appCookies = new SessionCooks(request, response);
		String token = appCookies.auth;
		if (token == null) {
			token = request.getParameter("t");
			t.setLimitedToken(true);
		}
		t.setValue(token);
		return t;

	}

}
