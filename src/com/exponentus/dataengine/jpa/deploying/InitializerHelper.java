package com.exponentus.dataengine.jpa.deploying;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.RollbackException;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.reflections.Reflections;

import com.exponentus.appenv.AppEnv;
import com.exponentus.dataengine.jpa.IDAO;
import com.exponentus.dataengine.jpa.ISimpleAppEntity;
import com.exponentus.env.Environment;
import com.exponentus.exception.SecureException;
import com.exponentus.scripting._Session;
import com.exponentus.user.AnonymousUser;

import administrator.dao.ApplicationDAO;
import administrator.model.Application;

/**
 *
 *
 * @author Kayra created 28-12-2015
 */

public class InitializerHelper {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Class<IInitialData>> getAllInitializers(boolean showConsoleOutput) throws IOException {
		Map<String, Class<IInitialData>> inits = new HashMap<String, Class<IInitialData>>();

		ApplicationDAO aDao = new ApplicationDAO();
		List<Application> list = aDao.findAll();
		for (Application app : list) {
			if (app.isOn()) {
				String packageName = app.getName().toLowerCase() + ".init";
				Reflections reflections = new Reflections(packageName);
				Set<Class<? extends InitialDataAdapter>> classes = reflections.getSubTypesOf(InitialDataAdapter.class);
				for (Class<? extends IInitialData> initializerClass : classes) {
					inits.put(initializerClass.getName(), (Class<IInitialData>) initializerClass);
					if (showConsoleOutput) {
						System.out.println(initializerClass.getName());
					}
				}
			}
		}

		if (inits.size() == 0 && showConsoleOutput) {
			System.out.println("there is no any initializer on the Server");
		}
		return inits;

	}

	public String runInitializer(String name, boolean showConsoleOutput) throws DatabaseException, SecureException {
		int count = 0;
		try {
			Class<?> populatingClass = Class.forName(name);
			count = runToPopulate(populatingClass, showConsoleOutput);
		} catch (ClassNotFoundException e) {
			System.out.println("initializer \"" + name + "\" has not found");
		}

		if (showConsoleOutput) {
			System.out.println(count + " records have been added");
		}

		return "";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private int runToPopulate(Class<?> populatingClass, boolean showConsoleOutput) throws DatabaseException, SecureException {
		int count = 0;
		IInitialData<ISimpleAppEntity, IDAO> pcInstance;
		try {
			String packageName = populatingClass.getPackage().getName();
			String p = packageName.substring(0, packageName.indexOf("."));
			AppEnv env = Environment.getAppEnv(p);
			if (env != null) {
				_Session ses = new _Session(env, new AnonymousUser());
				pcInstance = (IInitialData<ISimpleAppEntity, IDAO>) Class.forName(populatingClass.getCanonicalName()).newInstance();
				List<ISimpleAppEntity> entities = pcInstance.getData(ses, null, null);
				Class<?> daoClass = pcInstance.getDAO();
				IDAO dao = getDAOInstance(ses, daoClass);
				if (dao != null) {
					for (ISimpleAppEntity entity : entities) {
						try {
							if (dao.add(entity) != null) {
								if (showConsoleOutput) {
									System.out.println(entity.toString() + " added");
								}
								count++;
							}
						} catch (RollbackException e) {
							// System.out.println(e);
						}
					}
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return count;
	}

	private IDAO<?, ?> getDAOInstance(_Session ses, Class<?> daoClass) {
		@SuppressWarnings("rawtypes")
		Class[] intArgsClass = new Class[] { _Session.class };
		IDAO<?, ?> dao = null;

		try {
			Constructor<?> intArgsConstructor = daoClass.getConstructor(intArgsClass);
			dao = (IDAO<?, ?>) intArgsConstructor.newInstance(new Object[] { ses });
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dao;
	}

}
