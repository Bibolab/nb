package com.exponentus.webserver.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exponentus.server.Server;

public class Lang extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String servletType = request.getServletPath();
		String lang = servletType.substring(1, servletType.length());
		RequestDispatcher rd = request.getRequestDispatcher("p?lang=" + lang);
		try {
			rd.forward(request, response);
		} catch (IOException | ServletException e) {
			Server.logger.errorLogEntry(e);
		}
	}

}
