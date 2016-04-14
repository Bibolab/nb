package com.exponentus.dataengine.jpa.deploying;

import java.util.List;

import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;

import kz.flabs.localization.Vocabulary;

/**
 * Created by Kayra on 30/12/15.
 */

public interface IInitialData<T, T1> {
	List<T> getData(_Session ses, LanguageCode lang, Vocabulary vocabulary);

	Class<T1> getDAO();

	String getName();
}
