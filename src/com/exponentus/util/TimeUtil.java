package com.exponentus.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.exponentus.appenv.AppEnv;
import com.exponentus.env.EnvConst;

public class TimeUtil {
	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(EnvConst.DEFAULT_DATETIME_FORMAT);
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat(EnvConst.DEFAULT_DATE_FORMAT);

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
}
