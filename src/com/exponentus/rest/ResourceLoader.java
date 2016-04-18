package com.exponentus.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.exponentus.env.Site;

public class ResourceLoader extends Application {
	private Site appSite;

	public ResourceLoader(Site appSite) {
		super();
		this.appSite = appSite;
	}

	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(SessionService.class);
		for (String clazz : appSite.getRestServices()) {
			try {
				classes.add(Class.forName(clazz));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		return classes;
	}
}
