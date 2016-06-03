package com.exponentus.scripting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.exponentus.util.Util;

public class _Helper {

	public static String getDateAsString(Date date) {
		try {
			return Util.dateFormat.format(date);
		} catch (Exception e) {
			return date.toString();
		}
	}

	public static String getDateAsStringSilently(Date date) throws _Exception {
		try {
			return Util.dateTimeFormat.format(date);
		} catch (Exception e) {
			return "";
		}
	}

	public static String getDateAsStringShort() throws _Exception {
		return getDateAsStringShort(new Date());
	}

	public static String getDateAsStringShort(Date date) throws _Exception {
		try {
			return Util.dateFormat.format(date);
		} catch (Exception e) {
			throw new _Exception(_ExceptionType.SCRIPT_ENGINE_ERROR, "Date has not parsed :getDateAsStringShort(" + date + ")");
		}
	}

	public static String getDateAsStringShortSilently(Date date) throws _Exception {
		try {
			return Util.dateFormat.format(date);
		} catch (Exception e) {
			return "";
		}
	}

	public static Date convertStringToDate(String dateParam) throws _Exception {
		int numPatterns = 6;
		Pattern[] datePatterns = new Pattern[numPatterns];
		String[] dateFormats = new String[numPatterns];
		boolean flag = true;
		int i = 0;
		Matcher matcher;
		SimpleDateFormat sdf = null;

		datePatterns[0] = Pattern.compile("[0-9]{2}-[0-9]{2}-[0-9]{4}[ ]{1,}[0-9]{2}:[0-9]{2}:[0-9]{2}");
		dateFormats[0] = "dd-MM-yyyy HH:mm:ss";
		datePatterns[1] = Pattern.compile("[0-9]{2}-[0-9]{2}-[0-9]{4}");
		dateFormats[1] = "dd-MM-yyyy";
		datePatterns[2] = Pattern.compile("[0-9]{2}.[0-9]{2}.[0-9]{4}[ ]{1,}[0-9]{2}:[0-9]{2}:[0-9]{2}");
		dateFormats[2] = "dd.MM.yyyy HH:mm:ss";
		datePatterns[3] = Pattern.compile("[0-9]{2}.[0-9]{2}.[0-9]{4}");
		dateFormats[3] = "dd.MM.yyyy";
		datePatterns[4] = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}[ ]{1,}[0-9]{2}:[0-9]{2}:[0-9]{2}");
		dateFormats[4] = "yyyy-MM-dd HH:mm:ss";
		datePatterns[5] = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
		dateFormats[5] = "yyyy-MM-dd";

		while ((i < numPatterns) && flag) {
			matcher = datePatterns[i].matcher(dateParam);
			if (matcher.matches()) {
				sdf = new SimpleDateFormat(dateFormats[i]);
				flag = false;
			}
			i++;
		}

		try {
			return sdf.parse(dateParam);
		} catch (ParseException e) {
			throw new _Exception(_ExceptionType.FORMDATA_INCORRECT, "Parser error :convertStringToDate(" + dateParam + ")");
		} catch (Exception e) {
			throw new _Exception(_ExceptionType.FORMDATA_INCORRECT, "Error :convertStringToDate(" + dateParam + ")");
		}

	}

	public static String getRandomValue() {
		return Util.generateRandomAsText("QWERTYUIOPASDFGHJKLMNBVCXZ1234567890");
	}

	public static String removeHTMLTags(String content) {
		return Util.removeHTMLTags(content);
	}

	public static int countMaxPage(int colCount, int pageSize) {
		float mp = (float) colCount / (float) pageSize;
		float d = Math.round(mp);

		int maxPage = (int) d;
		if (mp > d) {
			maxPage++;
		}
		if (maxPage < 1) {
			maxPage = 1;
		}
		return maxPage;
	}

}
