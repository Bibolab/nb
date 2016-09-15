package com.exponentus.rest;

import java.util.ArrayList;
import java.util.List;

public class ServiceDescriptor {
	private String name;
	private String appName;
	private boolean isLoaded;
	private String urlMapping;
	private List<ServiceMethod> serviceMethods = new ArrayList<ServiceMethod>();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public List<ServiceMethod> getMethods() {
		return serviceMethods;
	}

	public void setMethods(List<ServiceMethod> serviceMethods) {
		this.serviceMethods = serviceMethods;
	}

	public void addMethod(ServiceMethod serviceMethod) {
		serviceMethods.add(serviceMethod);
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getUrlMapping() {
		return urlMapping;
	}

	public void setUrlMapping(String urlMapping) {
		this.urlMapping = urlMapping;
	}

}
