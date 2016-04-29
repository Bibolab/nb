package com.exponentus.dataengine.jpa.deploying;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import com.exponentus.localization.LanguageCode;
import com.exponentus.localization.Vocabulary;
import com.exponentus.scripting._Session;
import com.exponentus.server.Server;

/**
 * Created by Kayra on 30/12/15.
 */

public abstract class InitialDataAdapter<T, T1> implements IInitialData<T, T1> {

	@Override
	public abstract List<T> getData(_Session ses, LanguageCode lang, Vocabulary vocabulary);

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T1> getDAO() {
		Class<T> persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Class<T1> daoClass = null;
		try {
			daoClass = (Class<T1>) Class.forName(persistentClass.getName().replace("model", "dao") + "DAO");
		} catch (ClassNotFoundException e) {
			Server.logger.errorLogEntry(e);
		}

		return daoClass;
	}

}
