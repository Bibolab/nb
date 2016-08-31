package com.exponentus.rest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.exponentus.appenv.AppEnv;
import com.exponentus.env.EnvConst;
import com.exponentus.exception.RuleException;
import com.exponentus.rule.page.PageRule;
import com.exponentus.runtimeobj.Page;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scriptprocessor.page.PageOutcome;

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

	@GET
	@Path("/page/{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON })
	public Response producePage(@PathParam("id") String id, @Context UriInfo uriInfo)
	        throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		AppEnv env = getAppEnv();
		_Session ses = getSession();
		PageOutcome result = new PageOutcome();

		try {
			PageRule rule = env.ruleProvider.getRule(id);
			String referrer = request.getHeader("referer");
			_WebFormData formData = new _WebFormData(queryParams, referrer);
			Page page = new Page(env, ses, rule);
			result = page.getPageContent(result, formData, request.getMethod());

		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		} catch (RuleException e) {
			e.printStackTrace();
		}

		return Response.status(HttpServletResponse.SC_OK).entity(result.getJSON()).build();
	}

}
