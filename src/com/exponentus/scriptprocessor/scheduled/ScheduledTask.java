package com.exponentus.scriptprocessor.scheduled;

import java.io.IOException;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.exponentus.env.Environment;
import com.exponentus.scheduler.ScheduledClass;
import com.exponentus.scheduler.SchedulerHelper;
import com.exponentus.scripting._Session;
import com.exponentus.scriptprocessor.page.InfoMessageType;
import com.exponentus.server.Server;
import com.exponentus.user.SuperUser;

public class ScheduledTask {
	protected ScheduleSchema schema;

	public ScheduledTaskOutcome processCode(ScheduledClass sc) throws ClassNotFoundException {
		ScheduledTaskOutcome outcome = new ScheduledTaskOutcome();
		try {
			IScheduledScript myObject = sc.initializerClass.newInstance();
			outcome.setName(myObject.getName());
			myObject.setOutcome(outcome);
			myObject.setSession(new _Session(Environment.getAppEnv(sc.appName), new SuperUser()));

			return myObject.processCode(schema);
		} catch (Exception e) {
			outcome.setException(e);
			outcome.setType(InfoMessageType.SCHEDULED_TASK_ERROR);
			return outcome;
		}
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			SchedulerHelper sh = new SchedulerHelper();
			for (ScheduledClass sc : sh.getAllScheduledTasks(false).values()) {

				ScheduledTaskOutcome outcome = processCode(sc);
				if (outcome.getType() != InfoMessageType.OK) {
					Server.logger.errorLogEntry(outcome.getException());
				}

			}

		} catch (ClassNotFoundException | IOException e) {
			Server.logger.errorLogEntry(e);
		}
	}
}
