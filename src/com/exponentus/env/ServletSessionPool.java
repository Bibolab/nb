package com.exponentus.env;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ServletSessionPool {
	private static HashMap<String, HttpSession> sessions = new HashMap<>();

	public static HttpSession get(HttpServletRequest request) {
		HttpSession jses = request.getSession(true);
		sessions.put(jses.getId(), jses);
		return jses;
	}

	public static HashMap<String, HttpSession> getSessions() {
		return sessions;
	}

	public static boolean resetSessions(String id) {
		HttpSession jses = sessions.get(id);
		if (jses != null) {
			jses.invalidate();
		}
		return true;
	}

	public static int flush() {
		int count = sessions.size();
		for (HttpSession entry : sessions.values()) {
			try {
				entry.getCreationTime();
				entry.invalidate();
			} catch (IllegalStateException ise) {

			}
		}
		sessions.clear();
		SessionPool.flush();
		return count;
	}
}
