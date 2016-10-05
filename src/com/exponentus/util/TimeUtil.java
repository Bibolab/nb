package com.exponentus.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.exponentus.appenv.AppEnv;
import com.exponentus.env.EnvConst;

public class TimeUtil {
	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(EnvConst.DEFAULT_DATETIME_FORMAT);
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat(EnvConst.DEFAULT_DATE_FORMAT);

	private static final int numPatterns = 7;
	private static Pattern[] datePatterns = new Pattern[numPatterns];
	private static String[] dateFormats = new String[numPatterns];

	public static String timeConvert(int time) {
		return time / 24 / 60 + ":" + time / 60 % 24 + ':' + time % 60;
	}

	public static Date stringToDate(String val) {
		try {
			return dateTimeFormat.parse(val);
		} catch (ParseException e) {
			try {
				return dateFormat.parse(val);
			} catch (ParseException e1) {
				AppEnv.logger
				        .errorLogEntry("Unable convert text to date \"" + val + "\" (supposed format " + dateTimeFormat + " or " + dateFormat + ")");
				return null;
			}
		}
	}

	public static Date stringToDateSilently(String val) {
		try {
			return dateTimeFormat.parse(val);
		} catch (ParseException e) {
			try {
				return dateFormat.parse(val);
			} catch (ParseException e1) {
				return null;
			}
		}
	}

	public static String dateToStringSilently(Date date) {
		try {
			return dateFormat.format(date);
		} catch (Exception e) {
			return "";
		}
	}

	public static String dateTimeToStringSilently(Date date) {
		try {
			return dateTimeFormat.format(date);
		} catch (Exception e) {
			return "";
		}
	}

	public static Date stringToDateWithPassion(String dateParam) {
		boolean flag = true;
		int i = 0;
		Matcher matcher;
		SimpleDateFormat sdf = null;

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
			return null;
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			AppEnv.logger.errorLogEntry(e);
			return null;
		}

	}

	public static String getTimeDiffInMilSec(long start_time) {
		long time = System.currentTimeMillis() - start_time;
		int sec = (int) time / 1000;
		int msec = (int) time % 1000;
		return Integer.toString(sec) + "." + Integer.toString(msec);
	}

	public static void init() {
		datePatterns[0] = Pattern.compile("[0-9]{2}-[0-9]{2}-[0-9]{4}[ ]{1,}[0-9]{2}:[0-9]{2}:[0-9]{2}");
		dateFormats[0] = "dd-MM-yyyy HH:mm:ss";

		datePatterns[1] = Pattern.compile("[0-9]{2}-[0-9]{2}-[0-9]{4}");
		dateFormats[1] = "dd-MM-yyyy";

		datePatterns[2] = Pattern.compile("[0-9]{2}.[0-9]{2}.[0-9]{4}[ ]{1,}[0-9]{2}:[0-9]{2}:[0-9]{2}");
		dateFormats[2] = "dd.MM.yyyy HH:mm:ss";

		datePatterns[3] = Pattern.compile("[0-9]{2}.[0-9]{2}.[0-9]{4}[ ]{1,}[0-9]{2}:[0-9]{2}");
		dateFormats[3] = "dd.MM.yyyy HH:mm";

		datePatterns[4] = Pattern.compile("[0-9]{2}.[0-9]{2}.[0-9]{4}");
		dateFormats[4] = "dd.MM.yyyy";

		datePatterns[5] = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}[ ]{1,}[0-9]{2}:[0-9]{2}:[0-9]{2}");
		dateFormats[5] = "yyyy-MM-dd HH:mm:ss";

		datePatterns[6] = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
		dateFormats[6] = "yyyy-MM-dd";
	}
}
