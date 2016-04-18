package com.exponentus.env;

import java.util.List;

import com.exponentus.rest.RestType;

import kz.flabs.webrule.constants.RunMode;

public class Site {
	public String siteName;
	public String name;

	private RunMode restIsOn = RunMode.OFF;
	private String restUrlMapping = "";
	private RestType restType;
	private List<String> restServices;

	public RunMode getRestIsOn() {
		return restIsOn;
	}

	public void setRestIsOn(RunMode restIsOn) {
		this.restIsOn = restIsOn;
	}

	public String getRestUrlMapping() {
		return restUrlMapping;
	}

	public void setRestUrlMapping(String restUrlMapping) {
		this.restUrlMapping = restUrlMapping;
	}

	public RestType getRestType() {
		return restType;
	}

	public void setRestType(RestType restType) {
		this.restType = restType;
	}

	public List<String> getRestServices() {
		return restServices;
	}

	public void setRestServices(List<String> restServices) {
		this.restServices = restServices;
	}

	@Override
	public String toString() {
		return "name=" + name + ", siteName=" + siteName;
	}
}
