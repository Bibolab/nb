package com.exponentus.webserver.servlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exponentus.env.EnvConst;

public class SessionCooks {

	public String auth;
	public String refferer;
	private String currentLang;

	public SessionCooks(HttpServletRequest request, HttpServletResponse response) {
		try {
			Cookie[] cooks = request.getCookies();
			if (cooks != null) {
				for (int i = 0; i < cooks.length; i++) {
					if (cooks[i].getName().equals(EnvConst.LANG_COOKIE_NAME)) {
						currentLang = cooks[i].getValue();
					} else if (cooks[i].getName().equals(EnvConst.AUTH_COOKIE_NAME)) {
						auth = cooks[i].getValue();
					} else if (cooks[i].getName().equals(EnvConst.CALLING_PAGE_COOKIE_NAME)) {
						refferer = cooks[i].getValue();
					}
				}
			}
		} catch (Exception e) {

		}
	}

	public String getCurrentLang() {
		if (currentLang == null) {
			return EnvConst.DEFAULT_LANG;
		} else {
			return currentLang;
		}
	}

	@Override
	public String toString() {
		return "lang=" + currentLang;
	}

}
