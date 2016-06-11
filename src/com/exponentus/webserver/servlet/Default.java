package com.exponentus.webserver.servlet;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlets.DefaultServlet;

public class Default extends DefaultServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_FORBIDDEN);
		return;
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_FORBIDDEN);
		return;
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_FORBIDDEN);
		return;
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		StringBuilder allow = new StringBuilder();
		allow.append("GET");
		allow.append(", OPTIONS");
		resp.setHeader("Allow", allow.toString());
	}

	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		boolean serveContent = DispatcherType.INCLUDE.equals(request.getDispatcherType());
		serveResource(request, response, serveContent, null);
	}

}
