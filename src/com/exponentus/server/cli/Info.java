package com.exponentus.server.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.env.ServletSessionPool;
import com.exponentus.scripting._Session;
import com.exponentus.server.Server;
import com.exponentus.util.TimeUtil;

public class Info {

	public static void showServerInfo() {
		System.out.printf(Console.format, "server version", Server.serverVersion);
		System.out.printf(Console.format, "os", System.getProperty("os.version") + "(" + System.getProperty("os.arch") + ")");
		System.out.printf(Console.format, "jvm", System.getProperty("java.version"));
		DateTime now = DateTime.now();
		Minutes mins = Minutes.minutesBetween(new DateTime(Environment.startTime), now);
		System.out.printf(Console.format, "started at",
		        TimeUtil.dateToStringSilently(Environment.startTime) + ", duration=" + TimeUtil.timeConvert(mins.getMinutes()));
		System.out.printf(Console.format, "application server name", EnvConst.APP_ID);
		System.out.printf(Console.format, "server directory", new File("").getAbsolutePath());
		System.out.printf(Console.format, "officeframe directory", Environment.getOfficeFrameDir());
		System.out.printf(Console.format, "database name", EnvConst.DATABASE_NAME);
		System.out.printf(Console.format, "database", Environment.adminApplication.getDataBase().getInfo());
		System.out.printf(Console.format, "web server port", Environment.httpPort);
		System.out.printf(Console.format, "default language", EnvConst.DEFAULT_LANG);
		System.out.printf(Console.format, "session cookie name", EnvConst.AUTH_COOKIE_NAME);
		System.out.printf(Console.format, "languages", Environment.langs);
		File file = new File(File.separator);
		long totalSpace = file.getTotalSpace();
		long freeSpace = file.getFreeSpace();
		System.out.printf(Console.format, "total disk size", totalSpace / 1024 / 1024 / 1024 + " gb");
		System.out.printf(Console.format, "space free", freeSpace / 1024 / 1024 / 1024 + " gb");
		if (Environment.mailEnable) {
			System.out.printf(Console.format, "smtp port", Environment.smtpPort);
			System.out.printf(Console.format, "smtp auth", Environment.smtpAuth);
			System.out.printf(Console.format, "smtp server", Environment.SMTPHost);
			System.out.printf(Console.format, "smtp user", Environment.smtpUser);
			System.out.printf(Console.format, "smtp user name", Environment.smtpUserName);
		} else {
			System.out.printf(Console.format, "mail agent: ", "OFF");
		}
		if (Environment.isDevMode()) {
			System.out.printf(Console.format, "developer mode: ", "ON");
			System.out.printf(Console.format, "external server core folder", Environment.getKernelDir());
			System.out.printf(Console.format, "external " + EnvConst.OFFICEFRAME + " folder", Environment.getOfficeFrameDir());
		} else {
			System.out.printf(Console.format, "developer mode: ", "OFF");
		}
		System.out.printf(Console.format, "temporary files", Environment.fileToDelete.size());
	}

	public static void showDatabaseInfo() {
		System.out.println("database " + Environment.adminApplication.getDataBase().getInfo());
		List<String[]> info = Environment.adminApplication.getDataBase().getCountsOfRec();
		System.out.printf(Console.format, "Table", "Count");
		System.out.printf(Console.format, "--------------", "-----");
		for (String[] entry : info) {
			System.out.printf(Console.format, entry[0], entry[1]);
		}
		System.out.printf(Console.format, "            ", "-----");
		System.out.printf(Console.format, "     Total  ", Environment.adminApplication.getDataBase().getCount());
	}

	public static void showUsersInfo() {
		List<HttpSession> actualUsers = new ArrayList<>();
		for (HttpSession entry : ServletSessionPool.getSessions().values()) {
			try {
				entry.getCreationTime();
				actualUsers.add(entry);
			} catch (IllegalStateException ise) {

			}
		}
		if (actualUsers.size() > 0) {
			System.out.printf(Console.format, "User", "Description");
			System.out.printf(Console.format, "--------------", "-----");
			for (HttpSession entry : actualUsers) {
				String firstVal = "", secondVal = "";
				_Session ses = (_Session) entry.getAttribute(EnvConst.SESSION_ATTR);
				if (ses != null) {
					firstVal = ses.getUser().getLogin();
					secondVal = ses.toString();
				} else {
					firstVal = entry.getId();
				}
				secondVal += ", callingPage=" + entry.getAttribute("callingPage");
				System.out.printf(Console.format, firstVal, secondVal);
			}
			System.out.printf(Console.format, "            ", "-----");
			System.out.printf(Console.format, "     Total  ", actualUsers.size());
		} else {
			System.out.println("There is no user sessions still");
		}
	}
}
