package com.exponentus.scheduler.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.exponentus.env.Environment;
import com.exponentus.log.Log4jLogger;

public class TempFileCleaner implements Job {
	private static ArrayList<String> fileToDelete = new ArrayList<String>();
	protected boolean isFirstStart = true;
	private int ac;
	private Log4jLogger logger = new Log4jLogger("Scheduled");

	public TempFileCleaner() {

	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		// Server.logger.infoLogEntry("start scheduled: " +
		// this.getClass().getSimpleName());
		ac = 0;
		if (isFirstStart) {
			File folder = new File(Environment.tmpDir);
			if (folder.exists()) {
				File[] list = folder.listFiles();
				for (int i = list.length; --i >= 0;) {
					File file = list[i];
					if (!file.getName().equalsIgnoreCase("trash")) {
						delete(file);
					}
				}
			}
			isFirstStart = false;
		} else {
			for (String filePath : fileToDelete) {
				File file = new File(filePath);
				while (file.getParentFile() != null && !file.getParentFile().getName().equals("tmp")) {
					file = file.getParentFile();
				}
				if (file.getParentFile() == null) {
					file = null;
				}
				if (delete(file)) {
					fileToDelete.remove(filePath);
				}
			}
		}
		if (ac > 0) {
			logger.warningLogEntry(ac + " temporary files were deleted by a temp file cleaner task");
		}

	}

	public static void addFileToDelete(String filePath) {
		fileToDelete.add(filePath);
	}

	public boolean delete(File file) {
		if (file == null || !file.exists()) {
			return true;
		}
		Path path = Paths.get(file.getAbsolutePath());
		try {
			BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
			DateTime now = DateTime.now();
			Minutes hours = Minutes.minutesBetween(new DateTime(new Date(attr.creationTime().toMillis())), now);

			if (hours.getMinutes() >= 60) {
				if (file.isDirectory()) {
					for (File f : file.listFiles()) {
						delete(f);
					}
					if (file.delete()) {
						ac++;
						return true;
					}
				} else {
					if (file.delete()) {
						ac++;
						return true;
					}
				}
			}
		} catch (NoSuchFileException e) {
			// no need to inform
		} catch (IOException e) {
			logger.errorLogEntry(e);
		}
		return false;

	}
}
