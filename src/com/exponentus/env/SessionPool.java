package com.exponentus.env;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.exponentus.scripting._Session;
import com.exponentus.util.NumberUtil;
import com.exponentus.util.StringUtil;

public class SessionPool {
	private static HashMap<String, _Session> userSessions = new HashMap<>();

	public static String put(_Session us) {
		String token = StringUtil.generateRandomAsText("!'*-._qwertyuiopasdfghjklzxcvbnm1234567890", NumberUtil.getRandomNumber(15, 20));
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

	public static Set<String> remove(_Session ses) {
		String tokenToDelete = "";
		Set<String> httpSesToDelete = new HashSet<>();
		httpSesToDelete.add(ses.getJsesId());
		List<_Session> allSes = ses.getAllRelatedSessions();

		for (_Session relSes : allSes) {
			for (Entry<String, _Session> entry : userSessions.entrySet()) {
				if (relSes.getUser().getId() == entry.getValue().getUser().getId()) {
					tokenToDelete = entry.getKey();
					break;
				}
			}
			httpSesToDelete.add(relSes.getJsesId());
		}
		userSessions.remove(tokenToDelete);
		return httpSesToDelete;

	}

	public static HashMap<String, _Session> getUserSessions() {
		return userSessions;
	}

	public static void flush() {
		userSessions.clear();

	}

	public static void remove(List<_Session> allSes) {

	}

}
