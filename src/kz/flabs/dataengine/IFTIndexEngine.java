package kz.flabs.dataengine;

import com.exponentus.dataengine.jpa.ViewPage;
import com.exponentus.dataengine.jpadatabase.ftengine.FTEntity;
import com.exponentus.scripting._Session;

public interface IFTIndexEngine {

	void registerTable(FTEntity table);

	@SuppressWarnings("rawtypes")
	ViewPage<?> search(String keyWord, _Session ses, int pageNum, int pageSize);

}
