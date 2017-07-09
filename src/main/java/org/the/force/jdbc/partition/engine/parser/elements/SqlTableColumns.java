package org.the.force.jdbc.partition.engine.parser.elements;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by xuji on 2017/7/6.
 * 在sql语句中某一个tableSource引用的所有列
 */
public class SqlTableColumns {

    private boolean queryAll;

    private Set<String> columns = new LinkedHashSet<>();

    public boolean isQueryAll() {
        return queryAll;
    }

    public void setQueryAll(boolean queryAll) {
        this.queryAll = queryAll;
    }

    public void addQueriedColumn(String column) {
        columns.add(column.toLowerCase());
    }

    public Set<String> getColumns() {
        return columns;
    }

}
