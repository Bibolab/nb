package com.exponentus.dataengine.jpa;

import java.util.List;

import org.eclipse.persistence.exceptions.DatabaseException;

import com.exponentus.exception.SecureException;

public interface IDAO<T, K> {

	T findById(K id);

	T findById(String id);

	ViewPage<? extends IAppEntity> findAllByIds(List<K> ids, int pageNum, int pageSize);

	List<T> findAll();

	Long getCount();

	List<T> findAll(int firstRec, int pageSize);

	T add(T entity) throws DatabaseException, SecureException;

	T update(T entity) throws SecureException;

	void delete(T uuid) throws SecureException;

}
