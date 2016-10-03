package com.exponentus.rest;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.exponentus.common.dao.ReadingMarkDAO;
import com.exponentus.dataengine.IDatabase;
import com.exponentus.dataengine.IFTIndexEngine;
import com.exponentus.dataengine.jpa.AppEntity;
import com.exponentus.dataengine.jpa.ViewPage;
import com.exponentus.localization.LanguageCode;
import com.exponentus.rest.pojo.Outcome;
import com.exponentus.rest.pojo.ServerServiceExceptionType;
import com.exponentus.scripting._Session;

@Path("/application")
public class ApplicationService extends RestProvider {

	@POST
	@Path("/service/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response commonService(@PathParam("type") String type, MultivaluedMap<String, String> formParams) {
		Outcome outcome = new Outcome();
		_Session ses = getSession();
		if (type.equalsIgnoreCase("markasread")) {
			List<String> id = formParams.get("id");
			ReadingMarkDAO rmDao = new ReadingMarkDAO();
			try {
				UUID uuidId = UUID.fromString(id.get(0));
				rmDao.markAsRead(uuidId, ses.getUser());
			} catch (IllegalArgumentException e) {
				outcome.setMessage(e, ServerServiceExceptionType.SERVER_ERROR, ses.getLang());
				return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(outcome).build();
			}
		} else if (type.equalsIgnoreCase("isread")) {

		}
		return Response.status(HttpServletResponse.SC_OK).build();

	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchService(@DefaultValue("") @QueryParam("keyword") String keyWord, @DefaultValue("1") @QueryParam("page") int pageNum) {
		Outcome outcome = new Outcome();
		_Session ses = getSession();
		LanguageCode lang = ses.getLang();
		if (!keyWord.isEmpty()) {
			int pageSize = ses.pageSize;

			IDatabase db = ses.getDatabase();
			IFTIndexEngine ftEngine = db.getFTSearchEngine();
			ViewPage<?> result = ftEngine.search(keyWord, ses, pageNum, pageSize);

			if (result != null) {
				@SuppressWarnings("unchecked")
				ViewPage<AppEntity<UUID>> res = (ViewPage<AppEntity<UUID>>) result;
				return Response.status(HttpServletResponse.SC_OK).entity(res).build();
			} else {
				outcome.setMessage("ft_search_return_null", lang);
			}
		} else {
			outcome.setMessage("ft_search_keyword_is_empty", lang);
		}
		outcome.addMessage(keyWord);
		return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(outcome).build();

	}

}
