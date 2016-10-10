package com.exponentus.dataengine;

import com.exponentus.dataengine.jpa.ViewPage;
import com.exponentus.dataengine.jpadatabase.ftengine.FTEntity;
import com.exponentus.scripting._Session;

public interface IFTIndexEngine<T> {

	void registerTable(FTEntity table);

	@Deprecated
	ViewPage<?> search(String keyWord, _Session ses, int pageNum, int pageSize);

	ViewPage<T> search(Class<T> model, String keyWord, _Session ses, int pageNum, int pageSize);

}
