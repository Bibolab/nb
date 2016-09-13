package com.exponentus.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.HttpMethod;

import com.exponentus.scripting.POJOObjectAdapter;

public class ServiceDescriptor extends POJOObjectAdapter<UUID> {
	private String name;
	private boolean isLoaded;
	private List<Method> methods = new ArrayList<Method>();

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

	public List<Method> getMethods() {
		return methods;
	}

	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}

	class Method {
		private String name;

		private HttpMethod method;
		private String example;

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setMethod(HttpMethod method) {
			this.method = method;
		}

		public HttpMethod getMethod() {
			return method;
		}

		public String getExample() {
			return example;
		}

		public void setExample(String example) {
			this.example = example;
		}
	}

}
