package com.exponentus.webserver.valve;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import com.exponentus.env.Environment;
import com.exponentus.log.Log4jLogger;
import com.exponentus.util.NetUtil;

public class Logging extends ValveBase {
	private Log4jLogger logger;

	public Logging() {
		super();
		logger = new Log4jLogger(getClass().getSimpleName());
	}

	@SuppressWarnings("unused")
	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		HttpServletRequest http = request;
		String requestURI = http.getRequestURI();
		String params = http.getQueryString();

		if (params != null) {
			requestURI = requestURI + "?" + http.getQueryString();
		}

		RequestURL ru = new RequestURL(requestURI);

		// Server.logger.normalLogEntry(ru.getUrl() + " ---- ispage=" +
		// ru.isPage() + ", isprotected=" + ru.isProtected() + ", isdeafult=" +
		// ru.isDefault() + ", isauth=" + ru.isAuthRequest());

		String clientIpAddress = request.getHeader("X-FORWARDED-FOR");

		if (clientIpAddress == null) {
			clientIpAddress = request.getRemoteAddr();
		}

		ru.setIp(clientIpAddress);
		ru.setAgent(request.getHeader("user-agent"));

		logger.infoLogEntry(clientIpAddress + " " + NetUtil.getCountry(clientIpAddress) + " " + ru.toString() + "," + ru.getAgent());
		// com.flabser.server.Server.logger.infoLogEntry(clientIpAddress + " " +
		// ru.toString() + ", apptype="
		// + ru.getAppType() + ", servername=" + request.getServerName());

		if (Environment.isDevMode() && false) {
			System.out.println("-----------request -----------");
			System.out.println("Request: " + ru.getUrl());
			System.out.println("From: " + clientIpAddress);
			System.out.println("Agent: " + ru.getAgent());
			System.out.println("-");
			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String key = headerNames.nextElement();
				String value = request.getHeader(key);
				System.out.println(key + "=" + value);
			}
			System.out.println("----------------------------");
		}

		((Unsecure) getNext()).invoke(request, response, ru);
		return;
	}

}
