package com.exponentus.rest;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.exponentus.common.dao.ReadingMarkDAO;
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

}
