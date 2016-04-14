package com.exponentus.util;

import java.text.NumberFormat;
import java.util.Locale;

import com.exponentus.env.EnvConst;

public class NumberUtil {
	public static String formatFloat(float originalCost) {
		return NumberFormat.getInstance(new Locale(EnvConst.DEFAULT_COUNTRY_OF_NUMBER_FORMAT)).format(originalCost);
	}
}
