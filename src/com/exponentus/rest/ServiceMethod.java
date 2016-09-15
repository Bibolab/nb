package com.exponentus.rest;

public class ServiceMethod {
	private String method;
	private String url;
	private boolean isAnonymous;

	public void setMethod(String get) {
		this.method = get;
	}

	public String getMethod() {
		return method;
	}

	public String getURL() {
		return url;
	}

	public void setURL(String example) {
		this.url = example;
	}

	public boolean isAnonymous() {
		return isAnonymous;
	}

	public void setAnonymous(boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}
}