package com.exponentus.rest;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
	private static List<com.exponentus.rest.ServiceDescriptor> loaded = new ArrayList<com.exponentus.rest.ServiceDescriptor>();
	private static List<String> unsecure = new ArrayList<String>();

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
			ServiceDescriptor descr = new ServiceDescriptor();
			descr.setName(clazz);
			descr.setAppName(appSite.name);
			descr.setUrlMapping(appSite.getRestUrlMapping());
			try {
				Class<?> c = Class.forName(clazz);
				Constructor<?> contructor = c.getConstructor();
				IRestService serv = (IRestService) contructor.newInstance();
				descr = serv.updateDescription(descr);
				descr.setLoaded(true);
				classes.add(c);
				Server.logger.infoLogEntry("REST service class \"" + clazz + "\" loaded");
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
			        | SecurityException e) {
				Server.logger.errorLogEntry(e);
			} catch (ClassNotFoundException e) {
				Server.logger.errorLogEntry(e);
			}
			loaded.add(descr);
			for (ServiceMethod m : descr.getMethods()) {
				if (m.isAnonymous()) {
					unsecure.add(m.getURL());
				}
			}

		}

		return classes;
	}

	public static List<ServiceDescriptor> getLoaded() {
		return loaded;
	}

	public static List<String> getUnsecureMethods() {
		return unsecure;
	}

}
