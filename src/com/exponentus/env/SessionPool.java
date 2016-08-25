package com.exponentus.env;

import java.util.HashMap;

import com.exponentus.scripting._Session;
import com.exponentus.util.NumberUtil;
import com.exponentus.util.Util;

public class SessionPool {
	private static HashMap<String, _Session> userSessions = new HashMap<String, _Session>();

	public static String put(_Session us) {
		String token = Util.generateRandomAsText("!'*-._qwertyuiopasdfghjklzxcvbnm1234567890", NumberUtil.getRandomNumber(15, 20));
		userSessions.put(token, us);
		return token;
	}

	public static _Session getLoggeedUser(String token) {
		_Session us = userSessions.get(token);
		if (us != null) {
			return us;
		} else {
			return null;
		}
	}

	public static void remove(_Session us) {
		userSessions.remove(us.getUser().getUserID());
	}

	public static void remove(String key) {
		userSessions.remove(key);
	}

	public static HashMap<String, _Session> getUserSessions() {
		return userSessions;
	}

	public static void flush() {
		userSessions.clear();

	}

}
