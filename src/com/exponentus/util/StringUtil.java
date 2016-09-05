package com.exponentus.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.catalina.realm.RealmBase;

public class StringUtil {
	public static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";

	public static boolean checkByPattren(String value, String p) {
		Pattern pattern = Pattern.compile(p);
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

	public static String encode(String val) {
		return RealmBase.Digest(val, "MD5", "UTF-8");
	}

	public static String getRandomText() {
		return generateRandomAsText("qwertyuiopasdfghjklzxcvbnm", 10);
	}

	public static String generateRandomAsText(String setOfTheLetters, int len) {
		Random r = new Random();
		String key = "";
		char[] letters = new char[setOfTheLetters.length() + 10];

		for (int i = 0; i < 10; i++) {
			letters[i] = Character.forDigit(i, 10);
		}

		for (int i = 0; i < setOfTheLetters.length(); i++) {
			letters[i + 10] = setOfTheLetters.charAt(i);
		}

		for (int i = 0; i < len; i++) {
			key += letters[Math.abs(r.nextInt()) % letters.length];
		}

		return key;
	}
}
