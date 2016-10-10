package com.exponentus.rest.pojo.view;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by medin on 27.09.16.
 */
public class SortParams {

	private Map<String, Direction> sort = new HashMap<>();

	public static SortParams asc(String fieldName) {
		SortParams sortMap = new SortParams();
		sortMap.addAsc(fieldName);
		return sortMap;
	}

	public static SortParams desc(String fieldName) {
		SortParams sortMap = new SortParams();
		sortMap.addDesc(fieldName);
		return sortMap;
	}

	public SortParams addAsc(String fieldName) {
		if (fieldName == null || fieldName.isEmpty()) {
			throw new IllegalArgumentException("fieldName can not be empty");
		}

		sort.put(fieldName, new Direction(true));
		return this;
	}

	public SortParams addDesc(String fieldName) {
		if (fieldName == null || fieldName.isEmpty()) {
			throw new IllegalArgumentException("fieldName can not be empty");
		}

		sort.put(fieldName, new Direction(false));
		return this;
	}

	public final Map<String, Direction> values() {
		return sort;
	}

	public boolean isEmpty() {
		return sort.isEmpty();
	}

	public class Direction {

		private boolean ascending;

		Direction(boolean isAscending) {
			ascending = isAscending;
		}

		public boolean isAscending() {
			return ascending;
		}
	}
}
