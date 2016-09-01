package com.exponentus.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.exponentus.appenv.AppEnv;
import com.exponentus.dataengine.jpa.AppEntity;
import com.exponentus.env.EnvConst;
import com.exponentus.server.Server;

@Deprecated
public class Util {
	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(EnvConst.DEFAULT_DATETIME_FORMAT);
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat(EnvConst.DEFAULT_TIME_FORMAT);
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat(EnvConst.DEFAULT_DATE_FORMAT);

	public static String convertDataTimeToString(Date date) {
		try {
			return dateTimeFormat.format(date);
		} catch (Exception e) {
			return "null";
		}
	}

	public static String convertDataTimeToStringSilently(Date date) {
		try {
			return dateTimeFormat.format(date);
		} catch (Exception e) {
			return "";
		}
	}

	public static String convertDataTimeToTimeString(Date date) {
		try {
			return timeFormat.format(date);
		} catch (Exception e) {
			AppEnv.logger.errorLogEntry("Util, Unable to convert date to text  " + date);
			// AppEnv.logger.errorLogEntry(e);
			return "null";
		}
	}

	public static Date convertStringToDateTime(String date) {
		try {
			return dateTimeFormat.parse(date);
		} catch (Exception e) {
			AppEnv.logger.errorLogEntry("Util, Unable to convert text to date " + date + ", expected format: " + dateTimeFormat.toPattern());
			return null;
		}
	}

	@Deprecated
	public static String convertDateToStringSilently(Date date) {
		try {
			return dateFormat.format(date);
		} catch (Exception e) {
			return "";
		}
	}

	public static String convertDataToString(Calendar date) {
		try {
			return dateFormat.format(date.getTime());
		} catch (Exception e) {
			AppEnv.logger.errorLogEntry("Util, Unable to convert date to text " + date);
			// AppEnv.logger.errorLogEntry(e);
			return "err date";
		}
	}

	@Deprecated
	/**
	 * Recommendation to use is
	 * com.exponentus.util.TimeUtil.convertStringToDate(String)
	 **/
	public static Date convertStringToDate(String date) {
		try {
			return dateFormat.parse(date);
		} catch (Exception e) {
			AppEnv.logger.errorLogEntry("Util, Unable to convert text to date " + date + ", exepted: " + dateFormat.toPattern());
			// AppEnv.logger.errorLogEntry(e);
			return null;
		}
	}

	@Deprecated
	/**
	 * Recommendation to use is
	 * com.exponentus.util.TimeUtil.convertStringToDate(String)
	 **/
	public static Date convertStringToSimpleDate(String date) {
		if (date != null && !date.trim().equals("")) {
			try {
				return dateFormat.parse(date);
			} catch (Exception e) {
				AppEnv.logger.errorLogEntry("Util, Cannot convert the date to String (date=" + date + "), exepted : " + dateFormat.toPattern());
				return null;
			}
		} else {
			return null;
		}
	}

	public static long convertStringToLong(String d) {
		d = d.replaceAll("\\s+", "").replace(",", "").replace("/\\D/g", "");
		try {
			return Long.parseLong(d);
		} catch (Exception e) {
			return 0;
		}
	}

	public static int convertStringToInt(String d) {
		d = d.replaceAll("\\s+", "").replaceAll(",", "").replaceAll("/\\D/g", "");
		try {
			return Integer.parseInt(d);
		} catch (Exception e) {
			return 0;
		}
	}

	public static int convertStringToInt(String d, int defaultValue) {
		d = d.replaceAll("\\s+", "").replaceAll(",", "").replaceAll("/\\D/g", "");
		try {
			return Integer.parseInt(d);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static float convertStringToFloat(String d) {
		d = d.replaceAll("\\s+", "").replaceAll(",", ".").replaceAll("/\\D/g", "");
		try {
			return Float.parseFloat(d);
		} catch (Exception e) {
			return 0;
		}
	}

	public static int getRandomNumber(int anyNumber) {
		if (anyNumber > 0) {
			Random random = new Random();
			return Math.abs(random.nextInt()) % anyNumber;
		} else {
			return 0;
		}
	}

	public static int generateRandom() {
		Random random = new Random();
		return Math.abs(random.nextInt());
	}

	public static String generateRandomAsText() {
		return Integer.toString(generateRandom());
	}

	public static Object getRndListElement(List<?> list) {
		Random random = new Random();
		int index = random.nextInt(list.size());
		return list.get(index);
	}

	// TODO It can cause StackOvervlow error
	public static String toStringGettersVal(Object clazz) {
		Class<?> noparams[] = {};
		StringBuilder result = new StringBuilder(10000);
		String newLine = System.getProperty("line.separator");

		result.append(clazz.getClass().getName());
		result.append(" Object {");
		result.append(newLine);

		try {
			for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(clazz.getClass()).getPropertyDescriptors()) {
				Method method = propertyDescriptor.getReadMethod();
				if (method != null && !method.getName().equals("getClass")) {
					System.out.println(result);
					result.append(" ");
					result.append(method.getName());
					result.append(": ");
					try {
						String methodValue = "";
						Object val = method.invoke(clazz, noparams);
						if (val != null) {
							if (val instanceof Date) {
								methodValue = Util.dateFormat.format((Date) val);
							} else if (val.getClass().isInstance(AppEntity.class)) {
								methodValue = val.getClass().getCanonicalName();
							} else {
								methodValue = val.toString();
							}
							result.append(methodValue);
						} else {
							result.append("null");
						}
					} catch (Exception e) {
						AppEnv.logger.errorLogEntry(e);
					}
					result.append(newLine);
				}
			}
		} catch (IntrospectionException e) {
			AppEnv.logger.errorLogEntry(e);
		}

		result.append("}");
		return result.toString();
	}

	public static boolean isGroupName(String userID) {
		if (userID != null && userID.length() != 0) {
			return userID.startsWith("[") && userID.endsWith("]");
		}
		return false;
	}

	public static String getFileName(String fn, String tmpFolder) {
		int folderNum = 1;
		File dir = new File(tmpFolder + File.separator + Integer.toString(folderNum));
		while (dir.exists()) {
			folderNum++;
			dir = new File(tmpFolder + File.separator + Integer.toString(folderNum));
		}
		dir.mkdirs();
		fn = dir + File.separator + fn;
		return fn;
	}

	public static File getExistFile(String fn, String tmpFolder) {
		int folderNum = 1;
		File file = null;
		// File file = new File(tmpFolder + File.separator +
		// Integer.toString(folderNum) + File.separator + fn);
		do {
			file = new File(tmpFolder + File.separator + Integer.toString(folderNum) + File.separator + fn);
			folderNum++;

		} while (!file.exists() && folderNum < 20);

		return file;
	}

	public static String convertDataTimeToString(Calendar date) {
		try {
			return dateTimeFormat.format(date.getTime());
		} catch (Exception e) {
			if (date != null) {
				AppEnv.logger.errorLogEntry("Util, Cannot convert the time to String " + date);
			}
			// AppEnv.logger.errorLogEntry(e);
			return "";
		}
	}

	public static double convertBytesToKilobytes(long a) {
		double k = Math.round(a / 1024.0 * 100000.0) / 100000.0;
		return k;

	}

	public static boolean getRandomBoolean() {
		Random random = new Random();
		return random.nextBoolean();
	}

	public static String generateRandomAsText(String setOfTheLetters) {
		return generateRandomAsText(setOfTheLetters, 16);
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

	public static boolean addrIsCorrect(String email) {
		String validate = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		Pattern pattern = Pattern.compile(validate);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}

	public static String getTimeDiffInMilSec(long start_time) {
		long time = System.currentTimeMillis() - start_time;
		int sec = (int) time / 1000;// find seconds
		int msec = (int) time % 1000;// find milliseconds
		return Integer.toString(sec) + "." + Integer.toString(msec);
	}

	public static String getTimeDiffInSec(long start_time) {
		long time = System.currentTimeMillis() - start_time;
		int sec = (int) time / 1000;
		return Long.toString(sec);
	}

	public static boolean pwdIsCorrect(String email) {

		return true;

	}

	public static String readFile(String file) {
		BufferedReader reader = null;
		try {
			// File f = new File(file);
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			StringBuilder stringBuilder = new StringBuilder();
			String ls = System.getProperty("line.separator");

			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

			return stringBuilder.toString();
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

	public static String getAsTagValue(String value) {
		if (value != null && value.length() > 0) {

			if (value.startsWith("<![CDATA[") && value.endsWith("]]>")) {
				return value;
			}

			String val = value.replace("&", "&amp;");
			val = val.replace("\n", "");
			val = val.replace("\"", "'");
			val = val.replace("\r", "");
			val = val.replace("\"", "&quot;");
			val = val.replace("<", "&lt;").replace(">", "&gt;");
			return val;
		} else {
			return "";
		}
	}

}
