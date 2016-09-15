package com.exponentus.rest;

public class ServiceMethod {
	private String method;
	private String example;

	public void setMethod(String get) {
		this.method = get;
	}

	public String getMethod() {
		return method;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}
}