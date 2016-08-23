package com.exponentus.webserver.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.exception.TransformatorException;
import com.exponentus.localization.LanguageCode;
import com.exponentus.localization.Vocabulary;
import com.exponentus.scripting._Session;
import com.exponentus.server.Server;

import net.sf.saxon.s9api.SaxonApiException;

public class Error extends HttpServlet {
	private static final long serialVersionUID = 1207733369437122383L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		LanguageCode lang = LanguageCode.ENG;
		Vocabulary v = Environment.vocabulary;
		HttpSession jses = request.getSession(false);
		try {
			_Session ses = (_Session) jses.getAttribute(EnvConst.SESSION_ATTR);
			lang = ses.getLang();
		} catch (NullPointerException e) {

		}

		String type = request.getParameter("type");
		String msg = request.getParameter("msg");
		File errorXslt = Environment.getServerXSLT("error.xsl");

		try {
			request.setCharacterEncoding(EnvConst.SUPPOSED_CODE_PAGE);
			String outputContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?><request lang=\"" + lang + "\" >";

			if (type != null) {
				if (type.equals("ws_auth_error")) {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					errorXslt = Environment.getServerXSLT("authfailed.xsl");
					outputContent += "<error type=\"authfailed\"><message>" + v.getWord("authorization_was_failed", lang) + "</message>";
				} else if (type.equals("session_lost")) {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					errorXslt = Environment.getServerXSLT("authfailed.xsl");
					outputContent += "<error type=\"session_lost\"><message>" + v.getWord("session_was_lost", lang) + "</message>";
				} else if (type.equals("application_was_restricted")) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					msg = "work with the application was restricted";
					outputContent += "<message>" + msg + "</message>";
				} else {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					outputContent += "<error type=\"" + type + "\"><message>" + msg + "</message>";
				}
			} else {
				msg = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
				int statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
				type = (String) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE);
				Throwable exception = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

				Enumeration<?> attrs = request.getAttributeNames();
				while (attrs.hasMoreElements()) {
					// String name = (String) attrs.nextElement();
					// System.out.println(name + "=" +
					// request.getAttribute(name));
				}

				response.setStatus(statusCode);
				outputContent += "<error type=\"INTERNAL\"><code>" + statusCode + "</code><message>" + msg + "<errortext>" + exception
				        + "</errortext></message>";
			}

			outputContent += "<version>" + Server.serverVersion + "</version></error></request>";
			if (request.getParameter("as") != null) {
				response.setContentType("text/xml;charset=utf-8");
				PrintWriter out = response.getWriter();
				out.println(outputContent);
				out.close();
			} else {
				response.setContentType("text/html");

				new com.exponentus.webserver.servlet.xslt.SaxonTransformator().toTrans(response, errorXslt, outputContent);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SaxonApiException e) {
			e.printStackTrace();
		} catch (TransformatorException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		doPost(request, response);
	}

}
