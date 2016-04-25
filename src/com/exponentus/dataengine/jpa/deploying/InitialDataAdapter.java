package com.exponentus.dataengine.jpa.deploying;

import java.util.List;

import com.exponentus.localization.LanguageCode;
import com.exponentus.localization.Vocabulary;
import com.exponentus.scripting._Session;

/**
 * Created by Kayra on 30/12/15.
 */

public abstract class InitialDataAdapter<T, T1> implements IInitialData<T, T1> {

	@Override
	public abstract List<T> getData(_Session ses, LanguageCode lang, Vocabulary vocabulary);

	@Override
	public abstract Class<T1> getDAO();

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

}
