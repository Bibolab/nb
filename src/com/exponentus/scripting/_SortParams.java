package com.exponentus.scripting;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by medin on 27.09.16.
 */
public class _SortParams {

    private Map<String, _Direction> sort = new HashMap<>();

    public static _SortParams asc(String fieldName) {
        _SortParams sortMap = new _SortParams();
        sortMap.addAsc(fieldName);
        return sortMap;
    }

    public static _SortParams desc(String fieldName) {
        _SortParams sortMap = new _SortParams();
        sortMap.addDesc(fieldName);
        return sortMap;
    }

    public _SortParams addAsc(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            throw new IllegalArgumentException("fieldName can not be empty");
        }

        sort.put(fieldName, new _Direction(true));
        return this;
    }

    public _SortParams addDesc(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            throw new IllegalArgumentException("fieldName can not be empty");
        }

        sort.put(fieldName, new _Direction(false));
        return this;
    }

    public final Map<String, _Direction> values() {
        return sort;
    }

    public boolean isEmpty() {
        return sort.isEmpty();
    }

    public class _Direction {

        private boolean ascending;

        _Direction(boolean isAscending) {
            ascending = isAscending;
        }

        public boolean isAscending() {
            return ascending;
        }
    }
}
