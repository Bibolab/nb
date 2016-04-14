package com.exponentus.scripting.event;

import com.exponentus.scripting._Session;
import com.exponentus.scriptprocessor.scheduled.AbstractScheduledTask;

public abstract class _DoScheduledTask extends AbstractScheduledTask {

	@Override
	public abstract void doEvery5Min(_Session session);

	@Override
	public abstract void doEvery1Hour(_Session session);

	@Override
	public abstract void doEveryNight(_Session session);

}
