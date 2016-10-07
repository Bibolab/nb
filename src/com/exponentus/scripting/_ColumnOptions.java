package com.exponentus.scripting;

import com.exponentus.scriptprocessor.page.IOutcomeObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by medin on 07.10.16.
 */
public class _ColumnOptions implements IOutcomeObject {

    private List<_ColumnOption> columns;

    public _ColumnOptions() {
        columns = new LinkedList<>();
    }

    public List<_ColumnOption> getColumns() {
        return columns;
    }

    public void add(String name, String value, String type, String sort, String className) {
        columns.add(new _ColumnOption(name, value, type, sort, className));
    }

    class _ColumnOption {

        /**
         * json
         * { name: 'column_name', value: 'model_field', type: 'localizedName', sort: 'desc', className: 'vw-name' }
         */

        String name; // Column name. Caption key or translated
        String value; // Model field
        String type; // Data type. localizedName - for display localizedName value
        String sort; // available sort direction: asc, desc, both
        String className; // css class name

        public _ColumnOption(String name, String value, String type, String sort, String className) {
            if (value == null || value.isEmpty()) {
                throw new IllegalArgumentException("value can not be empty");
            }
            if (sort != null && !sort.isEmpty()) {
                if (!sort.equals("asc") && !sort.equals("desc") && !sort.equals("both")) {
                    throw new IllegalArgumentException("sort direction maybe equal 'asc', 'desc', 'both' or empty for unsorted");
                }
            }

            this.name = name;
            this.value = value;
            this.type = type;
            this.sort = sort;
            this.className = className;
        }
    }
}
