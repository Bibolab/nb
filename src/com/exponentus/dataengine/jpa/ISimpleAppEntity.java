package com.exponentus.dataengine.jpa;

public interface ISimpleAppEntity<K> {

	void setId(K id);

	K getId();

}
