package com.exponentus.rest;

import java.util.ArrayList;
import java.util.List;

public class ServiceDescriptor {
	private String name;
	private boolean isLoaded = true;
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

}
