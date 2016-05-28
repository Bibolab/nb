package com.exponentus.scriptprocessor.scheduled;

import java.io.IOException;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.exponentus.scheduler.SchedulerHelper;
import com.exponentus.server.Server;

public class ScheduledTask {
	protected ScheduleSchema schema;

	public ScheduledTaskOutcome processCode(IScheduledScript myObject) throws ClassNotFoundException {
		myObject.setOutcome(new ScheduledTaskOutcome());
		return myObject.processCode(schema);
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			SchedulerHelper sh = new SchedulerHelper();
			for (Class<IScheduledScript> sc : sh.getAllScheduledTasks(false).values()) {
				try {
					processCode(sc.newInstance());
				} catch (InstantiationException | IllegalAccessException e) {
					Server.logger.errorLogEntry(e);
				}

			}

		} catch (ClassNotFoundException | IOException e) {
			Server.logger.errorLogEntry(e);
		}
	}
}
