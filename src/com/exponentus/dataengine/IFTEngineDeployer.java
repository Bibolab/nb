package com.exponentus.dataengine;

public interface IFTEngineDeployer<T> {

	boolean createIndex(Class<T> model);

	boolean dropIndex(Class<T> model);
}
