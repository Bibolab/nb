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

import com.exponentus.appenv.AppEnv;
import com.exponentus.env.EnvConst;
import com.exponentus.env.SessionPool;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.user.IUser;
import com.exponentus.webserver.servlet.Cookies;

import administrator.services.Connect;
import kz.flabs.dataengine.DatabasePoolException;
import kz.flabs.exception.PortalException;
import kz.flabs.servlets.ProviderExceptionType;
import kz.flabs.servlets.PublishAsType;
import kz.flabs.users.AuthFailedException;
import kz.flabs.users.AuthFailedExceptionType;

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
	public Response createSession(Credentials authUser) throws ClassNotFoundException, InstantiationException, DatabasePoolException, UserException,
	        IllegalAccessException, SQLException, URISyntaxException {
		HttpSession jses;
		_Session ses = getSession();
		LanguageCode lang = ses.getLang();
		Cookies appCookies = new Cookies(request);
		String login = authUser.getLogin();
		try {
			IUser<Long> user = new Connect().getUser(login, authUser.getPwd());

			if (user != null && user.isAuthorized()) {
				jses = request.getSession(true);
				ses = new _Session(getAppEnv(), user);

				ses.setLang(LanguageCode.valueOf(appCookies.currentLang));

				AppEnv.logger.infoLogEntry(user.getUserID() + " has connected");

				jses.setAttribute(EnvConst.SESSION_ATTR, ses);
				int maxAge = -1;
				String token = SessionPool.put(ses);
				NewCookie cookie = new NewCookie(EnvConst.AUTH_COOKIE_NAME, token, "/", null, null, maxAge, false);
				return Response.status(HttpServletResponse.SC_OK).entity(authUser).cookie(cookie).build();
			} else {
				AppEnv.logger.infoLogEntry("Authorization failed, login or password is incorrect -");
				throw new AuthFailedException(AuthFailedExceptionType.PASSWORD_INCORRECT, login);
			}

		} catch (AuthFailedException e) {
			authUser.setError(AuthFailedExceptionType.PASSWORD_INCORRECT, lang);
			return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity(authUser).build();
		} catch (Exception e) {
			new PortalException(e, response, ProviderExceptionType.INTERNAL, PublishAsType.HTML);
		}

		return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity(authUser).build();
	}

}
