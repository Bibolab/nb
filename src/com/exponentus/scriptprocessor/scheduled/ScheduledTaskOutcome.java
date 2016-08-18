package com.exponentus.scriptprocessor.scheduled;

import com.exponentus.scripting._Session;
import com.exponentus.scriptprocessor.page.InfoMessageType;

public class ScheduledTaskOutcome {
	public String name;
	private InfoMessageType type = InfoMessageType.OK;
	private Exception exception;

	public void setSession(_Session ses) {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(InfoMessageType type) {
		this.type = type;
	}

	public InfoMessageType getType() {
		return type;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

}
