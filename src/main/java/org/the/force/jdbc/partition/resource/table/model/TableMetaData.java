package org.the.force.jdbc.partition.resource.table.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xuji on 2017/6/7.
 */
public class TableMetaData {

    private final LogicTable logicTable;

    private final Map<String, LogicColumn> columnMap = new LinkedHashMap<>();

    public TableMetaData(LogicTable logicTable) {
        this.logicTable = logicTable;
    }

    public Set<String> getColumns() {
        return columnMap.keySet();
    }

    public Set<String> getPkColumns() {
        return new LinkedHashSet<>();
    }

    public List<Set<String>> getUniqueColumns() {
        return new ArrayList<>();
    }
    
}
