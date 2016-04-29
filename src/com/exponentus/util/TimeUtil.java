package com.exponentus.util;

public class TimeUtil {
	public static String timeConvert(int time) {
		return time / 24 / 60 + ":" + time / 60 % 24 + ':' + time % 60;
	}
}
