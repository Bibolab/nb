package com.exponentus.webserver.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exponentus.env.EnvConst;

public class Redirector extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {

		String rUrl = EnvConst.DEFAULT_APPLICATION;
		RequestDispatcher rd = request.getRequestDispatcher(rUrl);
		try {

			// rd.forward(request, response);
			response.sendRedirect(rUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY); // or
		// SC_FOUND
		// response.setHeader("Location", EnvConst.DEFAULT_APPLICATION);
	}
}
