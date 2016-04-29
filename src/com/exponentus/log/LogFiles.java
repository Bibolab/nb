package com.exponentus.log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting.POJOObjectAdapter;
import com.exponentus.scripting._Session;
import com.exponentus.util.Util;

public class LogFiles {
	public File logDir;

	private ArrayList<File> fileList = new ArrayList<File>();
	private String dir;

	@SuppressWarnings("unchecked")
	public LogFiles(String dir) {
		this.dir = dir;
		logDir = new File("." + File.separator + "logs" + File.separator + dir);
		if (logDir.isDirectory()) {
			File[] list = logDir.listFiles();
			Arrays.sort(list, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
			for (int i = list.length; --i >= 0;) {
				fileList.add(list[i]);
			}
		}
	}

	public List<IPOJOObject> getLogFiles() {
		List<IPOJOObject> objs = new ArrayList<IPOJOObject>();
		for (File element : fileList) {
			objs.add(new POJOObjectAdapter<Object>() {
				@Override
				public String getURL() {
					return "p?id=log-form&amp;docid=" + element.getName() + "&amp;category=" + dir;

				}

				@Override
				public String getIdentifier() {
					return element.getName();
				}

				@Override
				public String getShortXMLChunk(_Session ses) {
					StringBuffer val = new StringBuffer(500);
					val.append("<name>" + element.getName() + "</name>");
					val.append("<size>" + element.length() / 1024 + "KB</size>");
					val.append("<lastmodified>" + Util.convertDataTimeToString(new Date(element.lastModified())) + "</lastmodified>");
					return val.toString();
				}
			});
		}
		return objs;
	}

	public List<File> getLogFileList() {
		return fileList;
	}

	public int getCount() {
		return fileList.size();
	}
}
