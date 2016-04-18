package com.exponentus.rest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import com.exponentus.appenv.AppEnv;
import com.exponentus.env.EnvConst;
import com.exponentus.scripting._Session;

@Path("/")
public class RestProvider {

	@Context
	protected ServletContext context;
	@Context
	public HttpServletRequest request;
	@Context
	protected HttpServletResponse response;

	public AppEnv getAppEnv() {
		return (AppEnv) context.getAttribute(EnvConst.APP_ATTR);

	}

	public String getAppID() {
		return (String) request.getAttribute("appid");

	}

	public _Session getSession() {
		HttpSession jses = request.getSession(false);
		_Session us = (_Session) jses.getAttribute(EnvConst.SESSION_ATTR);
		return us;
	}

}
