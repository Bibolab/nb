package com.exponentus.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.exponentus.env.Site;
import com.exponentus.rest.provider.ObjectMapperProvider;
import com.exponentus.server.Server;

public class ResourceLoader extends Application {
	private Site appSite;
	private static List<String> loaded = new ArrayList<String>();

	public ResourceLoader(Site appSite) {
		super();
		this.appSite = appSite;
	}

	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(ObjectMapperProvider.class);
		classes.add(RestProvider.class);
		classes.add(SessionService.class);
		classes.add(ApplicationService.class);
		for (String clazz : appSite.getRestServices()) {
			try {
				Server.logger.infoLogEntry("REST service class \"" + clazz + "\" loaded");
				classes.add(Class.forName(clazz));
			} catch (ClassNotFoundException e) {
				Server.logger.errorLogEntry(e);
			}
			loaded.add(clazz);
		}

		return classes;
	}

	public static List<String> getLoaded() {
		return loaded;
	}

}
