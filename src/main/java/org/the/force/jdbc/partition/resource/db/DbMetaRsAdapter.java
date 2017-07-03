package org.the.force.jdbc.partition.resource.db;

import org.the.force.jdbc.partition.common.ObjectedValue;
import org.the.force.jdbc.partition.resource.resultset.WrappedResultSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuji on 2017/7/3.
 */
public abstract class DbMetaRsAdapter extends WrappedResultSet {

    private Map<String, Integer> indexMap = new HashMap<>();

    private Map<Integer, ObjectedValue<String>> valueMap = new HashMap<>();


    public DbMetaRsAdapter(ResultSet resultSet) {
        super(resultSet);
    }

    public void put(String key, int index) {
        indexMap.put(key, index);
    }

    public void put(Integer key, ObjectedValue<String> value) {
        valueMap.put(key, value);
    }

    public String getString(int columnIndex) throws SQLException {
        ObjectedValue<String> objectedValue = valueMap.get(columnIndex);
        if (objectedValue != null) {
            return objectedValue.getValue();
        }
        return super.getString(columnIndex);
    }
    public String getString(String columnLabel) throws SQLException {
        Integer integer = indexMap.get(columnLabel);
        if (integer == null) {
            return super.getString(columnLabel);
        }
        return getString(integer);
    }

    public Object getObject(String columnLabel) throws SQLException {
        Integer integer = indexMap.get(columnLabel);
        if (integer == null) {
            return super.getObject(columnLabel);
        }
        return getObject(integer);
    }
    public Object getObject(int columnIndex) throws SQLException {
        ObjectedValue<String> objectedValue = valueMap.get(columnIndex);
        if (objectedValue != null) {
            return objectedValue.getValue();
        }
        return super.getObject(columnIndex);
    }
}
