package com.exponentus.dataengine.system;

public interface IExtUserDAO {
	IEmployee getEmployee(long id);

	IEmployee getEmployee(String name);
}
