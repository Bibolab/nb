package com.exponentus.scripting;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by medin on 27.09.16.
 */
public class _SortMap {

    private Map<String, _Direction> sort = new HashMap<>();

    public static _SortMap asc(String fieldName) {
        _SortMap sortMap = new _SortMap();
        sortMap.addAsc(fieldName);
        return sortMap;
    }

    public static _SortMap desc(String fieldName) {
        _SortMap sortMap = new _SortMap();
        sortMap.addDesc(fieldName);
        return sortMap;
    }

    public _SortMap addAsc(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            throw new IllegalArgumentException("fieldName can not be empty");
        }

        sort.put(fieldName, new _Direction(true));
        return this;
    }

    public _SortMap addDesc(String fieldName) {
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
