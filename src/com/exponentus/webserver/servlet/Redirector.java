package com.exponentus.webserver.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exponentus.env.EnvConst;
import com.exponentus.server.Server;

public class Redirector extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {

		String rUrl = EnvConst.DEFAULT_APPLICATION;
		try {
			response.sendRedirect(rUrl);
		} catch (IOException e) {
			Server.logger.errorLogEntry(e);
		}
	}
}
