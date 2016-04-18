package com.exponentus.rest;

import java.net.URISyntaxException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.omg.CORBA.UserException;

import com.exponentus.env.EnvConst;
import com.exponentus.env.SessionPool;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.server.Server;
import com.exponentus.user.IUser;

import administrator.model.User;
import administrator.services.Connect;
import kz.flabs.dataengine.DatabasePoolException;

@Path("/session")
public class SessionService extends RestProvider {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCurrentSession() {
		HttpSession jses = request.getSession(false);
		_Session userSession = (_Session) jses.getAttribute(EnvConst.SESSION_ATTR);
		return Response.status(HttpServletResponse.SC_OK).entity(userSession.getUser()).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createSession(User authUser) throws ClassNotFoundException, InstantiationException, DatabasePoolException, UserException,
	        IllegalAccessException, SQLException, URISyntaxException {
		_Session session = getSession();
		LanguageCode lang = session.getLang();
		String login = authUser.getLogin();
		IUser<Long> user = new Connect().getUser(login, authUser.getPwd());
		authUser.setPwd(null);
		if (!user.isAuthorized()) {
			Server.logger.warningLogEntry("signin of " + login + " was failed");
			// authUser.setError(AuthFailedExceptionType.PASSWORD_OR_LOGIN_INCORRECT,
			// lang);
			return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity(authUser).build();
		}

		String userID = user.getLogin();
		HttpSession jses = request.getSession(true);

		Server.logger.infoLogEntry(userID + " has connected");
		/*
		 * session.setUser(user); if (user.getStatus() ==
		 * UserStatusType.REGISTERED) { authUser = session.getAppUser(); //
		 * authUser.setAppId(appID); } else if (user.getStatus() ==
		 * UserStatusType.WAITING_FIRST_ENTERING) {
		 * authUser.setRedirect("tochangepwd"); } else if (user.getStatus() ==
		 * UserStatusType.NOT_VERIFIED) {
		 * authUser.setError(AuthFailedExceptionType.INCOMPLETE_REGISTRATION,
		 * lang); return
		 * Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity(authUser)
		 * .build(); } else if (user.getStatus() ==
		 * UserStatusType.WAITING_FOR_VERIFYCODE) {
		 * authUser.setError(AuthFailedExceptionType.INCOMPLETE_REGISTRATION,
		 * lang); return
		 * Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity(authUser)
		 * .build(); } else if (user.getStatus() ==
		 * UserStatusType.USER_WAS_DELETED) {
		 * authUser.setError(AuthFailedExceptionType.USER_WAS_DELETED, lang);
		 * return
		 * Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity(authUser)
		 * .build(); } else {
		 * authUser.setError(AuthFailedExceptionType.UNKNOWN_STATUS, lang);
		 * return
		 * Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity(authUser)
		 * .build(); }
		 */
		String token = SessionPool.put(session);
		jses.setAttribute(EnvConst.SESSION_ATTR, session);
		int maxAge = -1;

		NewCookie cookie = new NewCookie(EnvConst.AUTH_COOKIE_NAME, token, "/", null, null, maxAge, false);
		return Response.status(HttpServletResponse.SC_OK).entity(authUser).cookie(cookie).build();
	}

}
