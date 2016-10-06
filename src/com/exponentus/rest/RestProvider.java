package com.exponentus.rest;

import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.exponentus.appenv.AppEnv;
import com.exponentus.dataengine.IDatabase;
import com.exponentus.dataengine.IFTIndexEngine;
import com.exponentus.dataengine.jpa.AppEntity;
import com.exponentus.dataengine.jpa.ViewPage;
import com.exponentus.env.EnvConst;
import com.exponentus.exception.RuleException;
import com.exponentus.localization.LanguageCode;
import com.exponentus.rest.pojo.Outcome;
import com.exponentus.rest.pojo.SearchViewRequest;
import com.exponentus.rest.pojo.ViewRequest;
import com.exponentus.rest.pojo.constants.RequestType;
import com.exponentus.rule.page.PageRule;
import com.exponentus.runtimeobj.Page;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scriptprocessor.page.PageOutcome;
import com.exponentus.server.Server;

@Path("/")
public class RestProvider implements IRestService {
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

	@POST
	@Path("/view")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchService(ViewRequest r) {
		Outcome outcome = new Outcome();
		_Session ses = getSession();
		LanguageCode lang = ses.getLang();
		if (r.getType() == RequestType.FT_SEARCH) {
			SearchViewRequest svr = (SearchViewRequest) r;
			if (!svr.getKeyword().isEmpty()) {
				int pageSize = ses.pageSize;

				IDatabase db = ses.getDatabase();
				IFTIndexEngine ftEngine = db.getFTSearchEngine();
				ViewPage<?> result = ftEngine.search(svr.getKeyword(), ses, svr.getPageNum(), pageSize);

				if (result != null) {
					@SuppressWarnings("unchecked")
					ViewPage<AppEntity<UUID>> res = (ViewPage<AppEntity<UUID>>) result;
					outcome.setPayload(res);
					return Response.status(HttpServletResponse.SC_OK).entity(outcome).build();
				} else {
					outcome.setMessage("ft_search_return_null", lang);
				}
			} else {
				outcome.setMessage("ft_search_keyword_is_empty", lang);
			}
		}
		return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(outcome).build();

	}

	@GET
	@Path("/page/{id}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON })
	public Response producePage(@PathParam("id") String id, @Context UriInfo uriInfo) {
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
			return Response.status(HttpServletResponse.SC_OK).entity(result.getJSON()).build();
		} catch (final ClassNotFoundException e) {
			Server.logger.errorLogEntry(e);
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
		} catch (RuleException e) {
			Server.logger.errorLogEntry(e);
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
		}

	}

	@Override
	public ServiceDescriptor updateDescription(ServiceDescriptor sd) {
		return sd;
	}

}
