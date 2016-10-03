package com.exponentus.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.catalina.realm.RealmBase;
import org.apache.commons.io.IOUtils;

import com.exponentus.server.Server;

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

	public static int stringToInt(String d, int defaultValue) {
		return NumberUtil.stringToInt(d, defaultValue);
	}

	public static String readResource(String file) {
		InputStreamReader reader = null;
		try {

			InputStream in = Object.class.getClass().getResourceAsStream(file);
			reader = new InputStreamReader(in);
			String myInputStream = IOUtils.toString(reader);

			return myInputStream;
		} catch (FileNotFoundException e) {
			Server.logger.errorLogEntry(e);
		} catch (IOException e) {
			Server.logger.errorLogEntry(e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				Server.logger.errorLogEntry(e);
			}
		}
		return "";
	}
}
