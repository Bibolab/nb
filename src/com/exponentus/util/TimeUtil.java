package com.exponentus.util;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.exponentus.appenv.AppEnv;

public class TimeUtil {

	public static String timeConvert(int time) {
		return time / 24 / 60 + ":" + time / 60 % 24 + ':' + time % 60;
	}

	public static Date convertStringToDate(String val) {
		try {
			return DateUtils.parseDate(val, "yyyy", "dd.MM.yy", "dd.MM.yyyy", "dd-MM-yyyy", "dd.MM.yyyy hh:mm", "dd.MM.yyyy hh:mm:ss", "yyyy.MM.dd",
			        "yyyy.MM.dd hh:mm:ss");

		} catch (ParseException e) {
			AppEnv.logger.errorLogEntry("Unable convert text to date \"" + val + "\"");
			return null;
		}
	}

}
