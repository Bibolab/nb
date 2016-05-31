package com.exponentus.util;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

import com.exponentus.env.EnvConst;

public class NumberUtil {

	public static String formatFloat(float originalCost) {
		return NumberFormat.getInstance(new Locale(EnvConst.DEFAULT_COUNTRY_OF_NUMBER_FORMAT)).format(originalCost);
	}

	public static int getRandomNumber(int low, int high) {
		Random r = new Random();
		int result = r.nextInt(high - low) + low;
		return result;
	}
}
