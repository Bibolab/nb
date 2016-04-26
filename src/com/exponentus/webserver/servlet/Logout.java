package com.exponentus.webserver.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.exponentus.appenv.AppEnv;
import com.exponentus.env.EnvConst;
import com.exponentus.env.SessionPool;
import com.exponentus.exception.PortalException;
import com.exponentus.scripting._Session;

public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private AppEnv env;

	@Override
	public void init(ServletConfig config) throws ServletException {
		ServletContext context = config.getServletContext();
		env = (AppEnv) context.getAttribute(EnvConst.APP_ATTR);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {

			if (env != null && env.isWorkspace) {
				Cookie wLoginCook = new Cookie(EnvConst.AUTH_COOKIE_NAME, "0");
				wLoginCook.setMaxAge(0);
				wLoginCook.setPath("/");
				response.addCookie(wLoginCook);
			}

			HttpSession jses = request.getSession(false);
			if (jses != null) {
				_Session ses = (_Session) jses.getAttribute(EnvConst.SESSION_ATTR);
				if (ses != null) {
					ses.getUser().setAuthorized(false);
					SessionPool.remove(ses);
				}
				jses.removeAttribute(EnvConst.SESSION_ATTR);
				jses.invalidate();
			}

			response.sendRedirect(getRedirect());
		} catch (Exception e) {
			new PortalException(e, env, response, ProviderExceptionType.LOGOUTERROR);
		}

	}

	private String getRedirect() {
		if (env != null) {

			return "/Workspace/p?id=workspace";

		} else {
			return "";
		}
	}

}
