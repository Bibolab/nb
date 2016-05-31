package com.exponentus.webserver.valve;

public class Token {
	private String value;
	private boolean isLimitedToken;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isLimitedToken() {
		return isLimitedToken;
	}

	public void setLimitedToken(boolean isLimitedToken) {
		this.isLimitedToken = isLimitedToken;
	}

	@Override
	public String toString() {
		return "value=" + value + ", isLimited=" + isLimitedToken;
	}
}
