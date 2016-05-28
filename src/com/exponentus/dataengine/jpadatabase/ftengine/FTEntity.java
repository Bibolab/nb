package com.exponentus.dataengine.jpadatabase.ftengine;

import java.util.List;
import java.util.UUID;

import com.exponentus.dataengine.jpa.IAppEntity;
import com.exponentus.dataengine.jpa.IDAO;

public class FTEntity {
	private String tableName;
	private List<String> fieldNames;
	private Class<? extends IDAO<? extends IAppEntity, UUID>> daoImpl;

	public FTEntity() {

	}

	@SuppressWarnings("unchecked")
	public FTEntity(String tableName, List<String> fieldNames, String daoImpl) {
		this.tableName = tableName;
		this.fieldNames = fieldNames;
		try {
			this.daoImpl = (Class<? extends IDAO<? extends IAppEntity, UUID>>) Class.forName(daoImpl);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public Class<? extends IDAO<? extends IAppEntity, UUID>> getDaoImpl() {
		return daoImpl;
	}

	public String getTableName() {
		return tableName;
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

}
