package com.exponentus.env;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ServletSessionPool {
	private static HashMap<String, HttpSession> sessions = new HashMap<String, HttpSession>();

	public static HttpSession get(HttpServletRequest request) {
		HttpSession jses = request.getSession(true);
		sessions.put(jses.getId(), jses);
		return jses;
	}

	public static HashMap<String, HttpSession> getSessions() {
		return sessions;
	}

	public static int flush() {
		int count = sessions.size();
		for (HttpSession entry : sessions.values()) {
			entry.invalidate();
		}
		sessions.clear();
		SessionPool.flush();
		return count;
	}
}
