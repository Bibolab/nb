package com.exponentus.webserver.valve;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.http.HttpStatus;

import com.exponentus.appenv.AppEnv;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.env.ServletSessionPool;
import com.exponentus.exception.ApplicationException;
import com.exponentus.exception.RuleException;
import com.exponentus.localization.LanguageCode;
import com.exponentus.rest.ResourceLoader;
import com.exponentus.scripting._Session;
import com.exponentus.server.Server;
import com.exponentus.user.AnonymousUser;
import com.exponentus.webserver.servlet.SessionCooks;

import administrator.dao.LanguageDAO;

public class Unsecure extends ValveBase {
	private RequestURL ru;

	public void invoke(Request request, Response response, RequestURL ru) throws IOException, ServletException {
		this.ru = ru;
		invoke(request, response);
	}

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		String appType = ru.getAppType();
		if (ru.isProtected()) {
			AppEnv env = Environment.getAppEnv(appType);
			if (env != null) {
				if (ru.isAuthRequest()) {
					if (request.getMethod().equalsIgnoreCase("POST") || !ru.isLogout()) {
						HttpSession jses = ServletSessionPool.get(request);
						jses.setAttribute(EnvConst.SESSION_ATTR, new _Session(env, new AnonymousUser()));

					}
					getNext().getNext().invoke(request, response);
				} else {
					if (ru.isRest()) {
						if (ResourceLoader.getUnsecureMethods().contains(ru.getUrl())) {
							gettingSession(request, response, env);
							getNext().getNext().invoke(request, response);
						} else {
							((Secure) getNext()).invoke(request, response, ru);
						}
					} else if (ru.isPage()) {
						try {
							String pageId = ru.getPageID();
							if (pageId.isEmpty()) {
								pageId = env.getDefaultPage();
							}
							if (env.ruleProvider.getRule(pageId).isAnonymousAccessAllowed()) {
								gettingSession(request, response, env);
								getNext().getNext().invoke(request, response);
							} else {
								((Secure) getNext()).invoke(request, response, ru);
							}

						} catch (RuleException e) {
							Server.logger.errorLogEntry(e.getMessage());
							ApplicationException ae = new ApplicationException(appType, e.getMessage(),
							        new _Session(env, new AnonymousUser()).getLang());
							response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
							response.getWriter().println(ae.getHTMLMessage());
						}
					} else if (ru.isProtected()) {
						((Secure) getNext()).invoke(request, response, ru);
					} else {
						gettingSession(request, response, env);
						getNext().getNext().invoke(request, response);
					}
				}
			} else {
				if (appType.equals("favicon")) {
					getNext().getNext().invoke(request, response);
				} else {
					String val = appType.trim();
					// String val = request.getServletPath().substring(1,
					// request.getServletPath().length());
					if (EnvConst.WELCOME_APPLICATION.isEmpty()) {
						if (val.equals("") || val.equals("p") || val.equals("е")) {
							getNext().getNext().invoke(request, response);
						} else {
							String msg = "unknown application type \"" + appType + "\"";
							Server.logger.warningLogEntry(msg);
							ApplicationException ae = new ApplicationException(val, msg, LanguageCode.valueOf(EnvConst.DEFAULT_LANG));
							response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
							response.getWriter().println(ae.getHTMLMessage());
						}
					} else {
						String req = request.getServletPath();
						if (val.equals("") || val.equals("p") || new LanguageDAO().findAll().stream()
						        .filter(o -> o.getCode().name().equalsIgnoreCase(req.substring(1))).findFirst().isPresent()) {

							gettingSession(request, response, Environment.getAppEnv(EnvConst.WELCOME_APPLICATION));
							getNext().getNext().invoke(request, response);
						} else if (req.contains("/rest")) { // TODO it is hard
						                                    // coding !!
							gettingSession(request, response, Environment.getAppEnv(EnvConst.WELCOME_APPLICATION));
							getNext().getNext().invoke(request, response);
						} else {
							String msg = "unknown request \"" + req + "\"";
							Server.logger.warningLogEntry(msg);
							ApplicationException ae = new ApplicationException(req, msg, EnvConst.getDefaultLang());
							response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
							response.getWriter().println(ae.getHTMLMessage());
						}
					}
				}
			}
		} else {
			getNext().getNext().invoke(request, response);
		}
	}

	private void gettingSession(Request request, Response response, AppEnv env) {
		HttpSession jses = request.getSession(false);
		if (jses == null) {
			jses = request.getSession(true);
			jses.setAttribute(EnvConst.SESSION_ATTR, getAnonymousSes(request, response, jses, env));
		} else {
			_Session us = (_Session) jses.getAttribute(EnvConst.SESSION_ATTR);
			if (us == null) {
				jses.setAttribute(EnvConst.SESSION_ATTR, getAnonymousSes(request, response, jses, env));
			}
		}
	}

	private _Session getAnonymousSes(Request request, Response response, HttpSession jses, AppEnv env) {
		SessionCooks cooks = new SessionCooks(request, response);
		_Session ses = new _Session(env, new AnonymousUser());
		ses.setLang(LanguageCode.valueOf(cooks.getCurrentLang()));
		return ses;
	}

}
