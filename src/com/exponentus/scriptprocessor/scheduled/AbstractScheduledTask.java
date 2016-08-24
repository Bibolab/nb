package com.exponentus.scriptprocessor.scheduled;

import com.exponentus.log.Log4jLogger;
import com.exponentus.scripting._Session;
import com.exponentus.scriptprocessor.ScriptHelper;
import com.exponentus.scriptprocessor.page.InfoMessageType;

public abstract class AbstractScheduledTask extends ScriptHelper implements IScheduledScript {
	private _Session ses;
	private ScheduledTaskOutcome outcome;

	protected Log4jLogger logger = new Log4jLogger("Scheduled");

	@Override
	public void setSession(_Session ses) {
		this.ses = ses;

	}

	@Override
	public void setOutcome(ScheduledTaskOutcome outcome) {
		this.outcome = outcome;

	}

	protected void setError(Exception e) {
		outcome.setType(InfoMessageType.SCHEDULED_TASK_ERROR);
		outcome.setException(e);
	}

	@Override
	public ScheduledTaskOutcome processCode(ScheduleSchema schema) {
		switch (schema) {
		case EVERY_5_MIN:
			doEvery5Min(ses);
			break;
		case EVERY_1_HOUR:
			doEvery1Hour(ses);
			break;
		case EVERY_NIGHT:
			doEveryNight(ses);
			break;

		}
		return outcome;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName().toLowerCase();
	}

	public abstract void doEvery5Min(_Session session);

	public abstract void doEvery1Hour(_Session session);

	public abstract void doEveryNight(_Session session);

}
