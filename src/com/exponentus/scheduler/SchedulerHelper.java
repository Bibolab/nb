package com.exponentus.scheduler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.reflections.Reflections;

import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting.POJOObjectAdapter;
import com.exponentus.scripting._Session;
import com.exponentus.scripting.event._DoScheduledTask;
import com.exponentus.scriptprocessor.scheduled.IScheduledScript;
import com.exponentus.util.Util;

import administrator.dao.ApplicationDAO;
import administrator.model.Application;

/**
 *
 *
 * @author Kayra created 11-04-2016
 */

public class SchedulerHelper {

	public List<IPOJOObject> getQueue(boolean showConsoleOutput) throws IOException, SchedulerException {
		List<IPOJOObject> objs = new ArrayList<IPOJOObject>();
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();

		for (String groupName : scheduler.getJobGroupNames()) {

			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

				String jobName = jobKey.getName();
				String jobGroup = jobKey.getGroup();

				@SuppressWarnings("unchecked")
				List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
				Trigger t = triggers.get(0);
				String prev = Util.convertDataTimeToString(t.getPreviousFireTime());
				String next = Util.convertDataTimeToString(t.getNextFireTime());

				if (showConsoleOutput) {
					System.out.println("jobName:" + jobName + " group:" + jobGroup + " - previous:" + prev + ", next:" + next);
				}

				objs.add(new POJOObjectAdapter<Object>() {
					@Override
					public String getURL() {
						return "p?id=task-form&amp;docid=" + jobName;

					}

					@Override
					public String getIdentifier() {
						return jobName;
					}

					@Override
					public String getShortXMLChunk(_Session ses) {
						StringBuffer val = new StringBuffer(500);
						val.append("<name>" + jobName + "</name>");
						val.append("<previous>" + prev + "</previous>");
						val.append("<next>" + next + "</next>");
						return val.toString();
					}
				});

			}

		}
		return objs;
	}

	@SuppressWarnings("unchecked")
	public Map<String, ScheduledClass> getAllScheduledTasks(boolean showConsoleOutput) throws IOException {
		Map<String, ScheduledClass> tasks = new HashMap<String, ScheduledClass>();

		ApplicationDAO aDao = new ApplicationDAO();
		List<Application> list = aDao.findAll();
		for (Application app : list) {
			if (app.isOn()) {
				String appName = app.getName();
				String packageName = appName.toLowerCase() + ".scheduled";
				Reflections reflections = new Reflections(packageName);
				Set<Class<? extends _DoScheduledTask>> classes = reflections.getSubTypesOf(_DoScheduledTask.class);
				for (Class<? extends IScheduledScript> initializerClass : classes) {
					ScheduledClass sc = new ScheduledClass();
					sc.appName = appName;
					sc.initializerClass = (Class<IScheduledScript>) initializerClass;
					tasks.put(initializerClass.getName(), sc);
					if (showConsoleOutput) {
						System.out.println(sc.appName + " " + sc.initializerClass.getName());
					}
				}
			}
		}

		if (tasks.size() == 0 && showConsoleOutput) {
			System.out.println("there is no any scheduled tasks on the server");
		}
		return tasks;

	}
}
