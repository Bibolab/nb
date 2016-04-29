package com.exponentus.scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import com.exponentus.appenv.AppEnv;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.scripting.IPOJOObject;
import com.exponentus.scripting.POJOObjectAdapter;
import com.exponentus.scripting._Session;
import com.exponentus.scriptprocessor.scheduled.IScheduledScript;
import com.exponentus.server.Server;
import com.exponentus.user.SuperUser;
import com.exponentus.util.ReflectionUtil;
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

	// TODO it need to improve for checking if an application switched off
	public Map<String, IScheduledScript> getAllScheduledTasks(boolean showConsoleOutput) throws IOException {
		ZipInputStream zip = null;
		Map<String, IScheduledScript> tasks = new HashMap<String, IScheduledScript>();
		File jarFile = new File(EnvConst.NB_JAR_FILE);
		if (jarFile.exists()) {
			if (showConsoleOutput) {
				System.out.println("check " + jarFile.getAbsolutePath() + "...");
			}
			zip = new ZipInputStream(new FileInputStream(EnvConst.NB_JAR_FILE));
			for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
				String resource = entry.getName().replace("/", ".");
				for (AppEnv env : Environment.getApplications()) {
					if (!entry.isDirectory() && resource.startsWith(env.appName.toLowerCase() + ".scheduled")) {
						try {
							String name = resource.substring(0, resource.indexOf(".class"));
							Class<?> clazz = Class.forName(name);
							IScheduledScript instance = (IScheduledScript) clazz.newInstance();
							if (instance instanceof IScheduledScript) {
								_Session ses = new _Session(env, new SuperUser());
								instance.setSession(ses);
								tasks.put(name, instance);
								if (showConsoleOutput) {
									System.out.println(env.appName + ":" + name);
								}
							}
						} catch (InstantiationException e) {

						} catch (ClassNotFoundException e) {
							System.out.println(e.getMessage());
						} catch (IllegalAccessException e) {
							System.out.println(e);
						}
					}
				}
			}
		} else {
			if (showConsoleOutput) {
				System.out.println("checking class files...");
			}
			ApplicationDAO aDao = new ApplicationDAO();
			List<Application> list = aDao.findAll();
			for (Application app : list) {
				try {
					Class[] classesList = ReflectionUtil.getClasses(app.getName().toLowerCase() + ".scheduled");
					for (Class<? extends IScheduledScript> taskClass : classesList) {
						if (!taskClass.isInterface() && !Modifier.isAbstract(taskClass.getModifiers())) {
							IScheduledScript pcInstance = null;
							try {
								pcInstance = (IScheduledScript) Class.forName(taskClass.getCanonicalName()).newInstance();
								String name = pcInstance.getName();
								String packageName = taskClass.getPackage().getName();
								String p = packageName.substring(0, packageName.indexOf("."));
								AppEnv env = Environment.getAppEnv(p);
								if (env != null) {
									_Session ses = new _Session(env, new SuperUser());
									pcInstance.setSession(ses);
									tasks.put(name, pcInstance);
									if (showConsoleOutput) {
										System.out.println(env.appName + ":" + taskClass.getCanonicalName());
									}
								} else {
									if (showConsoleOutput) {
										System.out.println("null " + taskClass.getCanonicalName());
									}
								}
							} catch (InstantiationException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							}

						}
					}
				} catch (ClassNotFoundException e) {
					Server.logger.errorLogEntry(e);
				}
			}
		}
		if (tasks.size() == 0 && showConsoleOutput) {
			System.out.println("there is no any scheduled tasks on the server");
		}
		return tasks;
	}
}
