package com.exponentus.scriptprocessor.scheduled;

import com.exponentus.scripting._Session;

public interface IScheduledScript {
	void setSession(_Session ses);

	void setOutcome(ScheduledTaskOutcome outcome);

	ScheduledTaskOutcome processCode(ScheduleSchema schema);

	String getName();

}
