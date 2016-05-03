package com.exponentus.rule;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;

import com.exponentus.appenv.AppEnv;
import com.exponentus.exception.RuleException;
import com.exponentus.rule.page.PageRule;
import com.exponentus.scripting.IPOJOObject;
import com.exponentus.server.Server;

public class RuleFiles {
	public File rulesDir;

	private ArrayList<File> fileList = new ArrayList<File>();
	private AppEnv appEnv;

	@SuppressWarnings("unchecked")
	public RuleFiles(AppEnv appEnv) {
		this.appEnv = appEnv;
		rulesDir = new File(appEnv.getRulePath() + File.separator + "Page");
		if (rulesDir.isDirectory()) {
			File[] list = rulesDir.listFiles();
			Arrays.sort(list, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
			for (int i = list.length; --i >= 0;) {
				fileList.add(list[i]);
			}
		}
	}

	public List<IPOJOObject> getLogFiles() {
		List<IPOJOObject> objs = new ArrayList<IPOJOObject>();
		for (File element : fileList) {
			try {
				PageRule rule = appEnv.ruleProvider.getRule(FilenameUtils.removeExtension(element.getName()));
				objs.add(rule);
			} catch (RuleException e) {
				Server.logger.errorLogEntry(e);
			}
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
